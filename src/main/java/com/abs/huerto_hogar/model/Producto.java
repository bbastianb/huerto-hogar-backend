package com.abs.huerto_hogar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @Column(length = 10, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 50, nullable = false)
    private String unidad;

    @Column(length = 100, nullable = false)
    private String stock;

    @Column(name = "descripción", columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String img;

}