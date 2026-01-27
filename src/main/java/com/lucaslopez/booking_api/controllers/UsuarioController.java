package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.usuarios.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    @PostMapping("/registrar")
    public ResponseEntity registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datos, UriComponentsBuilder uriBuilder) {
        var usuario = usuarioService.registrarUsuario(datos);

        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        return ResponseEntity.created(uri).body(new DatosDetalleUsuario(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Page<DatosDetalleUsuario>> listarUsuarios(@PageableDefault Pageable paginacion) {
        return ResponseEntity.ok(usuarioRepository.findAll(paginacion).map(DatosDetalleUsuario::new));
    }

    @GetMapping("/{id}")
    // Permite el acceso si el usuario es ADMIN -O- si el usuario autenticado
    // intenta acceder a su propio recurso (el ID de la URL coincide con su ID del Token).
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity detallarUsuario(@PathVariable Long id){
        var datosDetalle = usuarioService.detallarUsuario(id);
        return ResponseEntity.ok(datosDetalle);
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity actualizarUsuario(@RequestBody @Valid DatosActualizacionUsuario datos, @PathVariable Long id) {
        var usuario = usuarioService.actualizarUsuario(id, datos);

        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));

    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/rol")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity cambiarRolUsuario(@PathVariable Long id, @RequestBody @Valid DatosCambioRol datos) {
        var usuario = usuarioService.cambiarRol(id, datos.role());
        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));
    }
}
