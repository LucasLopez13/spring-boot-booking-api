package com.lucaslopez.booking_api.domain.reservas;

import java.time.LocalDateTime;

public record DatosListaReservas(
        Long id,
        Long idUsuario,
        Long idPrestacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Estado estado

) {
    public DatosListaReservas(Reserva reserva) {
        this(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getPrestacion().getId(),
                reserva.getFechaDeInicio(),
                reserva.getFechaDeFin(),
                reserva.getEstado()
        );
    }
}
