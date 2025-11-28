package com.abs.huerto_hogar.repository;

import com.abs.huerto_hogar.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, String> {

   
   // List<Producto>findByCategoria(String categoria);//PUEDO SACARLO
    
}