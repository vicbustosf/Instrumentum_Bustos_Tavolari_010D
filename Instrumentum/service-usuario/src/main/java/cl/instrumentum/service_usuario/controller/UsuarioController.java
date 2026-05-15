package cl.instrumentum.service_usuario.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_usuario.model.Usuario;
import cl.instrumentum.service_usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.buscarUsuarioPorId(id).map(u -> //-> significa "si encuentra el usuario, haz esto"
                { 
                    u.setUsername(datos.getUsername());
                    u.setEmail(datos.getEmail());
                    u.setRol(datos.getRol());
                    u.setBanda(datos.getBanda());
                    return ResponseEntity.ok(usuarioService.guardarUsuario(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/banda/{idBanda}")
    public List<Usuario> porBanda(@PathVariable Long idBanda) {
        return usuarioService.usuariosPorBanda(idBanda);
    }
}