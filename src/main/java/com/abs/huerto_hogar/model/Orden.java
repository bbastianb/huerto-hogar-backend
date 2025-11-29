package com.abs.huerto_hogar.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_orden;

    @Column(nullable = false, length = 10)
    private String fecha_orden;

    @Column(nullable = false, length = 50)
    private String estado_orden;

    @Column(nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(name = "orden_producto", joinColumns = @JoinColumn(name = "id_orden"), inverseJoinColumns = @JoinColumn(name = "id_producto", referencedColumnName = "id"))
    private List<Producto> productos;
}
