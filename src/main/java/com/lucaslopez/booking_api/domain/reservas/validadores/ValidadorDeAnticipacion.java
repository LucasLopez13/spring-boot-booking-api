package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorDeAnticipacion implements ValidadorReserva{
    @Override
    public void validar(DatosRegistroReserva datos) {
        var fechaReserva = datos.fechaInicio();
        var ahora = LocalDateTime.now();

        var diferenciaHoras = Duration.between(ahora, fechaReserva).toHours();

        if (diferenciaHoras < 24) {
            throw new ValidacionException("Las reservas deben realizarse con al menos 24 horas de anticipación");
        }
    }
}
