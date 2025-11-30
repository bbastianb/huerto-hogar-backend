package com.abs.huerto_hogar.controller;

import com.abs.huerto_hogar.model.Contacto;
import com.abs.huerto_hogar.service.ContactoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacto") // 👈 OJO: singular, coincide con el front
@CrossOrigin(origins = "http://localhost:5173") // cambia si usas otro puerto para Vite
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService contactoService) {
        this.service = contactoService;
    }

    @GetMapping
    public List<Contacto> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contacto> obtener(@PathVariable Long id) {
        Contacto contacto = service.buscarPorId(id);
        if (contacto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contacto);
    }

    @PostMapping
    public ResponseEntity<Contacto> crear(@RequestBody Contacto contacto) {

        // Validaciones mínimas para que no se caiga
        if (contacto.getNombre() == null || contacto.getNombre().isBlank()
                || contacto.getEmail() == null || contacto.getEmail().isBlank()
                || contacto.getMensaje() == null || contacto.getMensaje().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Por si no viene fecha desde el front:
        if (contacto.getFechaEnvio() == null) {
            contacto.setFechaEnvio(java.time.LocalDateTime.now());
        }

        Contacto creado = service.guardar(contacto);
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
