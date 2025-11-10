package com.example.dto;

import com.example.model.Role;

public class UserDTO {
    @jakarta.validation.constraints.NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @jakarta.validation.constraints.NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private Role role;

    // Getters y setters
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
