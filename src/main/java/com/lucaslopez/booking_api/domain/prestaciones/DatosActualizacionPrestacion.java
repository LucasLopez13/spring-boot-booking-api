package com.lucaslopez.booking_api.domain.prestaciones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosActualizacionPrestacion(
        @NotBlank String nombre,
        @NotNull Tipo tipo
) {
}
