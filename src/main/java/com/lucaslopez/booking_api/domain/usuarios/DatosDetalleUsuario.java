package com.lucaslopez.booking_api.domain.usuarios;

import jakarta.validation.constraints.NotNull;

public record DatosDetalleUsuario(
        Long id,
        String nombre,
        String email
) {
    public DatosDetalleUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }
}
