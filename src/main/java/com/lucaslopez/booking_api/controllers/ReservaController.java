package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.reservas.*;
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
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Transactional
    @PostMapping
    public ResponseEntity registrarReserva(@RequestBody @Valid DatosRegistroReserva datos, UriComponentsBuilder uriBuilder) {
        var reserva = reservaService.registrarReserva(datos);

        var uri = uriBuilder.path("/reservas/{id}").buildAndExpand(reserva.getId()).toUri();

        return ResponseEntity.created(uri).body(new DatosDetalleReserva(reserva));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListaReservas>> listarReservas(@PageableDefault Pageable paginacion) {

        return ResponseEntity.ok(reservaRepository.findAll(paginacion).map(DatosListaReservas::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity detallarReserva(@PathVariable Long id) {
        var reserva = reservaService.detallarReserva(id);

        return ResponseEntity.ok(new DatosDetalleReserva(reserva));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity actualizarReserva(@RequestBody DatosActualizacionReserva datos, @PathVariable Long id) {
        var reserva = reservaService.actualizarReserva(id, datos);

        return ResponseEntity.ok(new DatosDetalleReserva(reserva));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);

        return ResponseEntity.noContent().build();
    }
}
