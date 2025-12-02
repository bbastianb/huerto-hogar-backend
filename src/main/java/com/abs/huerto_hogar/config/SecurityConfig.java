package com.abs.huerto_hogar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.abs.huerto_hogar.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // JWT = stateless
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                // ENDPOINTS PÚBLICOS

                                                // Swagger / OpenAPI
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Usuario públicos (registro / login / recuperar / actualizar
                                                // contraseña)
                                                .requestMatchers(
                                                                "/api/usuario/login",
                                                                "/api/usuario/guardar",
                                                                "/api/usuario/recuperar-contrasenna",
                                                                "/api/usuario/actualizar-contrasenna")
                                                .permitAll()

                                                // Contacto público (formulario de contacto)
                                                .requestMatchers(HttpMethod.POST, "/api/contacto/crear").permitAll()

                                                // Catálogo de productos público (solo lectura)
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/productos",
                                                                "/api/productos/buscar/**")
                                                .permitAll()

                                                // RUTAS CLIENTE (rol "usuario")

                                                // Crear orden desde el checkout
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/orden/guardar")
                                                .hasRole("usuario")

                                                // 3. RUTAS ADMIN (rol "admin")

                                                // Gestión de productos
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/productos/crear")
                                                .hasRole("admin")
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/productos/actualizar/**")
                                                .hasRole("admin")
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/productos/eliminar/**")
                                                .hasRole("admin")

                                                // Gestión de órdenes (listar, contar, cambiar estado, eliminar, buscar)
                                                .requestMatchers(
                                                                "/api/orden/actualizar-estado/**",
                                                                "/api/orden/eliminar/**")
                                                .hasRole("admin")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/orden",
                                                                "/api/orden/contar",
                                                                "/api/orden/buscar/**")
                                                .hasRole("admin")

                                                // Gestión de usuarios
                                                .requestMatchers(
                                                                "/api/usuario/eliminar/**",
                                                                "/api/usuario/buscar/**",
                                                                "/api/usuario/contar",
                                                                "/api/usuario/actualizar/**",
                                                                "/api/usuario" // listar usuarios
                                                ).hasRole("admin")

                                                // Gestión de contactos (admin ve y elimina mensajes)
                                                .requestMatchers(
                                                                HttpMethod.GET, "/api/contacto",
                                                                "/api/contacto/buscar/**")
                                                .hasRole("admin")
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/contacto/eliminar/**")
                                                .hasRole("admin")

                                                // 4. Cualquier otra ruta
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }
}
