package com.example.dto;

import java.util.Date;
import java.util.List;

public class FacturaDTO {
    private Long id;
    private Date fecha;
    private Long clienteId;
    private String nombreCliente;
    private String apellidoCliente;  
    private String direccionCliente; 
    private String telefonoCliente;  
    private String emailCliente;    
    private String cedulaCliente;    
    private Long medioPagoId;
    private String metodoPago;
    private Long timbradoId;
    private String numeroTimbrado;
    private String timbradoVencimiento;
    private String timbradoEstado;
    private Double subtotal;
    private Double iva;
    private Double total;
    private List<DetalleFacturaDTO> detalles;
    
    // Datos específicos de factura (pueden diferir del cliente)
    private String nombreFactura;
    private String rucFactura;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Long getMedioPagoId() {
        return medioPagoId;
    }

    public void setMedioPagoId(Long medioPagoId) {
        this.medioPagoId = medioPagoId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getSubtotal() { // Getter para subtotal
        return subtotal;
    }

    public void setSubtotal(Double subtotal) { // Setter para subtotal
        this.subtotal = subtotal;
    }

    public Double getIva() { // Getter para IVA
        return iva;
    }

    public void setIva(Double iva) { // Setter para IVA
        this.iva = iva;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }
    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public Long getTimbradoId() {
        return timbradoId;
    }

    public void setTimbradoId(Long timbradoId) {
        this.timbradoId = timbradoId;
    }

    public String getNumeroTimbrado() {
        return numeroTimbrado;
    }

    public void setNumeroTimbrado(String numeroTimbrado) {
        this.numeroTimbrado = numeroTimbrado;
    }

    public String getTimbradoVencimiento() {
        return timbradoVencimiento;
    }

    public void setTimbradoVencimiento(String timbradoVencimiento) {
        this.timbradoVencimiento = timbradoVencimiento;
    }

    public String getTimbradoEstado() {
        return timbradoEstado;
    }

    public void setTimbradoEstado(String timbradoEstado) {
        this.timbradoEstado = timbradoEstado;
    }

    public String getNombreFactura() {
        return nombreFactura;
    }

    public void setNombreFactura(String nombreFactura) {
        this.nombreFactura = nombreFactura;
    }

    public String getRucFactura() {
        return rucFactura;
    }

    public void setRucFactura(String rucFactura) {
        this.rucFactura = rucFactura;
    }
}