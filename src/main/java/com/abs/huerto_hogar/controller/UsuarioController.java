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

import com.abs.huerto_hogar.model.Usuario;
import com.abs.huerto_hogar.security.JwtUtil;
import com.abs.huerto_hogar.service.UsuarioService;

import lombok.Data;

@RestController // Define que la clase es un controlador de REST
@RequestMapping("api/usuario") // Mapear rutas a la clase
public class UsuarioController {

    @Autowired // Autowire el servicio de Usuario y lo inyecta en la clase
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/guardar") // Mapear ruta POST /usuario/guardar
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    @DeleteMapping("/eliminar/{id}") // Mapear ruta DELETE /usuario/eliminar/{id}
    public Usuario eliminarUsuario(@PathVariable Long id) {
        return usuarioService.eliminarUsuario(id).orElse(null);
    }

    @GetMapping("/buscar/{id}") // Mapear ruta GET /usuario/buscar/{id}
    public Usuario buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id).orElse(null);
    }

    @GetMapping("") // Mapear ruta GET /usuario/listar
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/contar") // Mapear ruta GET /usuario/contar
    public long contarUsuarios() {
        return usuarioService.contarUsuarios();
    }

    @PutMapping("/actualizar/{id}") // Mapear ruta PUT /usuario/actualizar/{id}
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        return usuarioService.actualizarUsuario(id, usuarioActualizado);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Usuario login) {

        Usuario usuario = usuarioService.login(login.getEmail(), login.getContrasenna());

        String token = jwtUtil.generateToken(usuario);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsuario(usuario);
        return response;
    }

    @PostMapping("/recuperar-contrasenna") // Mapear ruta POST /usuario/recuperar-contrasena
    public String recuperarContrasenna(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        usuarioService.enviarCodigoRecuperacion(email);
        return "Código de recuperación enviado al correo.";
    }

    @PutMapping("/actualizar-contrasenna") // Mapear ruta PUT /usuario/actualizar-contrasenna
    public String actualizarContrasenna(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        String contrasennaNueva = body.get("contrasennaNueva");
        usuarioService.actualizarContrasenna(email, codigo, contrasennaNueva);
        return "Contraseña actualizada correctamente.";
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Usuario usuario;
    }
}
