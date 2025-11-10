package com.example.dto.admin;

import java.math.BigDecimal;

public class AdminPedidoItemDTO {

  private Long productoId;
  private String productoNombre;
  private Integer cantidad;
  private BigDecimal precioUnitario;

  public AdminPedidoItemDTO() {}

  public AdminPedidoItemDTO(Long productoId, String productoNombre, Integer cantidad, BigDecimal precioUnitario) {
    this.productoId = productoId;
    this.productoNombre = productoNombre;
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
  }

  public Long getProductoId() {
    return productoId;
  }

  public void setProductoId(Long productoId) {
    this.productoId = productoId;
  }

  public String getProductoNombre() {
    return productoNombre;
  }

  public void setProductoNombre(String productoNombre) {
    this.productoNombre = productoNombre;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  public BigDecimal getPrecioUnitario() {
    return precioUnitario;
  }

  public void setPrecioUnitario(BigDecimal precioUnitario) {
    this.precioUnitario = precioUnitario;
  }
}
