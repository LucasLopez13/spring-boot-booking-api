package com.lucaslopez.booking_api.domain.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroUsuario(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank String contrasenia
) {
}
