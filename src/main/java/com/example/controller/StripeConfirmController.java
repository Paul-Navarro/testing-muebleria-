// src/main/java/com/example/controller/StripeConfirmController.java
package com.example.controller;

import com.example.service.OrderService;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout/stripe")
public class StripeConfirmController {

  private static final Logger log = LoggerFactory.getLogger(StripeConfirmController.class);

  private final OrderService orderService;

  @Value("${stripe.secret-key}")
  private String stripeSecretKey; // sk_test_...

  public StripeConfirmController(OrderService orderService) {
    this.orderService = orderService;
  }

  /**
   * Fallback dev: recibe session_id desde el success_url, consulta a Stripe
   * y si está paid, marca la orden como PAID + descuenta stock.
   */
  @PostMapping("/confirm")
  public ResponseEntity<Map<String, Object>> confirm(@RequestParam("session_id") String sessionId) throws Exception {
    RequestOptions opts = RequestOptions.builder().setApiKey(stripeSecretKey).build();
    Session session = Session.retrieve(sessionId, opts);

    String paymentStatus = session.getPaymentStatus(); // "paid", "unpaid", "no_payment_required", etc.
    log.info("[CONFIRM] session_id={}, payment_status={}", sessionId, paymentStatus);

    if ("paid".equalsIgnoreCase(paymentStatus)) {
      // eventId sintético para idempotencia local
      String eventId = "confirm-" + sessionId;
      orderService.confirmPaidAndDecrementStock("STRIPE", session.getId(), eventId);
      return ResponseEntity.ok(Map.of(
          "status", "PAID",
          "session_id", sessionId
      ));
    }

    // No está pagado todavía (o falló)
    return ResponseEntity.ok(Map.of(
        "status", paymentStatus,
        "session_id", sessionId
    ));
  }
}
