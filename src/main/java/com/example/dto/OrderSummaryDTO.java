// src/main/java/com/example/dto/OrderSummaryDTO.java
package com.example.dto;

import com.example.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderSummaryDTO {
  private Long id;
  private OffsetDateTime createdAt;
  private BigDecimal totalAmount;
  private OrderStatus status;

  public OrderSummaryDTO() {}
  public OrderSummaryDTO(Long id, OffsetDateTime createdAt, BigDecimal totalAmount, OrderStatus status) {
    this.id = id; this.createdAt = createdAt; this.totalAmount = totalAmount; this.status = status;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }
}
