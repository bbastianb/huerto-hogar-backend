package com.abs.huerto_hogar.service;

import com.abs.huerto_hogar.model.Producto;
import com.abs.huerto_hogar.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService{
    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo){
        this.repo = repo;
    }

    public List<Producto>listarTodos(){
        return repo.findAll();
    }

    public Producto buscarPorId(String id){
        return repo.findById(id).orElse(null);
    }

    public Producto guardar (Producto producto){
        return repo.save(producto);
    }

    public void eliminar(String id){
        repo.deleteById(id);
    }

    //public List<Producto> listarPorCategoria(String categoria){
      //  return repo.findByCategoria(categoria);}//PUEDO SACARLO

}