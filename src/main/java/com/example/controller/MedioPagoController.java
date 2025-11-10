package com.example.controller;

import com.example.model.MedioPago;
import com.example.service.MedioPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medios_pago")
public class MedioPagoController {

    @Autowired
    private MedioPagoService medioPagoService;

    @GetMapping
    public List<MedioPago> getAllMediosPago() {
        return medioPagoService.getAllMediosPago();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedioPago> getMedioPagoById(@PathVariable Long id) {
        return medioPagoService.getMedioPagoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MedioPago> createMedioPago(@RequestBody MedioPago medioPago) {
        MedioPago nuevoMedioPago = medioPagoService.createMedioPago(medioPago);
        return ResponseEntity.ok(nuevoMedioPago);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedioPago> updateMedioPago(@PathVariable Long id, @RequestBody MedioPago medioPagoDetalles) {
        MedioPago medioPagoActualizado = medioPagoService.updateMedioPago(id, medioPagoDetalles);
        return ResponseEntity.ok(medioPagoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedioPago(@PathVariable Long id) {
        medioPagoService.deleteMedioPago(id);
        return ResponseEntity.noContent().build();
    }
}
