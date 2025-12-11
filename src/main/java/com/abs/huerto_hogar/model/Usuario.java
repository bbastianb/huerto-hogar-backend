package com.abs.huerto_hogar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Clase de entidad para la tabla "usuario"

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String contrasenna;

    @Column(nullable = false, length = 150)
    private String telefono;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(nullable = false, length = 150)
    private String comuna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionEnum region;

    @Column(nullable = false, length = 25)
    private String rol; // "admin" o "usuario"

    @Lob
    @Column(name = "foto_perfil", columnDefinition = "LONGBLOB", nullable = true)
    private byte[] fotoPerfil;
}
