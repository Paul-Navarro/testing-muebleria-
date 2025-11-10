package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // evita conflicto con palabras reservadas
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Moneda usada internamente (ej: "PYG")
  private String currency;

  // Total de la orden (en PYG)
  private BigDecimal totalAmount;

  // Estado de la orden
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  // Proveedor de pago (ej: STRIPE)
  private String provider;

  // Referencia del proveedor (checkoutSessionId, preferenceId, etc.)
  private String providerRef;

  private OffsetDateTime createdAt = OffsetDateTime.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<OrderItem> items = new ArrayList<>();

  @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
  private EntregaPedido entrega;

  // Datos para la factura
  private String nombreFactura;
  private String rucFactura;

  // Tipo de entrega: true = delivery, false = pickup
  private Boolean isDelivery;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id") 
  private Cliente cliente;

  // ===== Getters y setters =====
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }

  public String getProvider() { return provider; }
  public void setProvider(String provider) { this.provider = provider; }

  public String getProviderRef() { return providerRef; }
  public void setProviderRef(String providerRef) { this.providerRef = providerRef; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }

  public Cliente getCliente() { return cliente; }
  public void setCliente(Cliente cliente) { this.cliente = cliente; }
  
  public EntregaPedido getEntrega() { return entrega; }
  public void setEntrega(EntregaPedido entrega) {
    this.entrega = entrega;
    if (entrega != null) entrega.setPedido(this);
  }

  public String getNombreFactura() { return nombreFactura; }
  public void setNombreFactura(String nombreFactura) { this.nombreFactura = nombreFactura; }

  public String getRucFactura() { return rucFactura; }
  public void setRucFactura(String rucFactura) { this.rucFactura = rucFactura; }

  public Boolean getIsDelivery() { return isDelivery; }
  public void setIsDelivery(Boolean isDelivery) { this.isDelivery = isDelivery; }
}
