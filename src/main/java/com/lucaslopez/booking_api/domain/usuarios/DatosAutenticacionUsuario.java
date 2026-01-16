package com.lucaslopez.booking_api.domain.usuarios;

import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacionUsuario(
        @NotBlank String email,
        @NotBlank String contrasenia
) {
}
