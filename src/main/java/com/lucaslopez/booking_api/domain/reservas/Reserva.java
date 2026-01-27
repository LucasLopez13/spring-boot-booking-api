package com.lucaslopez.booking_api.domain.reservas;

import com.lucaslopez.booking_api.domain.prestaciones.Prestacion;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "reservas")
@Entity(name = "Reserva")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    private Prestacion prestacion;
    private LocalDateTime fechaDeInicio;
    private LocalDateTime fechaDeFin;
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public Reserva(DatosRegistroReserva datos, Usuario usuario, Prestacion prestacion) {
        this.id = null;
        this.usuario = usuario;
        this.prestacion = prestacion;
        this.fechaDeInicio = datos.fechaInicio();
        this.fechaDeFin = datos.fechaFin();
        this.estado = Estado.RESERVADO;
    }

    public void actualizarReserva(Prestacion nuevaPrestacion) {
        this.prestacion = nuevaPrestacion;
    }

    public void cancelar() {
        this.estado = Estado.CANCELADO;
    }

    public void setFechaInicio(LocalDateTime fecha) {
        this.fechaDeInicio = fecha;
    }

    public void setFechaFin(LocalDateTime fecha) {
        this.fechaDeFin = fecha;
    }
}
