package com.abs.huerto_hogar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abs.huerto_hogar.model.Orden;
import com.abs.huerto_hogar.service.OrdenService;

// Swagger OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/orden")
@Tag(
    name = "Órdenes",
    description = "Gestión de órdenes de compra y actualización de su estado de envío"
)
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // ─────────────────────────────────────────────
    // POST /api/orden/guardar
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Crear una nueva orden",
        description = "Crea una orden asociada a un usuario existente. "
                    + "Debes enviar el objeto Orden con el id del usuario dentro de 'usuario.id'."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o falta el id de usuario"),
        @ApiResponse(responseCode = "500", description = "Error interno al crear la orden")
    })
    @PostMapping("/guardar")
    public Orden guardarOrden(@RequestBody Orden orden) {

        if (orden.getUsuario() == null || orden.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Debes enviar el id del usuario en la orden.");
        }

        Long usuarioId = orden.getUsuario().getId();
        return ordenService.saveOrden(orden, usuarioId);
    }

    // ─────────────────────────────────────────────
    // PUT /api/orden/actualizar-estado/{idOrden}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Actualizar estado de envío de una orden",
        description = "Actualiza el campo 'estado' de una orden. "
                    + "El nuevo estado se envía en el body como { \"estado\": \"NUEVO_ESTADO\" }."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de la orden actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido o datos incorrectos"),
        @ApiResponse(responseCode = "404", description = "No se encontró una orden con ese id"),
        @ApiResponse(responseCode = "500", description = "Error interno al actualizar el estado")
    })
    @PutMapping("/actualizar-estado/{idOrden}")
    public Orden actualizarEstadoEnvio(
            @PathVariable Long idOrden,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");
        return ordenService.updateEstadoEnvio(idOrden, nuevoEstado);
    }

    // ─────────────────────────────────────────────
    // DELETE /api/orden/eliminar/{idOrden}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Eliminar una orden",
        description = "Elimina una orden usando su id. Retorna la orden eliminada."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró una orden con ese id"),
        @ApiResponse(responseCode = "500", description = "Error interno al eliminar la orden")
    })
    @DeleteMapping("/eliminar/{idOrden}")
    public Orden eliminarOrden(@PathVariable Long idOrden) {
        return ordenService.deleteOrden(idOrden);
    }

    // ─────────────────────────────────────────────
    // GET /api/orden
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Listar todas las órdenes",
        description = "Obtiene el listado completo de órdenes registradas en el sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes obtenido correctamente")
    })
    @GetMapping("")
    public List<Orden> listarOrdenes() {
        return ordenService.listarOrdenes();
    }

    // ─────────────────────────────────────────────
    // GET /api/orden/contar
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Contar órdenes",
        description = "Retorna el número total de órdenes registradas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    })
    @GetMapping("/contar")
    public long contarOrdenes() {
        return ordenService.contarOrdenes();
    }

    // ─────────────────────────────────────────────
    // GET /api/orden/buscar/{id}
    // ─────────────────────────────────────────────
    @Operation(
        summary = "Buscar una orden por id",
        description = "Obtiene los datos de una orden utilizando su id."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró una orden con ese id"),
        @ApiResponse(responseCode = "500", description = "Error interno al buscar la orden")
    })
    @GetMapping("/buscar/{id}")
    public Orden buscarOrden(@PathVariable Long id) {
        return ordenService.buscarOrden(id);
    }
}
