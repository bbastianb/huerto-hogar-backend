package com.abs.huerto_hogar.service;

import com.abs.huerto_hogar.model.Contacto;
import com.abs.huerto_hogar.repository.ContactoRepository;
import com.abs.huerto_hogar.config.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoService {

    private final ContactoRepository repository;
    private final EmailService emailService;

    public ContactoService(ContactoRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public Contacto guardar(Contacto contacto) {

        // 1️⃣ Guardar en BD
        Contacto guardado = repository.save(contacto);

        // 2️⃣ Mail al admin
        try {
            emailService.enviarEmail(
                    "huertohogar25@gmail.com",
                    "Nuevo mensaje desde el formulario: " + contacto.getNombre(),
                    generarTextoContacto(contacto)
            );
        } catch (Exception e) {
            System.out.println("⚠️ Error enviando correo al admin: " + e.getMessage());
        }

        // 3️⃣ Mail de confirmación al usuario
        try {
            if (contacto.getEmail() != null && !contacto.getEmail().isBlank()) {
                emailService.enviarEmail(
                        contacto.getEmail(),
                        "Hemos recibido tu mensaje 🌿 - Huerto Hogar",
                        generarTextoConfirmacionUsuario(contacto)
                );
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error enviando correo al usuario: " + e.getMessage());
        }

        return guardado;
    }

    public List<Contacto> listarTodos() {
        return repository.findAll();
    }

    public Contacto buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // Texto que le llega al admin
    private String generarTextoContacto(Contacto c) {
        StringBuilder sb = new StringBuilder();
        sb.append("📩 Nuevo mensaje desde Huerto Hogar\n\n");
        sb.append("Nombre: ").append(c.getNombre()).append("\n");
        sb.append("Correo: ").append(c.getEmail()).append("\n");

        if (c.getTelefono() != null) {
            sb.append("Teléfono: ").append(c.getTelefono()).append("\n");
        }
        if (c.getAsunto() != null) {
            sb.append("Asunto: ").append(c.getAsunto()).append("\n");
        }

        sb.append("\nMensaje:\n").append(c.getMensaje()).append("\n");
        sb.append("\nFecha de envío: ").append(c.getFechaEnvio());

        return sb.toString();
    }

    // Texto que le llega al usuario
    private String generarTextoConfirmacionUsuario(Contacto c) {
        String nombre = (c.getNombre() == null || c.getNombre().isBlank())
                ? "Hola"
                : c.getNombre();

        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(",\n\n");
        sb.append("¡Gracias por escribirnos a Huerto Hogar! 🌿\n");
        sb.append("Hemos recibido tu mensaje y nuestro equipo lo revisará con cariño.\n\n");

        if (c.getAsunto() != null && !c.getAsunto().isBlank()) {
            sb.append("Asunto: ").append(c.getAsunto()).append("\n\n");
        }

        sb.append("Copia de tu mensaje:\n");
        sb.append("------------------------\n");
        sb.append(c.getMensaje()).append("\n");
        sb.append("------------------------\n\n");

        sb.append("Te responderemos a este mismo correo lo antes posible.\n\n");
        sb.append("Un abrazo,\n");
        sb.append("Equipo Huerto Hogar 🥕");

        return sb.toString();
    }
}
