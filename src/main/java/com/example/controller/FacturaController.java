package com.example.controller;

import com.example.dto.FacturaDTO;
import com.example.model.Factura;
import com.example.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        List<Factura> facturas = facturaService.getAllFacturas();
        List<FacturaDTO> facturaDTOs = facturas.stream()
            .map(facturaService::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(facturaDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable Long id) {
        Factura factura = facturaService.getFacturaById(id);
        FacturaDTO facturaDTO = facturaService.convertToDTO(factura);
        return ResponseEntity.ok(facturaDTO);
    }

    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@RequestBody Factura factura) {
        Factura nuevaFactura = facturaService.createFactura(factura);
        FacturaDTO facturaDTO = facturaService.convertToDTO(nuevaFactura);
        return ResponseEntity.status(201).body(facturaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(@PathVariable Long id, @RequestBody Factura facturaDetalles) {
        Factura facturaActualizada = facturaService.updateFactura(id, facturaDetalles);
        FacturaDTO facturaDTO = facturaService.convertToDTO(facturaActualizada);
        return ResponseEntity.ok(facturaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long id) {
        facturaService.deleteFactura(id);
        return ResponseEntity.noContent().build();
    }
}
