package com.example.dto;

import com.example.model.Categoria;

public class CategoriaDTO {

    private Long id;
    private String nombre;
    private String slug;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Long id, String nombre, String slug) {
        this.id = id;
        this.nombre = nombre;
        this.slug = slug;
    }

    // Constructor para mapear directamente desde la entidad
    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
        this.slug = categoria.getSlug();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
