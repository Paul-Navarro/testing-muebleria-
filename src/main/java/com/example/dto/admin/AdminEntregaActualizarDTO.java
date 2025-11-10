package com.example.dto.admin;

import com.example.model.EstadoEntrega;
import java.time.OffsetDateTime;

public class AdminEntregaActualizarDTO {

  private EstadoEntrega estadoEntrega;   // PENDIENTE / ENTREGADO / CANCELADO
  private OffsetDateTime entregadoEn;    // opcional: si ENTREGADO y null => se setea en el controller a now()
  private String indicaciones;           // opcional: comentario/nota

  public AdminEntregaActualizarDTO() {}

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

  public String getIndicaciones() {
    return indicaciones;
  }

  public void setIndicaciones(String indicaciones) {
    this.indicaciones = indicaciones;
  }
}
