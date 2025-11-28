package com.abs.huerto_hogar.controller;

import com.abs.huerto_hogar.model.Producto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.abs.huerto_hogar.service.ProductoService;


import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;

    public ProductoController(ProductoService productoService){
        this.service = productoService;
    }

    @GetMapping
    public List<Producto> listarPro(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable String id){
        Producto producto = service.buscarPorId(id);
        if(producto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        if (producto.getId() == null || producto.getId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Producto creado = service.guardar(producto);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable String id,
            @RequestBody Producto producto
    ) {
        if (service.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        producto.setId(id);
        Producto actualizado = service.guardar(producto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (service.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}