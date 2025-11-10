package com.example.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "entrega_pedido")
public class EntregaPedido {

  @Id
  @Column(name = "order_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "order_id")
  private Order pedido; 

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_entrega", nullable = false)
  private EstadoEntrega estadoEntrega = EstadoEntrega.PENDIENTE;

  @Column(name = "entregado_en")
  private OffsetDateTime entregadoEn;

  @Column(name = "nombre_contacto", length = 120)
  private String nombreContacto;

  @Column(name = "telefono", length = 50)
  private String telefono;

  @Column(name = "direccion_linea1", length = 180, nullable = false)
  private String direccionLinea1;

  @Column(name = "direccion_linea2", length = 180)
  private String direccionLinea2;

  @Column(name = "ciudad", length = 100)
  private String ciudad;

  @Column(name = "departamento", length = 100)
  private String departamento;

  @Column(name = "indicaciones", length = 500)
  private String indicaciones;

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Order getPedido() { return pedido; }
  public void setPedido(Order pedido) { this.pedido = pedido; }

  public EstadoEntrega getEstadoEntrega() { return estadoEntrega; }
  public void setEstadoEntrega(EstadoEntrega estadoEntrega) { this.estadoEntrega = estadoEntrega; }

  public OffsetDateTime getEntregadoEn() { return entregadoEn; }
  public void setEntregadoEn(OffsetDateTime entregadoEn) { this.entregadoEn = entregadoEn; }

  public String getNombreContacto() { return nombreContacto; }
  public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }

  public String getDireccionLinea1() { return direccionLinea1; }
  public void setDireccionLinea1(String direccionLinea1) { this.direccionLinea1 = direccionLinea1; }

  public String getDireccionLinea2() { return direccionLinea2; }
  public void setDireccionLinea2(String direccionLinea2) { this.direccionLinea2 = direccionLinea2; }

  public String getCiudad() { return ciudad; }
  public void setCiudad(String ciudad) { this.ciudad = ciudad; }

  public String getDepartamento() { return departamento; }
  public void setDepartamento(String departamento) { this.departamento = departamento; }

  public String getIndicaciones() { return indicaciones; }
  public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
}
