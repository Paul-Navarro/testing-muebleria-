package com.example.dto;

import com.example.model.Producto;

public class ProductoDetailDTO {
    public Long id;
    public String nombre;
    public String descripcion;
    public Double precio;      
    public Integer stock;       
    public String imagenUrl;
    public String categoriaSlug; 
    public Boolean activo;       

    public ProductoDetailDTO(Producto p) {
        this.id = p.getId();
        this.nombre = p.getNombre();
        this.descripcion = p.getDescripcion();
        this.precio = p.getPrecio();         // Double -> Double ✅
        this.stock = p.getStock();
        this.imagenUrl = p.getImagenUrl();
        if (p.getCategoria() != null) {
            // si tu entidad Categoria tiene getSlug(): usa esto
            // this.categoriaSlug = p.getCategoria().getSlug();

            // si NO tienes slug en Categoria, puedes usar nombre como “slug” temporal:
            this.categoriaSlug = p.getCategoria().getNombre(); // o deja null
        }
        this.activo = p.getActivo();
    }
}
