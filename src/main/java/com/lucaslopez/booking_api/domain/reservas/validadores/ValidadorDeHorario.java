package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class ValidadorDeHorario implements ValidadorReserva{
    @Override
    public void validar(DatosRegistroReserva datos) {
        var fechaInicio = datos.fechaInicio();
        var fechaFin = datos.fechaFin();

        var diaSemana = fechaInicio.getDayOfWeek();
        if (diaSemana == DayOfWeek.SUNDAY) {
            throw new ValidacionException("Lo sentimos, el complejo esta cerrado los domingos");
        }

        var apertura = LocalTime.of(9,0);
        var cierre = LocalTime.of(22,0);

        var horaInicio = LocalTime.of(fechaInicio.getHour(), fechaInicio.getMinute());
        var horaFin = LocalTime.of(fechaFin.getHour(), fechaFin.getMinute());

        if (horaInicio.isBefore(apertura) || horaFin.isAfter(cierre)) {
            throw new ValidacionException("La reserva esta fuera del horario de atencion (9:00 - 22:00)");
        }
    }
}
