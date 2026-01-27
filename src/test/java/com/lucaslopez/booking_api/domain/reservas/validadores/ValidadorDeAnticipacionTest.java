package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.ValidacionException;
import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorDeAnticipacionTest {

    private final ValidadorDeAnticipacion validador = new ValidadorDeAnticipacion();

    @Test
    @DisplayName("Debe fallar si la reserva es antes de 24 horas (ej: dentro de 1 hora)")
    void validar_FallaPorPocaAnticipacion() {
        //GIVEN
        var ahora = LocalDateTime.now();
        var fechaProxima = ahora.plusHours(1);

        var datos = new DatosRegistroReserva(1L,1L,fechaProxima,fechaProxima.plusHours(1));

        //WHEN & THEN
        //Verificamos que lance una excepción de tipo ValidacionException.
        assertThrows(ValidacionException.class,
                () -> validador.validar(datos));
    }

    @Test
    @DisplayName("Debe pasar si la reserva es con suficiente anticipacion (ej: dentro de 25 horas")
    void validar_PasaConBuenaAnticipacion() {
        //GIVEN
        var ahora = LocalDateTime.now();
        var fechaProxima = ahora.plusHours(25);

        var datos = new DatosRegistroReserva(1L,1L,fechaProxima,fechaProxima.plusHours(1));

        //WHEN & THEN
        //Verificamos que no lance ninguna excepción
        assertDoesNotThrow(() -> validador.validar(datos));
    }
}