package com.lucaslopez.booking_api.domain.usuarios;

import jakarta.validation.constraints.NotBlank;

public record DatosActualizacionUsuario(
        @NotBlank String nombre,
        @NotBlank String email
) {
}
