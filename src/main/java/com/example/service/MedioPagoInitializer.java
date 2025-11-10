// src/main/java/com/example/service/MedioPagoInitializer.java
package com.example.service;

import com.example.model.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MedioPagoInitializer implements CommandLineRunner {

    @Autowired
    private MedioPagoService medioPagoService;

    @Override
    public void run(String... args) throws Exception {
        // Traemos lo existente
        List<MedioPago> existentes = medioPagoService.getAllMediosPago();
        Set<String> tiposExistentes = existentes.stream()
                .map(mp -> Optional.ofNullable(mp.getTipo()).orElse("").trim().toLowerCase())
                .collect(Collectors.toSet());

        // Requeridos por la app
        String[] requeridos = {
                "Tarjeta de Crédito",
                "Tarjeta de Débito",
                "Pago QR",
                "Stripe"                
        };

        List<MedioPago> creadosAhora = new ArrayList<>();

        for (String tipo : requeridos) {
            String key = tipo.trim().toLowerCase();
            if (!tiposExistentes.contains(key)) {
                MedioPago mp = new MedioPago();
                mp.setTipo(tipo);
                MedioPago guardado = medioPagoService.createMedioPago(mp);
                creadosAhora.add(guardado);
                tiposExistentes.add(key);
            }
        }

        if (creadosAhora.isEmpty()) {
            System.out.println("[MedioPagoInitializer] Medios de pago ya existentes. No se crearon nuevos.");
        } else {
            System.out.println("[MedioPagoInitializer] Medios de pago creados: " +
                    creadosAhora.stream()
                            .map(mp -> mp.getTipo() + " (id=" + mp.getId() + ")")
                            .collect(Collectors.joining(", ")));
        }

        // Informar ID de 'Stripe' para configurar app.mediopago.stripe-id
        MedioPago stripe = medioPagoService.getAllMediosPago().stream()
                .filter(mp -> "stripe".equalsIgnoreCase(
                        Optional.ofNullable(mp.getTipo()).orElse("").trim()))
                .findFirst()
                .orElse(null);

        if (stripe != null) {
            System.out.println("[MedioPagoInitializer] Medio de pago 'Stripe' id=" + stripe.getId()
                    + " -> Agrega en application.properties: app.mediopago.stripe-id=" + stripe.getId());
        } else {
            System.out.println("[MedioPagoInitializer] ATENCIÓN: No se encontró 'Stripe'.");
        }
    }
}
