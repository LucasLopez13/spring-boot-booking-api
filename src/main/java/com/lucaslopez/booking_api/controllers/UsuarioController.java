package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.usuarios.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<DatosDetalleUsuario>> listarUsuarios(@PageableDefault Pageable paginacion) {
        return ResponseEntity.ok(usuarioRepository.findAll(paginacion).map(DatosDetalleUsuario::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity detallarUsuario(@PathVariable Long id){
        var usuario = usuarioService.detallarUsuario(id);

        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity actualizarUsuario(@RequestBody @Valid DatosActualizacionUsuario datos, @PathVariable Long id) {
        var usuario = usuarioService.actualizarUsuario(id, datos);

        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));

    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}
