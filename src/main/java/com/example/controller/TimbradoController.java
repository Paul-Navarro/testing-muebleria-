package com.example.controller;

import com.example.model.Timbrado;
import com.example.service.TimbradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/timbrados")
@PreAuthorize("hasRole('ADMIN')")
public class TimbradoController {

    @Autowired
    private TimbradoService timbradoService;

    // Get all timbrados
    @GetMapping
    public ResponseEntity<List<Timbrado>> getAllTimbrados() {
        List<Timbrado> timbrados = timbradoService.getAllTimbrados();
        return new ResponseEntity<>(timbrados, HttpStatus.OK);
    }

    // Get timbrado by ID
    @GetMapping("/{id}")
    public ResponseEntity<Timbrado> getTimbradoById(@PathVariable Long id) {
        Optional<Timbrado> timbrado = timbradoService.getTimbradoById(id);
        if (timbrado.isPresent()) {
            return new ResponseEntity<>(timbrado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get timbrado by number
    @GetMapping("/numero/{numeroTimbrado}")
    public ResponseEntity<Timbrado> getTimbradoByNumber(@PathVariable String numeroTimbrado) {
        Optional<Timbrado> timbrado = timbradoService.getTimbradoByNumber(numeroTimbrado);
        if (timbrado.isPresent()) {
            return new ResponseEntity<>(timbrado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all active timbrados
    @GetMapping("/activos")
    public ResponseEntity<List<Timbrado>> getAllActiveTimbrados() {
        List<Timbrado> activos = timbradoService.getAllActivos();
        return new ResponseEntity<>(activos, HttpStatus.OK);
    }

    // Get active timbrados by document type
    @GetMapping("/activos/{tipoDocumento}")
    public ResponseEntity<List<Timbrado>> getActivosByTipoDocumento(@PathVariable String tipoDocumento) {
        List<Timbrado> activos = timbradoService.getActivosByTipoDocumento(tipoDocumento);
        return new ResponseEntity<>(activos, HttpStatus.OK);
    }

    // Get active timbrado for facturas
    @GetMapping("/factura/activo")
    public ResponseEntity<Timbrado> getActiveTimbradoForFactura() {
        Optional<Timbrado> timbrado = timbradoService.getActiveTimbradoForFactura();
        if (timbrado.isPresent()) {
            return new ResponseEntity<>(timbrado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Create new timbrado
    @PostMapping
    public ResponseEntity<Timbrado> createTimbrado(@RequestBody Timbrado timbrado) {
        try {
            Timbrado savedTimbrado = timbradoService.saveTimbrado(timbrado);
            return new ResponseEntity<>(savedTimbrado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update timbrado
    @PutMapping("/{id}")
    public ResponseEntity<Timbrado> updateTimbrado(@PathVariable Long id, @RequestBody Timbrado timbradoDetails) {
        Optional<Timbrado> existingTimbrado = timbradoService.getTimbradoById(id);
        
        if (existingTimbrado.isPresent()) {
            Timbrado timbrado = existingTimbrado.get();
            
            // Update fields
            timbrado.setNumeroTimbrado(timbradoDetails.getNumeroTimbrado());
            timbrado.setNumeroAutorizacion(timbradoDetails.getNumeroAutorizacion());
            timbrado.setFechaInicio(timbradoDetails.getFechaInicio());
            timbrado.setFechaVencimiento(timbradoDetails.getFechaVencimiento());
            timbrado.setTipoDocumento(timbradoDetails.getTipoDocumento());
            timbrado.setNumeracionDesde(timbradoDetails.getNumeracionDesde());
            timbrado.setNumeracionHasta(timbradoDetails.getNumeracionHasta());
            timbrado.setSerie(timbradoDetails.getSerie());
            timbrado.setEstablecimiento(timbradoDetails.getEstablecimiento());
            timbrado.setPuntoEmision(timbradoDetails.getPuntoEmision());
            timbrado.setEstado(timbradoDetails.getEstado());
            timbrado.setRuc(timbradoDetails.getRuc());
            timbrado.setRazonSocial(timbradoDetails.getRazonSocial());
            timbrado.setActividadEconomica(timbradoDetails.getActividadEconomica());
            
            Timbrado updatedTimbrado = timbradoService.saveTimbrado(timbrado);
            return new ResponseEntity<>(updatedTimbrado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete timbrado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimbrado(@PathVariable Long id) {
        Optional<Timbrado> timbrado = timbradoService.getTimbradoById(id);
        if (timbrado.isPresent()) {
            timbradoService.deleteTimbrado(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Validate factura number
    @GetMapping("/validar/{numeroFactura}")
    public ResponseEntity<Timbrado> validateFacturaNumber(@PathVariable Long numeroFactura) {
        Optional<Timbrado> timbrado = timbradoService.validateFacturaNumber(numeroFactura);
        if (timbrado.isPresent()) {
            return new ResponseEntity<>(timbrado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update expired timbrados
    @PostMapping("/actualizar-vencidos")
    public ResponseEntity<String> updateExpiredTimbrados() {
        try {
            timbradoService.updateExpiredTimbrados();
            return new ResponseEntity<>("Timbrados vencidos actualizados correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar timbrados vencidos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get timbrados by RUC
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<List<Timbrado>> getTimbradosByRuc(@PathVariable String ruc) {
        List<Timbrado> timbrados = timbradoService.getTimbradosByRuc(ruc);
        return new ResponseEntity<>(timbrados, HttpStatus.OK);
    }
}