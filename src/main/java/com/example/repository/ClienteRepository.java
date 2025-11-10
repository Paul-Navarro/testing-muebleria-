package com.example.repository;

import com.example.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Verificar si ya existe un cliente con determinada cédula
    boolean existsByCedula(String cedula);

    // Verificar si ya existe un cliente con determinado email
    boolean existsByEmail(String email);

    // Buscar cliente por cédula
    Optional<Cliente> findByCedula(String cedula);

    // Buscar cliente por email
    Optional<Cliente> findByEmail(String email);
}
