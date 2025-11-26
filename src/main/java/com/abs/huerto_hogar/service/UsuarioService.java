package com.abs.huerto_hogar.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.repository.UsuarioRepository;
import com.abs.huerto_hogar.config.EmailService;

@Service // Define que la clase es un servicio de Spring
public class UsuarioService {

    private static final String SOLO_LETRAS_REGEX = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+$";
    private static final String SOLO_NUMEROS_REGEX = "^[0-9]+$";

    @Autowired // Autowire el servicio de email y lo inyecta en la clase
    private EmailService emailService;

    private final Map<String, String> codigosGuardados = new ConcurrentHashMap<>();

    @Autowired // Autowire el repositorio de Usuario y lo inyecta en la clase
    private UsuarioRepository usuarioRepository;

    @Autowired // Autowire el encoder de passwords y lo inyecta en la clase
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void enviarCodigoRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));
        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000); // Genera un código de 6 dígitos
        codigosGuardados.put(email, codigo);

        String texto = "Hola " + usuario.getNombre() + ",\n\n"
                + "Tu código de recuperación de contraseña es: " + codigo + "\n"
                + "Huerto Hogar 🌱";
        emailService.enviarEmail(email, "Código de Recuperación de Contraseña", texto);
    }

    public void actualizarContrasenna(String email, String codigo, String contrasennaNueva) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Codigo o Correo no válido."));

        String codigoGuardado = codigosGuardados.get(email);
        if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
            throw new IllegalArgumentException("Codigo o Correo no válido.");
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

        codigosGuardados.remove(email); // Elimina el código una vez usada la recuperación
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) { // Actualizar usuario

        Optional<Usuario> usuarioAActualizar = usuarioRepository.findById(id);

        if (usuarioAActualizar.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        Usuario usuarioExistente = usuarioAActualizar.get();

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

        if (usuarioActualizado.getContrasenna() != null &&
                !usuarioActualizado.getContrasenna().isBlank()) {

            if (usuarioActualizado.getContrasenna().length() < 8) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
            }
            String contrasennaEncriptada = passwordEncoder.encode(usuarioActualizado.getContrasenna());
            usuarioExistente.setContrasenna(contrasennaEncriptada);
        }

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

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
        usuarioExistente.setComuna(usuarioActualizado.getComuna());
        usuarioExistente.setRegion(usuarioActualizado.getRegion());
        usuarioExistente.setRol(usuarioActualizado.getRol());

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario guardarUsuario(Usuario usuario) { // Guardar usuario

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
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("usuario"); // Asigna rol por defecto "usuario"
        }

        if (!usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Formato de email no válido.");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        if (usuario.getContrasenna().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        String contrasennaEncriptada = passwordEncoder.encode(usuario.getContrasenna());
        usuario.setContrasenna(contrasennaEncriptada);

        return usuarioRepository.save(usuario);

    }

    public Optional<Usuario> eliminarUsuario(Long id) { // Eliminar usuario por ID
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return usuarioRepository.findById(id);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorId(Long id) { // Obtener usuario por ID
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarUsuarios() { // Listar todos los usuarios
        return usuarioRepository.findAll();
    }

    public long contarUsuarios() { // Contar usuarios
        return usuarioRepository.count();
    }

    public Optional<Usuario> buscarPorEmail(String email) { // Obtener usuario por email
        return usuarioRepository.findByEmail(email);
    }

    public Usuario login(String email, String contrasenna) { // Login
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email o contraseña incorrectos."));

        boolean passwordOk = passwordEncoder.matches(contrasenna, usuario.getContrasenna());
        if (!passwordOk) {
            throw new IllegalArgumentException("Email o contraseña incorrectos.");
        }

        return usuario;

    }

}
