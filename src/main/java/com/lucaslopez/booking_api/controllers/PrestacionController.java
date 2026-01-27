package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.prestaciones.*;
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
@RequestMapping("/prestaciones")
@SecurityRequirement(name = "bearer-key")
public class PrestacionController {

    @Autowired
    private PrestacionRepository prestacionRepository;

    @Autowired
    private PrestacionService prestacionService;

    @Transactional
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity registrarPrestacion(@RequestBody @Valid DatosRegistroPrestacion datos,
            UriComponentsBuilder uriBuilder) {
        var prestacion = prestacionService.registrarPrestacion(datos);

        var uri = uriBuilder.path("/prestaciones").buildAndExpand(prestacion.getId()).toUri();

        return ResponseEntity.created(uri).body(new DatosDetallePrestacion(prestacion));
    }

    @GetMapping
    public ResponseEntity<Page<DatosDetallePrestacion>> listarPrestaciones(@PageableDefault Pageable paginacion) {
        return ResponseEntity.ok(prestacionRepository.findAll(paginacion).map(DatosDetallePrestacion::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity detallarPrestacion(@PathVariable Long id) {
        var dto = prestacionService.detallarPrestacion(id);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity actualizarPrestacion(@RequestBody @Valid DatosActualizacionPrestacion datos,
            @PathVariable Long id) {
        var prestacion = prestacionService.actualizarPrestacion(id, datos);

        return ResponseEntity.ok(new DatosDetallePrestacion(prestacion));
    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity eliminarPrestacion(@PathVariable Long id) {
        prestacionService.eliminarPrestacion(id);

        return ResponseEntity.noContent().build();
    }
}
