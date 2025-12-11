package com.abs.huerto_hogar.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.abs.huerto_hogar.config.EmailService;
import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String SOLO_LETRAS_REGEX = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+$";
    private static final String SOLO_NUMEROS_REGEX = "^[0-9]+$";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final Map<String, String> codigosGuardados = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────
    // MÉTODOS DE AUTENTICACIÓN
    // ─────────────────────────────────────────────

    public Usuario autenticarUsuario(String email, String contrasenna) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email o contraseña incorrectos."));

        boolean passwordOk = passwordEncoder.matches(contrasenna, usuario.getContrasenna());
        if (!passwordOk) {
            throw new IllegalArgumentException("Email o contraseña incorrectos.");
        }

        return usuario;
    }

    public UserDetails obtenerUserDetails(Usuario usuario) {
        String authority = usuario.getRol().startsWith("ROLE_")
                ? usuario.getRol()
                : "ROLE_" + usuario.getRol();

        return new User(
                usuario.getEmail(),
                usuario.getContrasenna(),
                Collections.singletonList(new SimpleGrantedAuthority(authority)));
    }

    // ─────────────────────────────────────────────
    // MÉTODOS DE REGISTRO Y ACTUALIZACIÓN
    // ─────────────────────────────────────────────

    public Usuario guardarUsuario(Usuario usuario) {
        // Validaciones
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email no puede estar en blanco.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }
        if (usuario.getContrasenna() == null || usuario.getContrasenna().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco.");
        }
        if (usuario.getDireccion() == null || usuario.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar en blanco.");
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar en blanco.");
        }
        if (usuario.getComuna() == null || usuario.getComuna().isBlank()) {
            throw new IllegalArgumentException("La comuna no puede estar en blanco.");
        }
        if (!usuario.getComuna().matches(SOLO_LETRAS_REGEX)) {
            throw new IllegalArgumentException("La comuna solo puede contener letras y espacios.");
        }
        if (usuario.getRegion() == null) {
            throw new IllegalArgumentException("La región no puede estar en blanco.");
        }
        if (!usuario.getNombre().matches(SOLO_LETRAS_REGEX)) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios.");
        }

        // Rol por defecto
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("usuario");
        }

        // Validación de email
        if (!usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Formato de email no válido.");
        }

        // Verificar si email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        // Validación de contraseña
        if (usuario.getContrasenna().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        // Encriptar contraseña
        String contrasennaEncriptada = passwordEncoder.encode(usuario.getContrasenna());
        usuario.setContrasenna(contrasennaEncriptada);

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioAActualizar = usuarioRepository.findById(id);

        if (usuarioAActualizar.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        Usuario usuarioExistente = usuarioAActualizar.get();

        // Validación de email si cambia
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioActualizado.getEmail() == null || usuarioActualizado.getEmail().isBlank()) {
                throw new IllegalArgumentException("El email no puede estar en blanco.");
            }
            if (!usuarioActualizado.getEmail().contains("@")) {
                throw new IllegalArgumentException("Formato de email no válido.");
            }
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso.");
            }
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }

        // Actualizar contraseña si se proporciona
        if (usuarioActualizado.getContrasenna() != null &&
                !usuarioActualizado.getContrasenna().isBlank()) {
            if (usuarioActualizado.getContrasenna().length() < 8) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
            }
            String contrasennaEncriptada = passwordEncoder.encode(usuarioActualizado.getContrasenna());
            usuarioExistente.setContrasenna(contrasennaEncriptada);
        }

        // Validaciones de campos
        if (usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }
        if (usuarioActualizado.getApellido() == null || usuarioActualizado.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco.");
        }
        if (!usuarioActualizado.getNombre().matches(SOLO_LETRAS_REGEX)) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios.");
        }
        if (!usuarioActualizado.getApellido().matches(SOLO_LETRAS_REGEX)) {
            throw new IllegalArgumentException("El apellido solo puede contener letras y espacios.");
        }
        if (!usuarioActualizado.getComuna().matches(SOLO_LETRAS_REGEX)) {
            throw new IllegalArgumentException("La comuna solo puede contener letras y espacios.");
        }
        if (!usuarioActualizado.getTelefono().matches(SOLO_NUMEROS_REGEX)) {
            throw new IllegalArgumentException("El teléfono solo debe contener números.");
        }
        if (usuarioActualizado.getRegion() == null) {
            throw new IllegalArgumentException("La región no puede estar en blanco.");
        }

        // Actualizar campos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
        usuarioExistente.setComuna(usuarioActualizado.getComuna());
        usuarioExistente.setRegion(usuarioActualizado.getRegion());
        usuarioExistente.setRol(usuarioActualizado.getRol());

        return usuarioRepository.save(usuarioExistente);
    }

    // ─────────────────────────────────────────────
    // MÉTODOS DE RECUPERACIÓN DE CONTRASEÑA
    // ─────────────────────────────────────────────

    public void enviarCodigoRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
        codigosGuardados.put(email, codigo);

        String texto = "Hola " + usuario.getNombre() + ",\n\n"
                + "Tu código de recuperación de contraseña es: " + codigo + "\n"
                + "Huerto Hogar 🌱";

        emailService.enviarEmail(
                email,
                "Código de recuperación de contraseña - Huerto Hogar",
                texto);

    }

    public void actualizarContrasenna(String email, String codigo, String contrasennaNueva) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Código o correo no válido."));

        String codigoGuardado = codigosGuardados.get(email);
        if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
            throw new IllegalArgumentException("Código o correo no válido.");
        }

        if (contrasennaNueva == null || contrasennaNueva.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }

        if (contrasennaNueva.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        String contrasennaEncriptada = passwordEncoder.encode(contrasennaNueva);
        usuario.setContrasenna(contrasennaEncriptada);
        usuarioRepository.save(usuario);

        codigosGuardados.remove(email);
    }

    // ─────────────────────────────────────────────
    // MÉTODOS CRUD BÁSICOS
    // ─────────────────────────────────────────────

    public Optional<Usuario> eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            usuarioRepository.deleteById(id);
            return usuario;
        } else {
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario actualizarFotoPerfil(Long id, MultipartFile foto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("La foto no puede estar vacía.");
        }

        try {
            usuario.setFotoPerfil(foto.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la foto de perfil.", e);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario eliminarFotoPerfil(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        usuario.setFotoPerfil(null);
        return usuarioRepository.save(usuario);
    }
}