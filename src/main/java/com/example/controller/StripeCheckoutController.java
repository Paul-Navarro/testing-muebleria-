// src/main/java/com/example/controller/StripeCheckoutController.java
package com.example.controller;

import com.example.dto.CartRequest;
import com.example.model.Order;
import com.example.model.OrderStatus;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;
import com.example.service.FacturaService;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/checkout")
public class StripeCheckoutController {

  private final OrderService orderService;
  private final OrderRepository orderRepo;
  private final FacturaService facturaService;

  @Value("${stripe.usd-rate}")
  private BigDecimal usdRate; // p.ej. 0.00013

  @Value("${app.frontend-base-url}")
  private String frontendBase; // Ej: http://localhost:3000

  @Value("${app.mediopago.stripe-id:1}")
  private Long medioPagoStripeId;

  private static final Pattern RUC_PATTERN = Pattern.compile("^\\d{5,}-\\d$");

  public StripeCheckoutController(
      OrderService orderService,
      OrderRepository orderRepo,
      FacturaService facturaService
  ) {
    this.orderService = orderService;
    this.orderRepo = orderRepo;
    this.facturaService = facturaService;
  }

  /** Crea la sesión de Stripe y devuelve la URL de Checkout. */
  @PostMapping("/stripe")
  public ResponseEntity<?> createStripeCheckout(
      @RequestBody CartRequest req,
      Principal principal
  ) throws StripeException {
    if (principal == null) {
      return ResponseEntity.status(401).body(Map.of("message", "No autenticado"));
    }
    if (req == null) {
      return badRequest("Solicitud inválida");
    }
    final String username = principal.getName();

    // ===== Validaciones de negocio previas =====
    Map<String, String> fieldErrors = new LinkedHashMap<>();

    // Items
    if (req.getItems() == null || req.getItems().isEmpty()) {
      fieldErrors.put("items", "El carrito está vacío");
    }

    // Factura
    if (req.getFactura() == null || isBlank(req.getFactura().getNombreFactura())) {
      fieldErrors.put("nombreFactura", "Campo requerido");
    }
    String ruc = req.getFactura() != null ? safe(req.getFactura().getRucFactura()) : null;
    if (isBlank(ruc)) {
      fieldErrors.put("rucFactura", "Campo requerido");
    } else if (!RUC_PATTERN.matcher(ruc).matches()) {
      fieldErrors.put("rucFactura", "Formato inválido. Ej: 4813547-0");
    }

    boolean isDelivery = Boolean.TRUE.equals(req.getIsDelivery());

    // Entrega solo si es Delivery
    if (isDelivery) {
      if (req.getEntrega() == null) {
        fieldErrors.put("entrega", "Completá nombre de contacto, teléfono, dirección, ciudad y departamento");
      } else {
        if (isBlank(req.getEntrega().getNombreContacto()))
          fieldErrors.put("nombreContacto", "Campo requerido");
        if (isBlank(req.getEntrega().getTelefono()))
          fieldErrors.put("telefono", "Campo requerido");
        if (isBlank(req.getEntrega().getDireccionLinea1()))
          fieldErrors.put("direccionLinea1", "Campo requerido");
        if (isBlank(req.getEntrega().getCiudad()))
          fieldErrors.put("ciudad", "Campo requerido");
        if (isBlank(req.getEntrega().getDepartamento()))
          fieldErrors.put("departamento", "Campo requerido");
      }
    } else {
      // Pick-up: normalizamos la entrega a null/limpia para no romper lógicas aguas abajo
      if (req.getEntrega() != null) {
        req.getEntrega().setNombreContacto(null);
        req.getEntrega().setTelefono(null);
        req.getEntrega().setDireccionLinea1(null);
        req.getEntrega().setDireccionLinea2(null);
        req.getEntrega().setCiudad(null);
        req.getEntrega().setDepartamento(null);
        req.getEntrega().setIndicaciones(null);
      }
    }

    if (!fieldErrors.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
          "message", "Revisá los campos marcados",
          "fieldErrors", fieldErrors
      ));
    }

    // ===== Crear orden ligada al cliente del usuario =====
    Order order = orderService.createPendingOrderFromCart(username, req);

    // ===== Construir sesión de Stripe =====
    SessionCreateParams.Builder builder = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(frontendBase + "/checkout/success?orderId=" + order.getId()
            + "&session_id={CHECKOUT_SESSION_ID}")
        .setCancelUrl(frontendBase + "/carrito?cancel=true");

    order.getItems().forEach(it -> {
      long unitAmountCents = pygToUsdCents(it.getUnitPrice());
      builder.addLineItem(
          SessionCreateParams.LineItem.builder()
              .setQuantity((long) it.getQty())
              .setPriceData(
                  SessionCreateParams.LineItem.PriceData.builder()
                      .setCurrency("usd")
                      .setUnitAmount(unitAmountCents)
                      .setProductData(
                          SessionCreateParams.LineItem.PriceData.ProductData.builder()
                              .setName(it.getProductName()).build()
                      ).build()
              ).build()
      );
    });

    SessionCreateParams params = builder.build();

    // Idempotencia por intento
    String idemKey = "order-" + order.getId() + "-" + UUID.randomUUID();
    RequestOptions requestOptions = RequestOptions.builder()
        .setIdempotencyKey(idemKey)
        .build();

    Session session = Session.create(params, requestOptions);
    orderService.attachProviderRef(order.getId(), "STRIPE", session.getId());

    return ResponseEntity.ok(Map.of("url", session.getUrl()));
  }

  @PostMapping("/confirm")
  public ResponseEntity<?> confirmPayment(
      @RequestParam("orderId") Long orderId,
      @RequestParam("sessionId") String sessionId
  ) throws StripeException {

    Optional<Order> opt = orderRepo.findById(orderId);
    if (opt.isEmpty()) {
      return badRequest("Orden no encontrada");
    }
    Order order = opt.get();

    if (order.getStatus() == OrderStatus.PAID) {
      return ResponseEntity.ok(Map.of("status", "already_paid"));
    }

    if (order.getProviderRef() == null || !order.getProviderRef().equals(sessionId)) {
      return badRequest("sessionId inválido para esta orden");
    }

    Session session = Session.retrieve(sessionId);
    String paymentStatus = session.getPaymentStatus(); // "paid", "unpaid", ...

    if (!"paid".equalsIgnoreCase(paymentStatus)) {
      return ResponseEntity.status(409).body(Map.of(
          "message", "Stripe aún no marcó como pagada la sesión",
          "stripe_payment_status", paymentStatus
      ));
    }

    orderService.markPaidByProviderRef("STRIPE", sessionId);
    facturaService.crearDesdeOrden(order, medioPagoStripeId);

    return ResponseEntity.ok(Map.of("status", "paid_and_invoiced"));
  }

  // ===== Helpers =====

  private long pygToUsdCents(BigDecimal pyg) {
    if (pyg == null || usdRate == null) return 0L;
    BigDecimal usd = pyg.multiply(usdRate);
    return usd.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  private static String safe(String s) {
    return s == null ? "" : s.trim();
  }

  private ResponseEntity<Map<String, Object>> badRequest(String message) {
    return ResponseEntity.badRequest().body(Map.of("message", message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
    // Evita stacktrace y devuelve un 400 prolijo
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
  }
}
