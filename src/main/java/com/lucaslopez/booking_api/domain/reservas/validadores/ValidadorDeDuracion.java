package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ValidadorDeDuracion implements ValidadorReserva {
    @Override
    public void validar(DatosRegistroReserva datos) {
        if (datos.fechaInicio().isAfter(datos.fechaFin())) {
            throw new ValidacionException("La fecha de inicio debe ser anterior a la fecha final");
        }
        var duracion = Duration.between(datos.fechaInicio(), datos.fechaFin());
        if (duracion.toMinutes() < 30) {
            throw new ValidacionException("La duracion minima de la reserva es de 30 minutos");
        }
        if (duracion.toMinutes() > 240) {
            throw new ValidacionException("La duracion maxima de la reserva es de 4 horas");
        }
    }
}
