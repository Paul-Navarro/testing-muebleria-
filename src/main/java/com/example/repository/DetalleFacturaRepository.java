package com.example.repository;

import com.example.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
    
    @Query("SELECT COUNT(df) FROM DetalleFactura df WHERE df.producto.id = :productoId")
    long countByProducto_Id(@Param("productoId") Long productoId);
}