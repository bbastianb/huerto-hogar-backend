package com.abs.huerto_hogar.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Método que se ejecuta automáticamente cuando ocurre un error de autenticación
    // (por ejemplo: no hay token, token inválido, usuario no autenticado)
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        // Indica que el contenido de la respuesta será JSON
        response.setContentType("application/json");
        // Establece el código HTTP a 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crea un mapa para estructurar el cuerpo de la respuesta de error
        Map<String, Object> error = new HashMap<>();
        // Mensaje amigable para el cliente/front
        error.put("message", "Acceso no autorizado");
        // Mensaje técnico con la descripción de la excepción de autenticación
        error.put("error", authException.getMessage());
        // La ruta a la que el cliente intentó acceder
        error.put("path", request.getServletPath());
        // El código de estado HTTP (401)
        error.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        // Convierte el mapa de error a JSON usando ObjectMapper
        // y lo escribe en el body de la respuesta
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
