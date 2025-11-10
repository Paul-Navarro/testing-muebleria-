// src/main/java/com/example/dto/OrderItemDTO.java
package com.example.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
  private Long productId;
  private String productName;
  private Integer qty;
  private BigDecimal unitPrice;

  public OrderItemDTO() {}
  public OrderItemDTO(Long productId, String productName, Integer qty, BigDecimal unitPrice) {
    this.productId = productId; this.productName = productName; this.qty = qty; this.unitPrice = unitPrice;
  }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }
  public Integer getQty() { return qty; }
  public void setQty(Integer qty) { this.qty = qty; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
