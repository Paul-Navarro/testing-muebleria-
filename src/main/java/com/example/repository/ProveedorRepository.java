package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
