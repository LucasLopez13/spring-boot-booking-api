package com.lucaslopez.booking_api.domain.prestaciones;

public record DatosDetallePrestacion(
        Long idPrestacion,
        String nombre,
        Tipo tipo
) {
    public DatosDetallePrestacion(Prestacion prestacion) {
        this(
                prestacion.getId(),
                prestacion.getNombre(),
                prestacion.getTipo()
        );
    }
}
