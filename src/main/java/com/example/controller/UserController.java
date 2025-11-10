package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.dto.ChangePasswordRequest;
import java.util.Map;



import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Crear un nuevo usuario
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente");
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok("Usuario actualizado exitosamente");
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @PutMapping("/change-password/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden cambiar contraseñas
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada con éxito");
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> payload) {
        
        //System.out.println("Recibido: " + payload);
        String username = payload.get("username");  
        String password = payload.get("password");
    
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"El campo 'username' es obligatorio\"}");
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"El campo 'password' es obligatorio\"}");
        }
        boolean exists = userService.checkPasswordExists(username, password);
        return ResponseEntity.ok().body(exists);
    }
    
    
    

}
