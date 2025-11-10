package com.example.repository;

import com.example.model.Timbrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimbradoRepository extends JpaRepository<Timbrado, Long> {

    // Find active timbrados by document type
    @Query("SELECT t FROM Timbrado t WHERE t.tipoDocumento = :tipoDocumento AND t.estado = 'ACTIVO' AND t.fechaVencimiento > :fechaActual")
    List<Timbrado> findActivosByTipoDocumento(@Param("tipoDocumento") String tipoDocumento, @Param("fechaActual") Date fechaActual);

    // Find by timbrado number
    Optional<Timbrado> findByNumeroTimbrado(String numeroTimbrado);

    // Find all active timbrados
    @Query("SELECT t FROM Timbrado t WHERE t.estado = 'ACTIVO' AND t.fechaVencimiento > :fechaActual")
    List<Timbrado> findAllActivos(@Param("fechaActual") Date fechaActual);

    // Find expired timbrados
    @Query("SELECT t FROM Timbrado t WHERE t.fechaVencimiento <= :fechaActual AND t.estado = 'ACTIVO'")
    List<Timbrado> findVencidos(@Param("fechaActual") Date fechaActual);

    // Check if a number is within the authorized range
    @Query("SELECT t FROM Timbrado t WHERE t.tipoDocumento = :tipoDocumento AND t.estado = 'ACTIVO' " +
           "AND t.fechaVencimiento > :fechaActual AND :numero BETWEEN t.numeracionDesde AND t.numeracionHasta")
    Optional<Timbrado> findByTipoDocumentoAndNumeroEnRango(
        @Param("tipoDocumento") String tipoDocumento, 
        @Param("numero") Long numero, 
        @Param("fechaActual") Date fechaActual
    );

    // Find by RUC
    List<Timbrado> findByRuc(String ruc);
}