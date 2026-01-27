package com.lucaslopez.booking_api.domain.prestaciones;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "prestaciones")
@Entity(name = "Prestacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@SQLDelete(sql = "UPDATE prestaciones SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Prestacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    private boolean activo = true;

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
