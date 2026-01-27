package com.lucaslopez.booking_api.domain.usuarios;


public record DatosDetalleUsuario(
        Long id,
        String nombre,
        String email,
        Role role
) {
    public DatosDetalleUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
