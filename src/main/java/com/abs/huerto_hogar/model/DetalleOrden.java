package com.abs.huerto_hogar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_orden")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    @JsonIgnore
    private Orden orden;

    @Column(name = "id_producto") // En texto plano No relacional
    private String idProducto;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "cantidad")
    private Integer cantidad;
}
