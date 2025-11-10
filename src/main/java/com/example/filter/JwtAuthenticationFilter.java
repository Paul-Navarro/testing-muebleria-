package com.example.filter;

import com.example.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String token = request.getHeader("Authorization");

        System.out.println("[JWT-FILTER] Processing request: " + request.getMethod() + " " + requestURI);
        System.out.println("[JWT-FILTER] Authorization header present: " + (token != null));

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remover el prefijo "Bearer "
            try {
                if (!jwtUtil.validateToken(token)) {
                    throw new RuntimeException("Token no válido o expirado");
                }

                String username = jwtUtil.getUsernameFromToken(token);

                // Configurar la autenticación en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                System.out.println("[JWT-FILTER] Token validation failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token no válido o expirado\", \"status\": 401}");
                response.getWriter().flush();
                return;
            }
        }

        // Continuar con el procesamiento de la solicitud
        filterChain.doFilter(request, response);
    }

}
