package com.abs.huerto_hogar.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UsuarioRepository usuarioRepository;

        // Método obligatorio de la interfaz UserDetailsService
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                // Busca un usuario por email en la base de datos
                // Si no lo encuentra, lanza excepción UsernameNotFoundException
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado con email: " + email));

                // Asegura que el rol tenga el prefijo "ROLE_"
                // Si en la BD guardaste "admin" o "usuario", aquí se transforma a "ROLE_admin"
                // o "ROLE_usuario"
                String authority = usuario.getRol().startsWith("ROLE_")
                                ? usuario.getRol() // Si ya empieza con ROLE_, lo deja tal cual
                                : "ROLE_" + usuario.getRol(); // Si no, le agrega el prefijo ROLE_

                // Crea un objeto User (de Spring Security) a partir de Usuario
                // Este User es el que entiende Spring Security internamente
                return new User(
                                usuario.getEmail(), // username -> será el email
                                usuario.getContrasenna(), // password -> la contraseña encriptada (BCrypt)
                                Collections.singletonList( // lista de authorities/roles
                                                new SimpleGrantedAuthority(authority)));
        }
}
