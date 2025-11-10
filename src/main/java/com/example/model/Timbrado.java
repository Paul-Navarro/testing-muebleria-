package com.example.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Timbrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroTimbrado;

    private String numeroAutorizacion;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private String tipoDocumento; // FACTURA, RECIBO, CONTRATO, etc.

    private Long numeracionDesde;

    private Long numeracionHasta;

    private String serie;

    private String establecimiento;

    private String puntoEmision;

    @Enumerated(EnumType.STRING)
    private EstadoTimbrado estado;

    private String ruc;

    private String razonSocial;

    private String actividadEconomica;

    // Constructors
    public Timbrado() {}

    public Timbrado(String numeroTimbrado, Date fechaInicio, Date fechaVencimiento, 
                   String tipoDocumento, EstadoTimbrado estado) {
        this.numeroTimbrado = numeroTimbrado;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.tipoDocumento = tipoDocumento;
        this.estado = estado;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTimbrado() {
        return numeroTimbrado;
    }

    public void setNumeroTimbrado(String numeroTimbrado) {
        this.numeroTimbrado = numeroTimbrado;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Long getNumeracionDesde() {
        return numeracionDesde;
    }

    public void setNumeracionDesde(Long numeracionDesde) {
        this.numeracionDesde = numeracionDesde;
    }

    public Long getNumeracionHasta() {
        return numeracionHasta;
    }

    public void setNumeracionHasta(Long numeracionHasta) {
        this.numeracionHasta = numeracionHasta;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoEmision() {
        return puntoEmision;
    }

    public void setPuntoEmision(String puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    public EstadoTimbrado getEstado() {
        return estado;
    }

    public void setEstado(EstadoTimbrado estado) {
        this.estado = estado;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    // Utility method to check if timbrado is active
    public boolean isActivo() {
        return estado == EstadoTimbrado.ACTIVO && 
               fechaVencimiento != null && 
               fechaVencimiento.after(new Date());
    }

    public enum EstadoTimbrado {
        ACTIVO,
        VENCIDO,
        CANCELADO
    }
}