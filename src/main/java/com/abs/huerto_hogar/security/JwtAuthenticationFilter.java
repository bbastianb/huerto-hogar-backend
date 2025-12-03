package com.abs.huerto_hogar.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Inyeccion del JwtUtil para trabajar con tokens JWT creación y validación
    private final JwtUtil jwtUtil;

    // Servici UserDetailsService para cargar el usuario por email
    private final CustomUserDetailsService userDetailsService;

    // Este método se ejecuta en CADA request HTTP que llega al backend
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Lee el header "Authorization" de la petición que es la firma del token
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer " no hay token se sigue la
        // cadena y no se autentica a nadie
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No hacemos nada con JWT, dejamos que los demás filtros sigan
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrae el token quitando el prefijo "Bearer " (los primeros 7 caracteres)
            final String jwt = authHeader.substring(7);

            // Usa JwtUtil para sacar el username (email) del token
            final String userEmail = jwtUtil.extractUsername(jwt);

            // Si pudimos sacar un email y todavía no hay autenticación en el contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carga los datos completos del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Verifica que el token sea válido para ese usuario (firma correcta, no
                // expirado, etc.)
                if (jwtUtil.validateToken(jwt, userDetails)) {

                    // Crea un objeto de autenticación de Spring con:
                    // - el usuario (userDetails)
                    // - credenciales null (no se vuelven a usar la contraseña)
                    // - la lista de roles (authorities) del usuario
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Agrega información adicional del request (IP,etc.) al token de
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Guarda este usuario autenticado en el SecurityContext de Spring
                    // A partir de aquí, para esta request, Spring sabe "quién eres" y qué roles
                    // tienes
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si ocurre cualquier error al procesar el token (token malformado, expirado,
            // etc.), lo logueamos
            logger.error("Error procesando JWT: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
