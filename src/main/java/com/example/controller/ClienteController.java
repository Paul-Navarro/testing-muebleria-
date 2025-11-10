package com.example.controller;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import com.example.model.Cliente;
import com.example.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.dto.ClienteRegistroDTO;
import com.example.repository.ClienteRepository;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.model.User;
import com.example.model.Role;



import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.getAllClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.createCliente(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        return ResponseEntity.ok(clienteService.updateCliente(id, clienteDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registro")
    @Transactional
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteRegistroDTO dto) {
        // 1) Validaciones de unicidad
        if (clienteRepository.existsByCedula(dto.cedula)) {
            return ResponseEntity.badRequest().body("Ya existe un cliente con esa cédula");
        }
        if (clienteRepository.existsByEmail(dto.email)) {
            return ResponseEntity.badRequest().body("Ya existe un cliente con ese email");
        }
        if (userRepository.existsByUsername(dto.username)) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese nombre de usuario");
        }

        // 2) Guardar cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.nombre);
        cliente.setApellido(dto.apellido);
        cliente.setCedula(dto.cedula);
        cliente.setDireccion(dto.direccion);
        cliente.setTelefono(dto.telefono);
        cliente.setEmail(dto.email);
        cliente.setUsername(dto.username);
        clienteRepository.save(cliente);

        // 3) Crear usuario de app y VINCULAR al cliente
        User user = new User();
        user.setUsername(dto.username); // usar username del DTO
        user.setPassword(passwordEncoder.encode(dto.password));
        user.setRole(Role.CLIENT);
        user.setCliente(cliente); // <<-- CLAVE: guardar FK cliente_id en app_user
        userRepository.save(user);

        // 4) Respuesta útil (201 + payload)
        Map<String, Object> res = new HashMap<>();
        res.put("clienteId", cliente.getId());
        res.put("userId", user.getId());
        res.put("username", user.getUsername());
        res.put("role", user.getRole().name());
        res.put("fullName", (cliente.getNombre() + " " + (cliente.getApellido() != null ? cliente.getApellido() : "")).trim());

        return ResponseEntity.status(201).body(res);
    }
}
