package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.ValidacionException;
import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorDeHorarioTest {

    private final ValidadorDeHorario validador = new ValidadorDeHorario();

    @Test
    @DisplayName("Debe fallar si intenta registrar una reserva un domingo")
    void validar_FallaSiEsDomingo() {
        var proximoDomingo = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        var fechaDomingo = LocalDateTime.of(proximoDomingo, LocalTime.of(10, 0)); //Horario habil pero dia incorrecto

        var datos = new DatosRegistroReserva(1L,1L,fechaDomingo,fechaDomingo.plusHours(1));

        //WHEN & THEN
        var excepcion = assertThrows(ValidacionException.class, () -> validador.validar(datos));
        //Comparamos si la excepcion es la misma
        assertEquals("Lo sentimos, el complejo esta cerrado los domingos", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe fallar si intenta reservar antes de las 9:00")
    void validar_FallaSiEsAntesDeLas9() {
        //GIVEN
        //Lunes a las 08:00hs
        var lunes = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        var fechaTemprano = LocalDateTime.of(lunes, LocalTime.of(8, 59));

        var datos = new DatosRegistroReserva(1L,1L,fechaTemprano,fechaTemprano.plusHours(1));

        //WHEN & THEN
        var excepcion = assertThrows(ValidacionException.class, () -> validador.validar(datos));
        assertTrue(excepcion.getMessage().contains("fuera del horario"));

    }

    @Test
    @DisplayName("Debe fallar si la reserva termina despues de las 22:00")
    void validar_FallaPorHorarioTarde() {
        //GIVEN
        //lunes Empieza a las 21:30, pero termina a las 22:30
        var lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        var fechaInicio = LocalDateTime.of(lunes, java.time.LocalTime.of(21, 30));
        var fechaFin = LocalDateTime.of(lunes, java.time.LocalTime.of(22, 30));

        var datos = new DatosRegistroReserva(1L, 1L, fechaInicio, fechaFin);

        // WHEN & THEN
        var excepcion = assertThrows(ValidacionException.class, () -> validador.validar(datos));
        assertTrue(excepcion.getMessage().contains("fuera del horario"));
    }

    @Test
    @DisplayName("Debe pasar si es un Lunes a las 10:00 AM (Horario y día permitidos)")
    void validar_EscenarioExito() {
        // GIVEN
        var lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        var fechaValida = LocalDateTime.of(lunes, java.time.LocalTime.of(10, 0));

        var datos = new DatosRegistroReserva(1L, 1L, fechaValida, fechaValida.plusHours(1));

        // WHEN & THEN
        assertDoesNotThrow(() -> validador.validar(datos));
    }

}