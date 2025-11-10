package com.example.repository;

import com.example.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Opcional (para lock):
import org.springframework.data.jpa.repository.Lock;
import javax.persistence.LockModeType;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ------- Listados con categoría (evita N+1) -------
    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findByNombreContainingIgnoreCase(String q, Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findByCategoria_SlugIgnoreCase(String slug, Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findByNombreContainingIgnoreCaseAndCategoria_SlugIgnoreCase(
            String q, String slug, Pageable pageable
    );

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findByDestacadoTrue(Pageable pageable);

    // ------- Conteo de productos por categoría -------
    long countByCategoria_Id(Long categoriaId);

    // ------- Descuento de stock (para confirmar pago) -------
    @Modifying
    @Query("update Producto p set p.stock = p.stock - :qty " +
           "where p.id = :id and p.stock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);

    // ------- (Opcional) Traer con lock para validar antes de crear la orden -------
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);
}
