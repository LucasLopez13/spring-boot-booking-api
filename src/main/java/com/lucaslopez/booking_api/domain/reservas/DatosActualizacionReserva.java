package com.lucaslopez.booking_api.domain.reservas;

import java.time.LocalDateTime;

public record DatosActualizacionReserva(
        Long idPrestacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
        ) {
}
