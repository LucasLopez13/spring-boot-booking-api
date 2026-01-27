package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.ValidacionException;
import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorDeDuracionTest {

    private final ValidadorDeDuracion validador = new ValidadorDeDuracion();

    @Test
    @DisplayName("Debe fallar si la fecha de inicio es después de la fecha fin")
    void validar_FallaPorIncoherenciaFechas() {
        //GIVEN
        var inicio = LocalDateTime.now().plusDays(1).withHour(10);
        var fin = LocalDateTime.now().plusDays(1).withHour(9);

        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);
        // WHEN & THEN
        assertThrows(ValidacionException.class, () -> validador.validar(datos));
    }

    @Test
    @DisplayName("Debe fallar si la duración es menor a 30 minutos")
    void validar_FallaPorDuracionCorta() {
        //GIVEN
        var inicio = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        var fin = inicio.plusMinutes(29);

        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);
        // WHEN & THEN
        var ex = assertThrows(ValidacionException.class, () -> validador.validar(datos));
        assertEquals("La duracion minima de la reserva es de 30 minutos", ex.getMessage());
    }

    @Test
    @DisplayName("Debe fallar si la duración es mayor a 4 horas")
    void validar_FallaPorDuracionLarga() {
        //GIVEN
        var inicio = LocalDateTime.now().plusDays(1).withHour(10);
        var fin = inicio.plusHours(4).plusMinutes(1); // 4 horas y 1 minuto

        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);
        // WHEN & THEN
        var ex = assertThrows(ValidacionException.class, () -> validador.validar(datos));
        assertEquals("La duracion maxima de la reserva es de 4 horas", ex.getMessage());
    }

    @Test
    @DisplayName("Debe pasar si dura exactamente 1 hora")
    void validar_EscenarioExito() {
        //GIVEN
        var inicio = LocalDateTime.now().plusDays(1).withHour(10);
        var fin = inicio.plusHours(1);

        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);
        // WHEN & THEN
        assertDoesNotThrow(() -> validador.validar(datos));
    }

}