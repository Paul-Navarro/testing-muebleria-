package com.example;

import com.example.util.JwtUtil;

public class JwtUtilTest {

    public static void main(String[] args) {
        JwtUtil jwtUtil = new JwtUtil("mi_secreto_aleatorio_de_32_caracteres", true);

        // Generar un token de prueba
        String testUsername = "admin";
        String testToken = jwtUtil.generateToken(testUsername, 1000 * 60 * 10); // 10 minutos de duración

        // Validar token
        boolean isValid = jwtUtil.validateToken(testToken);
        System.out.println("Token válido: " + isValid); // Debería ser true

        // Obtener el username desde el token
        String extractedUsername = jwtUtil.getUsernameFromToken(testToken);
        System.out.println("Nombre de usuario extraído: " + extractedUsername); // Debería ser "admin"

        // Comprobar expiración del token
        boolean isExpired = jwtUtil.isTokenExpired(testToken);
        System.out.println("Token expirado: " + isExpired); // Debería ser false
    }
}
