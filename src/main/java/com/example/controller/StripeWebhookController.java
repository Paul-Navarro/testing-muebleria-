// src/main/java/com/example/controller/StripeWebhookController.java
package com.example.controller;

import com.example.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

@RestController
@RequestMapping("/api/checkout/stripe")
public class StripeWebhookController {
  private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

  private final OrderService orderService;

  @Value("${stripe.webhook-secret}")
  private String endpointSecret;

  @Value("${stripe.verify-signature:true}")   // por defecto true en prod
  private boolean verifySignature;

  private final Gson gson = new Gson();

  public StripeWebhookController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request,
                                                    @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
    String payload = readBody(request);
    log.info("[STRIPE] Webhook recibido. verifySignature={}, signaturePresent={}", verifySignature, (sigHeader != null));

    Event event;
    try {
      if (verifySignature) {
        event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
      } else {
        // DEV: parseo sin verificar firma
        event = gson.fromJson(payload, Event.class);
      }
    } catch (SignatureVerificationException e) {
      log.warn("[STRIPE] Firma inválida: {}", e.getMessage());
      return ResponseEntity.status(400).body("signature verification failed");
    } catch (JsonSyntaxException e) {
      log.warn("[STRIPE] JSON inválido: {}", e.getMessage());
      return ResponseEntity.status(400).body("invalid json");
    } catch (Exception e) {
      log.error("[STRIPE] Error construyendo evento: {}", e.getMessage(), e);
      return ResponseEntity.status(400).body("bad request");
    }

    String eventId = event.getId();
    String eventType = event.getType();
    log.info("[STRIPE] Event recibido: type={}, id={}", eventType, eventId);

    try {
      switch (eventType) {
        case "checkout.session.completed": {
          EventDataObjectDeserializer d = event.getDataObjectDeserializer();
          Session session = (Session) d.getObject().orElse(null);
          if (session == null) {
            log.warn("[STRIPE] No se pudo deserializar Session para event {}", eventId);
            break;
          }
          log.info("[STRIPE] sessionId={}, payment_status={}", session.getId(), session.getPaymentStatus());
          if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
            orderService.confirmPaidAndDecrementStock("STRIPE", session.getId(), eventId);
            log.info("[STRIPE] Orden confirmada y stock descontado para session {}", session.getId());
          } else {
            log.info("[STRIPE] Session no pagada ({}), no se cierra orden", session.getPaymentStatus());
          }
          break;
        }
        case "checkout.session.async_payment_succeeded": {
          EventDataObjectDeserializer d = event.getDataObjectDeserializer();
          Session session = (Session) d.getObject().orElse(null);
          if (session != null) {
            log.info("[STRIPE] async_payment_succeeded: sessionId={}", session.getId());
            orderService.confirmPaidAndDecrementStock("STRIPE", session.getId(), eventId);
          }
          break;
        }
        default:
          log.info("[STRIPE] Evento ignorado: {}", eventType);
      }
    } catch (Exception ex) {
      log.error("[STRIPE] Error procesando event {}: {}", eventId, ex.getMessage(), ex);
      return ResponseEntity.status(500).body("processing error");
    }

    return ResponseEntity.ok("ok");
  }

  private String readBody(HttpServletRequest request) {
    try (BufferedReader reader = request.getReader()) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) sb.append(line);
      return sb.toString();
    } catch (Exception e) {
      log.error("[STRIPE] No se pudo leer el payload: {}", e.getMessage(), e);
      return "";
    }
  }
}
