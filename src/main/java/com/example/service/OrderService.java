// src/main/java/com/example/service/OrderService.java
package com.example.service;

import com.example.dto.CartRequest;
import com.example.model.Cliente;
import com.example.model.EstadoEntrega;
import com.example.model.EntregaPedido;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.OrderStatus;
import com.example.model.StripeEvent;
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.repository.ProductoRepository;
import com.example.repository.StripeEventRepository;
import com.example.repository.UserRepository;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

  private final ProductoRepository productoRepo;
  private final OrderRepository orderRepo;
  private final StripeEventRepository stripeEventRepo;
  private final UserRepository userRepo;

  public OrderService(
      ProductoRepository productoRepo,
      OrderRepository orderRepo,
      StripeEventRepository stripeEventRepo,
      UserRepository userRepo
  ) {
    this.productoRepo = productoRepo;
    this.orderRepo = orderRepo;
    this.stripeEventRepo = stripeEventRepo;
    this.userRepo = userRepo;
  }

  /**
   * Crea una orden en estado PENDING_PAYMENT ligada al Cliente del usuario autenticado.
   * - Si isDelivery = true → exige datos de entrega y guarda snapshot (entrega_pedido).
   * - Si isDelivery = false (Pick-up) → NO exige ni guarda entrega.
   * No descuenta stock aquí (se descuenta al confirmar pago).
   */
  @Transactional
  public Order createPendingOrderFromCart(String username, CartRequest req) {
    if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
      throw new IllegalArgumentException("Carrito vacío");
    }

    // === Validar datos de factura (el Controller ya valida, pero reforzamos) ===
    CartRequest.Factura factura = req.getFactura();
    if (factura == null) {
      throw new IllegalArgumentException("Faltan datos de factura");
    }
    if (isBlank(factura.getNombreFactura()) || isBlank(factura.getRucFactura())) {
      throw new IllegalArgumentException("Completá nombre y RUC para la factura");
    }

    // === Resolver usuario y cliente ===
    User user = userRepo.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    Cliente cliente = user.getCliente();
    if (cliente == null) {
      // Política de negocio: forzar completar perfil si no hay cliente asociado
      throw new IllegalStateException("El usuario no tiene cliente asociado");
    }

    // === Flag de entrega ===
    boolean isDelivery = Boolean.TRUE.equals(req.getIsDelivery());

    // === Si es Delivery, validar snapshot de entrega ===
    CartRequest.Entrega ent = req.getEntrega();
    if (isDelivery) {
      if (ent == null
          || isBlank(ent.getNombreContacto())
          || isBlank(ent.getTelefono())
          || isBlank(ent.getDireccionLinea1())
          || isBlank(ent.getCiudad())
          || isBlank(ent.getDepartamento())) {
        throw new IllegalArgumentException(
            "Completá nombre de contacto, teléfono, dirección, ciudad y departamento");
      }
    } else {
      // Pick-up: ignoramos cualquier dato de entrega que venga
      ent = null;
    }

    // === Crear la orden base ===
    Order order = new Order();
    order.setStatus(OrderStatus.PENDING_PAYMENT);
    order.setCurrency("PYG");
    order.setCliente(cliente); // asociar la orden al cliente

    // Datos de factura (normalizados)
    order.setNombreFactura(factura.getNombreFactura().trim());
    order.setRucFactura(factura.getRucFactura().trim());

    // Tipo de entrega
    order.setIsDelivery(isDelivery);

    // === Ítems y total ===
    List<OrderItem> items = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    for (CartRequest.Item it : req.getItems()) {
      if (it == null || it.getProductId() == null) {
        throw new IllegalArgumentException("Ítem inválido (productId nulo)");
      }
      if (it.getQty() == null || it.getQty() <= 0) {
        throw new IllegalArgumentException("Cantidad inválida para el producto " + it.getProductId());
      }

      var p = productoRepo.findById(it.getProductId())
          .orElseThrow(() ->
              new IllegalArgumentException("Producto no encontrado: " + it.getProductId()));

      // Validación mínima de stock (no reservamos todavía)
      if (p.getStock() < it.getQty()) {
        throw new IllegalArgumentException("Sin stock para " + p.getNombre());
      }

      // Precio (asegurar no nulo)
      BigDecimal unit = (p.getPrecio() == null)
          ? BigDecimal.ZERO
          : BigDecimal.valueOf(p.getPrecio()); // Double -> BigDecimal
      int qty = it.getQty();

      OrderItem oi = new OrderItem();
      oi.setOrder(order);
      oi.setProductId(p.getId());
      oi.setProductName(p.getNombre());
      oi.setUnitPrice(unit);   // PYG
      oi.setQty(qty);

      items.add(oi);
      total = total.add(unit.multiply(BigDecimal.valueOf(qty)));
    }

    order.setItems(items);
    order.setTotalAmount(total);

    // === Snapshot de entrega (solo si Delivery) ===
    if (isDelivery) {
      EntregaPedido entrega = new EntregaPedido();
      entrega.setPedido(order);
      entrega.setEstadoEntrega(EstadoEntrega.PENDIENTE);
      entrega.setNombreContacto(ent.getNombreContacto().trim());
      entrega.setTelefono(ent.getTelefono().trim());
      entrega.setDireccionLinea1(ent.getDireccionLinea1().trim());
      entrega.setDireccionLinea2(trimToNull(ent.getDireccionLinea2()));
      entrega.setCiudad(ent.getCiudad().trim());
      entrega.setDepartamento(ent.getDepartamento().trim());
      entrega.setIndicaciones(trimToNull(ent.getIndicaciones()));
      order.setEntrega(entrega); // requiere @OneToOne(cascade = ALL, optional = true) en Order
    } else {
      order.setEntrega(null);
    }

    return orderRepo.save(order);
  }

  /**
   * Guarda la referencia del proveedor (por ejemplo, Stripe session id).
   */
  @Transactional
  public void attachProviderRef(Long orderId, String provider, String ref) {
    var order = orderRepo.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
    order.setProvider(provider);
    order.setProviderRef(ref);
    orderRepo.save(order);
  }

  /**
   * CONFIRMA pago e impacta stock de manera transaccional e idempotente.
   * Usar desde el webhook del proveedor con eventId único.
   */
  @Transactional
  public void confirmPaidAndDecrementStock(String provider, String providerRef, String stripeEventId) {
    // Idempotencia: si el evento ya fue procesado, salir
    if (stripeEventRepo.existsByEventId(stripeEventId)) {
      return;
    }

    Order order = orderRepo.findByProviderAndProviderRef(provider, providerRef)
        .orElseThrow(() ->
            new IllegalArgumentException("Orden no encontrada para " + provider + " / " + providerRef));

    // Si ya está pagada, sólo registrar el evento
    if (Objects.equals(order.getStatus(), OrderStatus.PAID)) {
      stripeEventRepo.save(new StripeEvent(stripeEventId));
      return;
    }

    // Descontar stock por cada ítem
    for (OrderItem oi : order.getItems()) {
      int affected = productoRepo.decrementStock(oi.getProductId(), oi.getQty());
      if (affected == 0) {
        throw new IllegalStateException("Stock insuficiente o producto inexistente: " + oi.getProductId());
      }
    }

    // Marcar la orden como pagada
    order.setStatus(OrderStatus.PAID);
    orderRepo.save(order);

    // Registrar el evento para idempotencia
    stripeEventRepo.save(new StripeEvent(stripeEventId));
  }

  /**
   * Marca pagada por referencia del proveedor (sin id de evento).
   */
  @Transactional
  public void markPaidByProviderRef(String provider, String providerRef) {
    Order order = orderRepo.findByProviderAndProviderRef(provider, providerRef)
        .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

    if (Objects.equals(order.getStatus(), OrderStatus.PAID)) {
      return; // nada que hacer
    }

    for (OrderItem oi : order.getItems()) {
      int affected = productoRepo.decrementStock(oi.getProductId(), oi.getQty());
      if (affected == 0) {
        throw new IllegalStateException("Stock insuficiente o producto inexistente: " + oi.getProductId());
      }
    }

    order.setStatus(OrderStatus.PAID);
    orderRepo.save(order);
  }

  // ===== helpers =====
  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
  private static String trimToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
