// src/main/java/com/example/controller/MyOrdersController.java
package com.example.controller;

import com.example.dto.OrderDetailDTO;
import com.example.dto.OrderItemDTO;
import com.example.dto.OrderSummaryDTO;
import com.example.model.Order;
import com.example.model.OrderStatus; // 🔽 importar
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class MyOrdersController {

  private final OrderRepository orderRepo;
  private final UserRepository userRepo;

  public MyOrdersController(OrderRepository orderRepo, UserRepository userRepo) {
    this.orderRepo = orderRepo;
    this.userRepo = userRepo;
  }

  private Long getClienteIdFromPrincipal(Principal principal) {
    if (principal == null) throw new RuntimeException("No autenticado");
    User u = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    if (u.getCliente() == null) throw new RuntimeException("El usuario no tiene cliente asociado");
    return u.getCliente().getId();
  }

  @GetMapping("/my")
  public ResponseEntity<List<OrderSummaryDTO>> myOrders(Principal principal) {
    Long clienteId = getClienteIdFromPrincipal(principal);

    // 🔽 Solo pedidos PAID del cliente, más recientes primero
    List<Order> orders = orderRepo.findByClienteIdAndStatusOrderByCreatedAtDesc(
        clienteId, OrderStatus.PAID
    );

    List<OrderSummaryDTO> dto = orders.stream()
        .map(o -> new OrderSummaryDTO(o.getId(), o.getCreatedAt(), o.getTotalAmount(), o.getStatus()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> myOrderDetail(@PathVariable Long id, Principal principal) {
    Long clienteId = getClienteIdFromPrincipal(principal);

    Optional<Order> opt = orderRepo.findByIdAndClienteIdAndStatus(id, clienteId, OrderStatus.PAID);
    if (opt.isEmpty()) return ResponseEntity.notFound().build();

    Order o = opt.get();
    OrderDetailDTO dto = new OrderDetailDTO();
    dto.setId(o.getId());
    dto.setCreatedAt(o.getCreatedAt());
    dto.setTotalAmount(o.getTotalAmount());
    dto.setCurrency(o.getCurrency());
    dto.setStatus(o.getStatus());
    dto.setItems(
        o.getItems().stream()
            .map(i -> new OrderItemDTO(i.getProductId(), i.getProductName(), i.getQty(), i.getUnitPrice()))
            .collect(Collectors.toList())
    );
    return ResponseEntity.ok(dto);
  }
}
