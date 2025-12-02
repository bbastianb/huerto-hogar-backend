package com.abs.huerto_hogar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.service.UsuarioService;

// Swagger OpenAPI imports
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/usuario")
@Tag(
        name = "Usuarios",
        description = "Gestión de usuarios: creación, autenticación, actualización y recuperación de contraseña"
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ─────────────────────────────────────────────
    // POST /api/usuario/guardar
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario con los datos enviados en el cuerpo de la solicitud."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos")
    })
    @PostMapping("/guardar")
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    // ─────────────────────────────────────────────
    // DELETE /api/usuario/eliminar/{id}
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Eliminar un usuario",
            description = "Elimina un usuario existente utilizando su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró un usuario con ese ID")
    })
    @DeleteMapping("/eliminar/{id}")
    public Usuario eliminarUsuario(@PathVariable Long id) {
        return usuarioService.eliminarUsuario(id).orElse(null);
    }

    // ─────────────────────────────────────────────
    // GET /api/usuario/buscar/{id}
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene los datos de un usuario mediante su identificador único."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/buscar/{id}")
    public Usuario buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id).orElse(null);
    }

    // ─────────────────────────────────────────────
    // GET /api/usuario
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene un listado completo de usuarios registrados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping("")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    // ─────────────────────────────────────────────
    // GET /api/usuario/contar
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Contar usuarios",
            description = "Devuelve el número total de usuarios registrados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    })
    @GetMapping("/contar")
    public long contarUsuarios() {
        return usuarioService.contarUsuarios();
    }

    // ─────────────────────────────────────────────
    // PUT /api/usuario/actualizar/{id}
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Actualizar datos de usuario",
            description = "Actualiza el usuario con el ID indicado, reemplazando sus datos por los enviados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/actualizar/{id}")
    public Usuario actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado
    ) {
        return usuarioService.actualizarUsuario(id, usuarioActualizado);
    }

    // ─────────────────────────────────────────────
    // POST /api/usuario/login
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario utilizando email y contraseña."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario login) {
        return usuarioService.login(login.getEmail(), login.getContrasenna());
    }

    // ─────────────────────────────────────────────
    // POST /api/usuario/recuperar-contrasenna
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Enviar código de recuperación",
            description = "Envía un código al email del usuario para recuperar su contraseña."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Código enviado correctamente"),
        @ApiResponse(responseCode = "404", description = "Email no registrado")
    })
    @PostMapping("/recuperar-contrasenna")
    public String recuperarContrasenna(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        usuarioService.enviarCodigoRecuperacion(email);
        return "Código de recuperación enviado al correo.";
    }

    // ─────────────────────────────────────────────
    // PUT /api/usuario/actualizar-contrasenna
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Actualizar contraseña del usuario",
            description = "Actualiza la contraseña mediante email, código enviado y nueva contraseña."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Código incorrecto o datos inválidos")
    })
    @PutMapping("/actualizar-contrasenna")
    public String actualizarContrasenna(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        String contrasennaNueva = body.get("contrasennaNueva");
        usuarioService.actualizarContrasenna(email, codigo, contrasennaNueva);
        return "Contraseña actualizada correctamente.";
    }
}
