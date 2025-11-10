package com.example.dto.admin;

import com.example.model.OrderStatus;
import com.example.model.EstadoEntrega;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AdminPedidoResumenDTO {

  private Long id;
  private OffsetDateTime fechaCreacion;
  private BigDecimal total;
  private OrderStatus estadoPago;      // ej: PAID, PENDING, etc.
  private EstadoEntrega estadoEntrega; // PENDIENTE / ENTREGADO / CANCELADO

  // Datos básicos del cliente (para listado)
  private Long clienteId;
  private String clienteNombre;
  private String clienteTelefono;
  private String clienteEmail;

  // Tipo de entrega: true = delivery, false = pickup
  private Boolean isDelivery;

  public AdminPedidoResumenDTO() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OffsetDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(OffsetDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public OrderStatus getEstadoPago() {
    return estadoPago;
  }

  public void setEstadoPago(OrderStatus estadoPago) {
    this.estadoPago = estadoPago;
  }

  public EstadoEntrega getEstadoEntrega() {
    return estadoEntrega;
  }

  public void setEstadoEntrega(EstadoEntrega estadoEntrega) {
    this.estadoEntrega = estadoEntrega;
  }

  public Long getClienteId() {
    return clienteId;
  }

  public void setClienteId(Long clienteId) {
    this.clienteId = clienteId;
  }

  public String getClienteNombre() {
    return clienteNombre;
  }

  public void setClienteNombre(String clienteNombre) {
    this.clienteNombre = clienteNombre;
  }

  public String getClienteTelefono() {
    return clienteTelefono;
  }

  public void setClienteTelefono(String clienteTelefono) {
    this.clienteTelefono = clienteTelefono;
  }

  public String getClienteEmail() {
    return clienteEmail;
  }

  public void setClienteEmail(String clienteEmail) {
    this.clienteEmail = clienteEmail;
  }

  public Boolean getIsDelivery() {
    return isDelivery;
  }

  public void setIsDelivery(Boolean isDelivery) {
    this.isDelivery = isDelivery;
  }
}
