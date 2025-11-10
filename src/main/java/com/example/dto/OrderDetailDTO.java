// src/main/java/com/example/dto/OrderDetailDTO.java
package com.example.dto;

import com.example.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderDetailDTO {
  private Long id;
  private OffsetDateTime createdAt;
  private BigDecimal totalAmount;
  private String currency;
  private OrderStatus status;
  private List<OrderItemDTO> items;

  public OrderDetailDTO() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }
  public List<OrderItemDTO> getItems() { return items; }
  public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
