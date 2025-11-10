package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.model.Proveedor;
import com.example.service.ProveedorService;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public List<Proveedor> obtenerTodos() {
        return proveedorService.obtenerTodos();
    }

    @PostMapping
    public Proveedor guardarProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.guardarProveedor(proveedor);
    }

    @GetMapping("/{id}")
    public Proveedor obtenerPorId(@PathVariable Long id) {
        return proveedorService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
    }

    @PutMapping("/{id}")
    public Proveedor actualizarProveedor(@PathVariable Long id, @RequestBody Proveedor proveedorDetalles) {
        Proveedor proveedorExistente = proveedorService.obtenerPorId(id);
        if (proveedorExistente != null) {
            proveedorExistente.setNombre(proveedorDetalles.getNombre());
            proveedorExistente.setDireccion(proveedorDetalles.getDireccion());
            proveedorExistente.setCiudad(proveedorDetalles.getCiudad());
            proveedorExistente.setTelefono(proveedorDetalles.getTelefono());
            proveedorExistente.setEmail(proveedorDetalles.getEmail());
            proveedorExistente.setRuc(proveedorDetalles.getRuc());
            return proveedorService.guardarProveedor(proveedorExistente);
        } else {
            // Manejar el caso en que no se encuentre el proveedor (opcional)
            throw new RuntimeException("Proveedor no encontrado");
        }
    }

}
