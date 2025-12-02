package com.abs.huerto_hogar.controller;

import com.abs.huerto_hogar.model.Contacto;
import com.abs.huerto_hogar.service.ContactoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/contacto")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Contacto", description = "Gestión de mensajes enviados desde el formulario de contacto")
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService contactoService) {
        this.service = contactoService;
    }

    // ──────────────────────────────────────────────────────────────
    // LISTAR TODOS
    // ──────────────────────────────────────────────────────────────
    @Operation(summary = "Listar todos los mensajes", 
               description = "Obtiene una lista con todos los mensajes enviados por los usuarios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public List<Contacto> listar() {
        return service.listarTodos();
    }

    // ──────────────────────────────────────────────────────────────
    // OBTENER POR ID
    // ──────────────────────────────────────────────────────────────
    @Operation(summary = "Obtener mensaje por ID", 
               description = "Busca un mensaje almacenado mediante su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensaje encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un mensaje con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Contacto> obtener(@PathVariable Long id) {

        Contacto contacto = service.buscarPorId(id);

        if (contacto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contacto);
    }

    // ──────────────────────────────────────────────────────────────
    // CREAR CONTACTO
    // ──────────────────────────────────────────────────────────────
    @Operation(summary = "Crear un nuevo mensaje de contacto", 
               description = "Registra un nuevo mensaje enviado desde el formulario. "
                           + "Si no se especifica fecha, se asigna automáticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos")
    })
    @PostMapping
    public ResponseEntity<Contacto> crear(@RequestBody Contacto contacto) {

        // Validaciones mínimas
        if (contacto.getNombre() == null || contacto.getNombre().isBlank()
                || contacto.getEmail() == null || contacto.getEmail().isBlank()
                || contacto.getMensaje() == null || contacto.getMensaje().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Asignar fecha si no viene desde el front
        if (contacto.getFechaEnvio() == null) {
            contacto.setFechaEnvio(java.time.LocalDateTime.now());
        }

        Contacto creado = service.guardar(contacto);
        return ResponseEntity.ok(creado);
    }

    // ──────────────────────────────────────────────────────────────
    // ELIMINAR
    // ──────────────────────────────────────────────────────────────
    @Operation(summary = "Eliminar mensaje de contacto", 
               description = "Elimina un mensaje utilizando su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mensaje eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un mensaje con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (service.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }

        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
