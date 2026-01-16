package com.lucaslopez.booking_api.domain.prestaciones;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "prestaciones")
@Entity(name = "Prestacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Prestacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public Prestacion(DatosRegistroPrestacion datos) {
        this.id = null;
        this.nombre = datos.nombre();
        this.tipo = datos.tipo();
    }

    public void actualizarDatos(DatosActualizacionPrestacion datos) {
        if (datos.nombre() != null) {
            this.nombre = datos.nombre();
        }
        if (datos.tipo() != null) {
            this.tipo = datos.tipo();
        }
    }
}
