package com.example.service;

import com.example.model.MedioPago;
import com.example.repository.MedioPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedioPagoService {

    @Autowired
    private MedioPagoRepository medioPagoRepository;

    // Obtener todos los medios de pago
    public List<MedioPago> getAllMediosPago() {
        return medioPagoRepository.findAll();
    }

    // Obtener un medio de pago por ID
    public Optional<MedioPago> getMedioPagoById(Long id) {
        return medioPagoRepository.findById(id);
    }

    // Crear un nuevo medio de pago
    public MedioPago createMedioPago(MedioPago medioPago) {
        return medioPagoRepository.save(medioPago);
    }

    // Actualizar un medio de pago existente
    public MedioPago updateMedioPago(Long id, MedioPago medioPagoDetalles) {
        MedioPago medioPagoExistente = medioPagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));

        // Actualizar los campos necesarios
        medioPagoExistente.setTipo(medioPagoDetalles.getTipo());

        return medioPagoRepository.save(medioPagoExistente);
    }

    // Eliminar un medio de pago por ID
    public void deleteMedioPago(Long id) {
        if (!medioPagoRepository.existsById(id)) {
            throw new RuntimeException("Medio de pago no encontrado");
        }
        medioPagoRepository.deleteById(id);
    }
}
