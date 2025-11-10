package com.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;
    private final boolean isDevEnvironment;

    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.devEnvironment:false}") boolean isDevEnvironment) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.isDevEnvironment = isDevEnvironment;
    }

    public String generateToken(String username, long duration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + duration))
                .signWith(SECRET_KEY)
                .compact();
    }

    @SuppressWarnings("deprecation")
    public boolean validateToken(String token) {

        try {
            // Valida la firma y obtiene los claims
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);

            // Verifica si el token está expirado
            if (isTokenExpired(token)) {
                return false;
            }

            return true;
        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            // Extrae el nombre de usuario del token
            String tokenUsername = getUsernameFromToken(token);

            // Verifica que el username coincida y que el token no esté expirado
            return tokenUsername.equals(username) && validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // Si hay un error, considera el token como expirado
        }
    }

    public boolean isTokenCloseToExpiration(String token) {
        Date expirationDate = extractExpiration(token);
        long timeLeft = expirationDate.getTime() - System.currentTimeMillis();
        return timeLeft < 10000;
    }
}
