package com.example.dto.admin;

import com.example.model.OrderStatus;
import com.example.model.EstadoEntrega;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class AdminPedidoDetalleDTO {

  private Long id;
  private OffsetDateTime fechaCreacion;
  private BigDecimal total;
  private String moneda;
  private OrderStatus estadoPago;

  private EstadoEntrega estadoEntrega;
  private OffsetDateTime entregadoEn;

  // Snapshot de envío / entrega
  private String nombreContacto;
  private String telefono;
  private String direccionLinea1;
  private String direccionLinea2;
  private String ciudad;
  private String departamento;
  private String indicaciones;

  // Cliente
  private Long clienteId;
  private String clienteNombre;
  private String clienteTelefono;
  private String clienteEmail;

  private List<AdminPedidoItemDTO> items;

  public AdminPedidoDetalleDTO() {}

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

  public String getMoneda() {
    return moneda;
  }

  public void setMoneda(String moneda) {
    this.moneda = moneda;
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

  public OffsetDateTime getEntregadoEn() {
    return entregadoEn;
  }

  public void setEntregadoEn(OffsetDateTime entregadoEn) {
    this.entregadoEn = entregadoEn;
  }

  public String getNombreContacto() {
    return nombreContacto;
  }

  public void setNombreContacto(String nombreContacto) {
    this.nombreContacto = nombreContacto;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getDireccionLinea1() {
    return direccionLinea1;
  }

  public void setDireccionLinea1(String direccionLinea1) {
    this.direccionLinea1 = direccionLinea1;
  }

  public String getDireccionLinea2() {
    return direccionLinea2;
  }

  public void setDireccionLinea2(String direccionLinea2) {
    this.direccionLinea2 = direccionLinea2;
  }

  public String getCiudad() {
    return ciudad;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }

  public String getDepartamento() {
    return departamento;
  }

  public void setDepartamento(String departamento) {
    this.departamento = departamento;
  }

  public String getIndicaciones() {
    return indicaciones;
  }

  public void setIndicaciones(String indicaciones) {
    this.indicaciones = indicaciones;
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

  public List<AdminPedidoItemDTO> getItems() {
    return items;
  }

  public void setItems(List<AdminPedidoItemDTO> items) {
    this.items = items;
  }
}
