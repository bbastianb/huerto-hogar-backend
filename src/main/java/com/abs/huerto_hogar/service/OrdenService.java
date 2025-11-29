package com.abs.huerto_hogar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abs.huerto_hogar.model.Orden;
import com.abs.huerto_hogar.model.Producto;
import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.repository.OrdenRepository;
import com.abs.huerto_hogar.repository.ProductoRepository;
import com.abs.huerto_hogar.repository.UsuarioRepository;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public Orden saveOrden(Orden orden, Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        if (orden.getEstado_orden() == null || orden.getEstado_orden().isBlank()) {
            throw new IllegalArgumentException("El estado de la orden no puede estar en blanco.");
        }

        if (orden.getFecha_orden() == null || orden.getFecha_orden().isBlank()) {
            throw new IllegalArgumentException("La fecha de la orden no puede estar en blanco.");
        }

        if (orden.getTotal() == null || orden.getTotal() <= 0) {
            throw new IllegalArgumentException("El total de la orden debe ser mayor a 0.");
        }

        if (orden.getProductos() == null || orden.getProductos().isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un producto.");
        }

        // La lista que llega desde el front trae solo el id de cada producto
        List<Producto> productosPersistidos = orden.getProductos().stream()
                .map(p -> productoRepository.findById(p.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "El producto con id " + p.getId() + " no existe.")))
                .collect(Collectors.toList());

        orden.setUsuario(usuario);
        orden.setProductos(productosPersistidos);

        return ordenRepository.save(orden);
    }

    public Orden updateOrden(Long idOrden, String nuevoEstado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe."));

        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar en blanco.");
        }

        orden.setEstado_orden(nuevoEstado);
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
