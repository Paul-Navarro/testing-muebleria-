package com.example.dto;

import com.example.model.Producto;

public class ProductoListDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private String categoriaSlug;

    public ProductoListDTO() {}

    public ProductoListDTO(Producto p) {
        this.id = p.getId();
        this.nombre = p.getNombre();
        this.descripcion = p.getDescripcion();
        this.precio = p.getPrecio();
        this.imagenUrl = p.getImagenUrl();
        this.categoriaSlug = (p.getCategoria() != null) ? p.getCategoria().getSlug() : null;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getCategoriaSlug() { return categoriaSlug; }
    public void setCategoriaSlug(String categoriaSlug) { this.categoriaSlug = categoriaSlug; }
}
