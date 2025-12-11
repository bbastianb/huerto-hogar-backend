package com.abs.huerto_hogar.config;

import java.util.Arrays;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.abs.huerto_hogar.security.JwtAuthenticationEntryPoint;
import com.abs.huerto_hogar.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // Activo la seguridad web de Spring Security
@RequiredArgsConstructor // Lombok me genera un constructor con los campos final
public class SecurityConfig {

        // Inyecto el filtro JWT que procesa el token en cada request
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        // Inyecto mi entry point personalizado para manejar errores 401
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        // Definicion de los endpoints protegidos y configuración general de seguridad
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // Configuro CORS con mi configuración personalizada
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                // Configuro cómo manejar los errores de autenticación (cuando no hay usuario
                                // válido)
                                .exceptionHandling(ex -> ex
                                                // Cuando alguien no está autenticado y quiere entrar a algo protegido,
                                                // delego en mi JwtAuthenticationEntryPoint
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Aquí defino qué rutas son públicas y qué rutas requieren permisos/roles
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-resources/**",
                                                                "/webjars/**")
                                                .permitAll() // A estas rutas puede entrar cualquiera, sin token

                                                .requestMatchers("/api/usuario/guardar").permitAll()
                                                .requestMatchers("/error").permitAll()

                                                // Endpoints públicos de usuario
                                                // Login: el usuario aún no tiene token, así que debe ser público
                                                .requestMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
                                                // Registro de usuario (crear cuenta)
                                                .requestMatchers(HttpMethod.POST, "/api/usuario/guardar").permitAll()
                                                // Recuperación de contraseña
                                                .requestMatchers(HttpMethod.POST, "/api/usuario/recuperar-contrasenna")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/usuario/actualizar-contrasenna")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/usuario/*/foto-perfil")
                                                .permitAll()

                                                // Endpoint público de contacto
                                                // Cualquiera puede enviar un mensaje de contacto
                                                .requestMatchers(HttpMethod.POST, "/api/contacto/crear").permitAll()

                                                // Productos públicos (catálogo)
                                                // Permito ver productos sin estar logueado (solo lectura GET)
                                                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                                                // ENDPOINTS PROTEGIDOS POR ROLES

                                                .requestMatchers(HttpMethod.POST, "/api/orden/guardar")
                                                .hasAnyAuthority("ROLE_usuario", "ROLE_admin")

                                                // Rutas exclusivas para admin

                                                // Solo admin puede crear, editar o eliminar productos
                                                .requestMatchers(HttpMethod.POST, "/api/productos/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.PUT, "/api/productos/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.DELETE, "/api/productos/**")
                                                .hasAuthority("ROLE_admin")

                                                // Solo admin puede ver, actualizar o eliminar órdenes
                                                .requestMatchers(HttpMethod.GET, "/api/orden/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.PUT, "/api/orden/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.DELETE, "/api/orden/**")
                                                .hasAuthority("ROLE_admin")

                                                // Solo admin puede listar, actualizar o eliminar usuarios
                                                .requestMatchers(HttpMethod.GET, "/api/usuario/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.PUT, "/api/usuario/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.DELETE, "/api/usuario/**")
                                                .hasAuthority("ROLE_admin")

                                                // Solo admin puede ver y eliminar mensajes de contacto
                                                .requestMatchers(HttpMethod.GET, "/api/contacto/**")
                                                .hasAuthority("ROLE_admin")
                                                .requestMatchers(HttpMethod.DELETE, "/api/contacto/**")
                                                .hasAuthority("ROLE_admin")

                                                // Cualquier otra ruta que no mencioné arriba requiere estar
                                                // autenticado,
                                                // pero no necesariamente un rol específico (basta con tener un token
                                                // válido)
                                                .anyRequest().authenticated())
                                // Agrego mi filtro JWT antes del filtro de autenticación por username y
                                // password
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                // Devuelvo la configuración construida
                return http.build();
        }

        // Expongo el AuthenticationManager como bean para poder usarlo en
        // servicios/controladores si lo necesito
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                // Le pido a Spring que me entregue el AuthenticationManager configurado
                return config.getAuthenticationManager();
        }

        // Configuración de CORS para permitir que mi frontend (Vite/React) consuma esta
        // API
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Orígenes permitidos (donde corre mi frontend en desarrollo)
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
                // Métodos HTTP permitidos
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                // Cabeceras permitidas (acepto todas con "*")
                configuration.setAllowedHeaders(Arrays.asList("*"));
                // Permito el envío de cookies/credenciales si fuera necesario
                configuration.setAllowCredentials(true);
                // Expongo la cabecera "Authorization" para poder leer el token si hace falta
                configuration.setExposedHeaders(Arrays.asList("Authorization"));

                // Asocio esta configuración a todas las rutas de mi API (/**)
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
