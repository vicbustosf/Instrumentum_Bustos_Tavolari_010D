package cl.instrumentum.service_usuario.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_usuario.model.Usuario;
import cl.instrumentum.service_usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(u -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Usuario encontrado correctamente.", "usuario", u)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un usuario con ID " + id + ".")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Usuario creado correctamente.", "usuario", nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(u -> {
                    u.setUsername(datos.getUsername());
                    u.setEmail(datos.getEmail());
                    u.setRol(datos.getRol());
                    u.setBanda(datos.getBanda());
                    Usuario actualizado = usuarioService.guardarUsuario(u);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Usuario actualizado correctamente.", "usuario", actualizado));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un usuario con ID " + id + ".")));
    }

    // MODIFICADO: Manejo usando el boolean del Service
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Usuario " + id + " eliminado correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe un usuario con ID " + id + "."));
        }
    }

    @GetMapping("/banda/{idBanda}")
    public List<Usuario> porBanda(@PathVariable Long idBanda) {
        return usuarioService.usuariosPorBanda(idBanda);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "Error de validación: " + errores));
    }
}