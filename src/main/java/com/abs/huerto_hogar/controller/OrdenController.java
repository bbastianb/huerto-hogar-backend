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

@RestController
@RequestMapping("api/orden")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // POST /api/orden/guardar
    @PostMapping("/guardar")
    public Orden guardarOrden(@RequestBody Orden orden) {

        if (orden.getUsuario() == null || orden.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Debes enviar el id del usuario en la orden.");
        }

        Long usuarioId = orden.getUsuario().getId();
        return ordenService.saveOrden(orden, usuarioId);
    }

    // PUT /api/orden/actualizar-estado/{idOrden}
    @PutMapping("/actualizar-estado/{idOrden}")
    public Orden actualizarEstadoEnvio(
            @PathVariable Long idOrden,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");
        return ordenService.updateEstadoEnvio(idOrden, nuevoEstado);
    }

    // DELETE /api/orden/eliminar/{idOrden}
    @DeleteMapping("/eliminar/{idOrden}")
    public Orden eliminarOrden(@PathVariable Long idOrden) {
        return ordenService.deleteOrden(idOrden);
    }

    // GET /api/orden
    @GetMapping("")
    public List<Orden> listarOrdenes() {
        return ordenService.listarOrdenes();
    }

    // GET /api/orden/contar
    @GetMapping("/contar")
    public long contarOrdenes() {
        return ordenService.contarOrdenes();
    }

    // GET /api/orden/buscar/{id}
    @GetMapping("/buscar/{id}")
    public Orden buscarOrden(@PathVariable Long id) {
        return ordenService.buscarOrden(id);
    }
}
