package com.example.service;

import com.example.model.Cliente;
import com.example.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.dto.ClienteRegistroDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente updateCliente(Long id, Cliente clienteDetails) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setNombre(clienteDetails.getNombre());
        cliente.setApellido(clienteDetails.getApellido()); 
        cliente.setDireccion(clienteDetails.getDireccion());
        cliente.setTelefono(clienteDetails.getTelefono());
        cliente.setCedula(clienteDetails.getCedula());
        cliente.setEmail(clienteDetails.getEmail());
        return clienteRepository.save(cliente);
    }

    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }


    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteRegistroDTO dto) {
        if (clienteRepository.existsByCedula(dto.cedula)) {
            return ResponseEntity.badRequest().body("Ya existe un cliente con esa cédula");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.nombre);
        cliente.setApellido(dto.apellido);
        cliente.setCedula(dto.cedula);
        cliente.setDireccion(dto.direccion);
        cliente.setTelefono(dto.telefono);
        cliente.setEmail(dto.email);

        clienteRepository.save(cliente);
        return ResponseEntity.ok("Cliente registrado correctamente");
    }
}
