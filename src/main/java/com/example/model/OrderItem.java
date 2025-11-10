package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Producto referenciado
    private Long productId;
    private String productName;

    // Precio unitario (en PYG)
    private BigDecimal unitPrice;

    private Integer qty;

    // ===== Getters y setters =====

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public Long getProductId() { return productId; }

    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQty() { return qty; }

    public void setQty(Integer qty) { this.qty = qty; }
}
