package com.example.dto;

import java.util.List;

public class CartRequest {
    private List<Item> items;
    private Factura factura; // ← obligatorio (datos para la factura)
    private Entrega entrega; // ← obligatorio (se valida en el servicio)
    private Boolean isDelivery; // ← true = delivery, false = pickup

    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Factura getFactura() {
        return factura;
    }
    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Entrega getEntrega() {
        return entrega;
    }
    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public Boolean getIsDelivery() {
        return isDelivery;
    }
    public void setIsDelivery(Boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    // ===== Clase interna para los items =====
    public static class Item {
        private Long productId;
        private Integer qty;

        public Long getProductId() {
            return productId;
        }
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        public Integer getQty() {
            return qty;
        }
        public void setQty(Integer qty) {
            this.qty = qty;
        }
    }

    // ===== Snapshot de entrega que viaja desde el front =====
    public static class Entrega {
        private String nombreContacto;   // requerido
        private String telefono;         // requerido
        private String direccionLinea1;  // requerido
        private String direccionLinea2;  // opcional
        private String ciudad;           // requerido
        private String departamento;     // requerido
        private String indicaciones;     // opcional

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

    // ===== Datos de factura que viajan desde el front =====
    public static class Factura {
        private String nombreFactura;   // requerido
        private String rucFactura;      // requerido

        public String getNombreFactura() { return nombreFactura; }
        public void setNombreFactura(String nombreFactura) { this.nombreFactura = nombreFactura; }

        public String getRucFactura() { return rucFactura; }
        public void setRucFactura(String rucFactura) { this.rucFactura = rucFactura; }
    }
}
