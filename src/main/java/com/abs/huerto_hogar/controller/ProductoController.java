package com.abs.huerto_hogar.controller;

import com.abs.huerto_hogar.model.Producto;
import com.abs.huerto_hogar.service.ProductoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Swagger OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(
    name = "Productos",
    description = "Gestión de productos: listar, buscar, crear, actualizar y eliminar"
)
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService productoService){
        this.service = productoService;
    }

    // ─────────────────────────────────────────────
    // GET /api/productos
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Listar todos los productos",
        description = "Obtiene una lista con todos los productos disponibles en el sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public List<Producto> listarPro(){
        return service.listarTodos();
    }

    // ─────────────────────────────────────────────
    // GET /api/productos/buscar/{id}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Obtener un producto por ID",
        description = "Busca y devuelve un producto utilizando su identificador único (String)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Producto> buscarProducto(@PathVariable String id){
        Producto producto = service.buscarPorId(id);
        if(producto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    // ─────────────────────────────────────────────
    // POST /api/productos/crear
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Crear un nuevo producto",
        description = "Registra un nuevo producto. Debe enviarse un ID válido dentro del objeto Producto."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado correctamente"),
        @ApiResponse(responseCode = "400", description = "El ID del producto es requerido o inválido")
    })
    @PostMapping("/crear")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        if (producto.getId() == null || producto.getId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Producto creado = service.guardar(producto);
        return ResponseEntity.ok(creado);
    }

    // ─────────────────────────────────────────────
    // PUT /api/productos/actualizar/{id}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Actualizar un producto existente",
        description = "Modifica los datos de un producto usando su ID. "
                    + "El ID del path es el utilizado para actualizar el registro."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizarProducto(
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

    // ─────────────────────────────────────────────
    // DELETE /api/productos/eliminar/{id}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Eliminar un producto",
        description = "Elimina un producto usando su ID único."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String id) {
        if (service.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
