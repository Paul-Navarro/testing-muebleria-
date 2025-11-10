package com.example.controller;

import com.example.util.JwtUtil;
import com.example.model.User;
import com.example.model.Cliente;
import com.example.model.Role;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.BadCredentialsException;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Endpoint para iniciar sesión y devolver un token JWT
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
    
            // Tokens (ajusta los tiempos a gusto)
            String accessToken  = jwtUtil.generateToken(loginRequest.getUsername(), 30 * 60 * 1000);
            String refreshToken = jwtUtil.generateToken(loginRequest.getUsername(), 60 * 2 * 1000);
    
            Map<String, Object> res = new HashMap<>();
            res.put("accessToken", accessToken);
            res.put("refreshToken", refreshToken);
    
            return ResponseEntity.ok(res);
    
        } catch (BadCredentialsException e) {
            // Credenciales inválidas
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Usuario o contraseña incorrectos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    
        } catch (AuthenticationException e) {
            // Cualquier otro problema de autenticación
            Map<String, Object> err = new HashMap<>();
            err.put("message", "No se pudo iniciar sesión");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
    }
    

    @GetMapping("/me")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token faltante o no válido");
        }
    
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido");
        }
    
        String username = jwtUtil.getUsernameFromToken(token);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    
        Map<String, Object> res = new HashMap<>();
        res.put("id", user.getId());
        res.put("username", user.getUsername());
        res.put("role", user.getRole().name());
    
        // Si es cliente y está vinculado, devolvemos sus datos
        if (user.getRole() == Role.CLIENT && user.getCliente() != null) {
            Cliente c = user.getCliente();
            res.put("clienteId", c.getId());
            res.put("nombre", c.getNombre());
            res.put("apellido", c.getApellido());
            res.put("fullName", (c.getNombre() + " " + (c.getApellido() != null ? c.getApellido() : "")).trim());
            res.put("email", c.getEmail());
            res.put("telefono", c.getTelefono());
            res.put("cedula", c.getCedula());
        } else {
            // fallback: mostrará username como display name
            res.put("fullName", user.getUsername());
        }
    
        return ResponseEntity.ok(res);
    }
    
    
}

// Clase LoginRequest para encapsular los datos de inicio de sesión
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
