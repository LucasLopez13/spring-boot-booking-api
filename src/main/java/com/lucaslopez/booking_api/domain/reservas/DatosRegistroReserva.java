package com.lucaslopez.booking_api.domain.reservas;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DatosRegistroReserva(
        @NotNull Long idUsuario,
        @NotNull Long idPrestacion,
        @NotNull @Future LocalDateTime fechaInicio,
        @NotNull @Future LocalDateTime fechaFin
        ) {
}
