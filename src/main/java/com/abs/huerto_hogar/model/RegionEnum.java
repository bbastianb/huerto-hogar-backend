package com.abs.huerto_hogar.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegionEnum {

    // Constantes de la tabla "region"
    ARICA_Y_PARINACOTA("Arica y Parinacota"),
    TARAPACA("Tarapacá"),
    ANTOFAGASTA("Antofagasta"),
    ATACAMA("Atacama"),
    COQUIMBO("Coquimbo"),
    VALPARAISO("Valparaíso"),
    METROPOLITANA("Metropolitana de Santiago"),
    O_HIGGINS("Libertador General Bernardo O'Higgins"),
    MAULE("Maule"),
    NUBLE("Ñuble"),
    BIOBIO("Biobío"),
    ARAUCANIA("La Araucanía"),
    LOS_RIOS("Los Ríos"),
    LOS_LAGOS("Los Lagos"),
    AYSEN("Aysén del General Carlos Ibáñez del Campo"),
    MAGALLANES("Magallanes y de la Antártica Chilena");

    private final String nombre;
}
