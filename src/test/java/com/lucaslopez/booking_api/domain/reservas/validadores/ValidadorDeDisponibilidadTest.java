package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.ValidacionException;
import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import com.lucaslopez.booking_api.domain.reservas.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ValidadorDeDisponibilidadTest {
    @Mock
    private ReservaRepository repository;

    @InjectMocks
    private ValidadorDeDisponibilidad validador;

    @Test
    @DisplayName("Debe fallar si el repositorio confirma solapamiento")
    void validar_FallaSiEstaOcupado() {
        // GIVEN
        var inicio = LocalDateTime.now().plusDays(1);
        var fin = inicio.plusHours(1);
        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);

        // Simulamos que el repositorio dice TRUE (Existe solapamiento)
        given(repository.existsSolapamiento(anyLong(), any(), any())).willReturn(true);

        // WHEN & THEN
        assertThrows(ValidacionException.class, () -> validador.validar(datos));
    }

    @Test
    @DisplayName("Debe pasar si el repositorio dice que está libre")
    void validar_PasaSiEstaLibre() {
        // GIVEN
        var inicio = LocalDateTime.now().plusDays(1);
        var fin = inicio.plusHours(1);
        var datos = new DatosRegistroReserva(1L, 1L, inicio, fin);

        // Simulamos que el repositorio dice FALSE (No hay nadie jugando)
        given(repository.existsSolapamiento(anyLong(), any(), any())).willReturn(false);

        // WHEN & THEN
        assertDoesNotThrow(() -> validador.validar(datos));
    }


}