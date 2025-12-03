package com.abs.huerto_hogar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.security.JwtUtil;
import com.abs.huerto_hogar.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios: creación, autenticación, actualización y recuperación de contraseña")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    // ─────────────────────────────────────────────
    // DTOs INTERNOS
    // ─────────────────────────────────────────────

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String contrasenna;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsuarioResponse {
        private Long id;
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String direccion;
        private String comuna;
        private String region;
        private String rol;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        private String token;
        private UsuarioResponse usuario;
    }

    // ─────────────────────────────────────────────
    // ENDPOINTS PÚBLICOS
    // ─────────────────────────────────────────────

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario con los datos enviados en el cuerpo de la solicitud.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos")
    })
    @PostMapping("/guardar")
    public ResponseEntity<UsuarioResponse> guardarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);
            UsuarioResponse response = convertirAResponse(usuarioGuardado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario utilizando email y contraseña. Devuelve token JWT y datos del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) { // 👈 cambia a <?>
        try {
            // Autenticar usuario
            Usuario usuario = usuarioService.autenticarUsuario(
                    loginRequest.getEmail(),
                    loginRequest.getContrasenna());

            // Obtener UserDetails para generar token
            UserDetails userDetails = usuarioService.obtenerUserDetails(usuario);

            // Generar token JWT
            String token = jwtUtil.generateToken(userDetails);

            // Crear respuesta sin datos sensibles
            UsuarioResponse usuarioResponse = convertirAResponse(usuario);

            // Devolver respuesta OK
            return ResponseEntity.ok(new LoginResponse(token, usuarioResponse));

        } catch (IllegalArgumentException e) {
            // 401 con mensaje claro
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Correo o contraseña incorrectos."));
        }
    }

    @Operation(summary = "Enviar código de recuperación", description = "Envía un código al email del usuario para recuperar su contraseña.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código enviado correctamente"),
            @ApiResponse(responseCode = "404", description = "Email no registrado")
    })
    @PostMapping("/recuperar-contrasenna")
    public ResponseEntity<String> recuperarContrasenna(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            usuarioService.enviarCodigoRecuperacion(email);
            return ResponseEntity.ok("Código de recuperación enviado al correo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar contraseña del usuario", description = "Actualiza la contraseña mediante email, código enviado y nueva contraseña.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Código incorrecto o datos inválidos")
    })
    @PutMapping("/actualizar-contrasenna")
    public ResponseEntity<String> actualizarContrasenna(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String codigo = body.get("codigo");
            String contrasennaNueva = body.get("contrasennaNueva");

            usuarioService.actualizarContrasenna(email, codigo, contrasennaNueva);
            return ResponseEntity.ok("Contraseña actualizada correctamente.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // ENDPOINTS PROTEGIDOS (ADMIN)
    // ─────────────────────────────────────────────

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario existente utilizando su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró un usuario con ese ID")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<UsuarioResponse> eliminarUsuario(@PathVariable Long id) {
        return usuarioService.eliminarUsuario(id)
                .map(usuario -> ResponseEntity.ok(convertirAResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los datos de un usuario mediante su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(convertirAResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los usuarios", description = "Obtiene un listado completo de usuarios registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping("")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        List<UsuarioResponse> responses = usuarios.stream()
                .map(this::convertirAResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Contar usuarios", description = "Devuelve el número total de usuarios registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    })
    @GetMapping("/contar")
    public ResponseEntity<Long> contarUsuarios() {
        return ResponseEntity.ok(usuarioService.contarUsuarios());
    }

    @Operation(summary = "Actualizar datos de usuario", description = "Actualiza el usuario con el ID indicado, reemplazando sus datos por los enviados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
            return ResponseEntity.ok(convertirAResponse(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ─────────────────────────────────────────────
    // MÉTODOS PRIVADOS
    // ─────────────────────────────────────────────

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        response.setTelefono(usuario.getTelefono());
        response.setDireccion(usuario.getDireccion());
        response.setComuna(usuario.getComuna());
        response.setRol(usuario.getRol());
        return response;
    }
}