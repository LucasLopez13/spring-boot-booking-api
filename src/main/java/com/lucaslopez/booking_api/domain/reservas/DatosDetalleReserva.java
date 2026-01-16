package com.lucaslopez.booking_api.domain.reservas;

import com.lucaslopez.booking_api.domain.prestaciones.DatosDetallePrestacion;
import com.lucaslopez.booking_api.domain.usuarios.DatosDetalleUsuario;

import java.time.LocalDateTime;

public record DatosDetalleReserva(
        Long id,
        DatosDetalleUsuario usuario,
        DatosDetallePrestacion prestacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Estado estado
) {
    public DatosDetalleReserva(Reserva reserva) {
        this(
                reserva.getId(),
                new DatosDetalleUsuario(reserva.getUsuario()),
                new DatosDetallePrestacion(reserva.getPrestacion()),
                reserva.getFechaDeInicio(),
                reserva.getFechaDeFin(),
                reserva.getEstado()
        );
    }
}
