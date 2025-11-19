package com.abs.huerto_hogar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abs.huerto_hogar.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email); // Verificar si el email existe

    Optional<Usuario> findByEmail(String email); // Obtener usuario por email
}
