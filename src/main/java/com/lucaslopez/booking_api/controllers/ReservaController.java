package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.reservas.*;
import com.lucaslopez.booking_api.domain.usuarios.Role;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/reservas")
@SecurityRequirement(name = "bearer-key")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Transactional
    @PostMapping
    public ResponseEntity registrarReserva(@RequestBody @Valid DatosRegistroReserva datos,
            UriComponentsBuilder uriBuilder) {
        var reserva = reservaService.registrarReserva(datos);

        var uri = uriBuilder.path("/reservas/{id}").buildAndExpand(reserva.getId()).toUri();

        return ResponseEntity.created(uri).body(new DatosDetalleReserva(reserva));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<Page<DatosListaReservas>> listarMisReservas(
            @PageableDefault Pageable paginacion,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        var pagina = reservaRepository.findByUsuarioId(usuarioLogueado.getId(), paginacion)
                .map(DatosListaReservas::new);

        return ResponseEntity.ok(pagina);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DatosListaReservas>> listarReservas(@PageableDefault Pageable paginacion) {

        return ResponseEntity.ok(reservaRepository.findAll(paginacion).map(DatosListaReservas::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity detallarReserva(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogueado) {
        var reserva = reservaService.detallarReserva(id);

        if (!usuarioLogueado.getRole().equals(Role.ADMIN) &&
                !reserva.usuario().id().equals(usuarioLogueado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reserva);
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity actualizarReserva(@RequestBody DatosActualizacionReserva datos, @PathVariable Long id) {
        var reserva = reservaService.actualizarReserva(id, datos);

        return ResponseEntity.ok(new DatosDetalleReserva(reserva));
    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);

        return ResponseEntity.noContent().build();
    }
}
