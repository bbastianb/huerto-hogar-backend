package com.abs.huerto_hogar.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abs.huerto_hogar.model.DetalleEnvio;
import com.abs.huerto_hogar.model.DetalleOrden;
import com.abs.huerto_hogar.model.Orden;
import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.repository.OrdenRepository;
import com.abs.huerto_hogar.repository.UsuarioRepository;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Orden saveOrden(Orden orden, Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        if (orden.getFecha_orden() == null || orden.getFecha_orden().isBlank()) {
            throw new IllegalArgumentException("La fecha de la orden no puede estar en blanco.");
        }
        if (orden.getTotal() == null || orden.getTotal() <= 0) {
            throw new IllegalArgumentException("El total de la orden debe ser mayor a 0.");
        }

        if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un producto.");
        }

        if (orden.getDetalleEnvio() == null) {
            throw new IllegalArgumentException("La orden debe incluir los datos de envío.");
        }

        orden.setUsuario(usuario);

        for (DetalleOrden d : orden.getDetalles()) {
            d.setOrden(orden);

            if (d.getIdProducto() == null || d.getIdProducto().isBlank()) {
                throw new IllegalArgumentException("Cada detalle debe tener un idProducto.");
            }
            if (d.getNombreProducto() == null || d.getNombreProducto().isBlank()) {
                throw new IllegalArgumentException("Cada detalle debe tener un nombreProducto.");
            }
            if (d.getPrecioUnitario() == null || d.getPrecioUnitario() <= 0) {
                throw new IllegalArgumentException("El precioUnitario debe ser mayor a 0.");
            }
            if (d.getCantidad() == null || d.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
            }
        }

        DetalleEnvio envio = orden.getDetalleEnvio();
        envio.setOrden(orden);

        if (envio.getDireccion() == null || envio.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La dirección de envío no puede estar en blanco.");
        }
        if (envio.getComuna() == null || envio.getComuna().isBlank()) {
            throw new IllegalArgumentException("La comuna no puede estar en blanco.");
        }
        if (envio.getRegion() == null || envio.getRegion().isBlank()) {
            throw new IllegalArgumentException("La región no puede estar en blanco.");
        }
        if (envio.getMetodo_envio() == null || envio.getMetodo_envio().isBlank()) {
            throw new IllegalArgumentException("El método de envío no puede estar en blanco.");
        }

        return ordenRepository.save(orden);
    }

    public Orden updateEstadoEnvio(Long idOrden, String nuevoEstadoEnvio) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));

        if (nuevoEstadoEnvio == null || nuevoEstadoEnvio.isBlank()) {
            throw new IllegalArgumentException("El estado de envío no puede estar en blanco.");
        }

        // Este es por si acaso
        if (orden.getDetalleEnvio() == null) {
            throw new IllegalArgumentException("La orden no tiene detalle de envío asociado.");
        }

        orden.getDetalleEnvio().setEstado_envio(nuevoEstadoEnvio);
        return ordenRepository.save(orden);
    }

    public Orden deleteOrden(Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));
        ordenRepository.delete(orden);
        return orden;
    }

    public List<Orden> listarOrdenes() {
        return ordenRepository.findAll();
    }

    public long contarOrdenes() {
        return ordenRepository.count();
    }

    public Orden buscarOrden(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));
    }
}
