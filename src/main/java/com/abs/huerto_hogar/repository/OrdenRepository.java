package com.abs.huerto_hogar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abs.huerto_hogar.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

}
