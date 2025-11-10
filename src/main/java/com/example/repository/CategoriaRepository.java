package com.example.repository;

import com.example.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Buscar por slug (para filtros en el landing)
    Optional<Categoria> findBySlug(String slug);

    // Saber si existe una categoría con ese slug
    boolean existsBySlug(String slug);
}
