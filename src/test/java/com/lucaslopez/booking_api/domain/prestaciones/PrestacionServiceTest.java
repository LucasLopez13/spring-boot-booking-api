package com.lucaslopez.booking_api.domain.prestaciones;

import com.lucaslopez.booking_api.domain.ValidacionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PrestacionServiceTest {
    @Mock
    private PrestacionRepository prestacionRepository;

    @InjectMocks
    private PrestacionService prestacionService;

    // Datos de prueba
    private final DatosRegistroPrestacion datosPrestacion = new DatosRegistroPrestacion(
            "Cancha de Padel 1",
            Tipo.PADEL
    );

    @Test
    @DisplayName("Debe registrar prestación si el nombre no existe")
    void registrarPrestacion_EscenarioExito() {
        // GIVEN
        // Simulamos que NO existe ninguna cancha con ese nombre
        given(prestacionRepository.existsByNombre(anyString())).willReturn(false);

        // WHEN
        prestacionService.registrarPrestacion(datosPrestacion);

        // THEN
        verify(prestacionRepository).save(any(Prestacion.class));
    }

    @Test
    @DisplayName("Debe fallar al registrar si el nombre ya existe")
    void registrarPrestacion_FallaPorNombreDuplicado() {
        // GIVEN
        // Simulamos que YA existe (true)
        given(prestacionRepository.existsByNombre(datosPrestacion.nombre())).willReturn(true);

        // WHEN & THEN
        var exception = assertThrows(ValidacionException.class, () ->
                prestacionService.registrarPrestacion(datosPrestacion)
        );

        assertEquals("Ya existe una prestacion con ese nombre", exception.getMessage());
        verify(prestacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar al buscar/eliminar si el ID no existe")
    void buscarPrestacion_FallaPorIdNoEncontrado() {
        // GIVEN
        // Simulamos que buscar por ID devuelve vacío
        given(prestacionRepository.findById(99L)).willReturn(Optional.empty());

        // WHEN & THEN
        // Probamos con eliminar, que internamente llama a buscarPrestacionPorId
        var exception = assertThrows(ValidacionException.class, () ->
                prestacionService.eliminarPrestacion(99L)
        );

        assertEquals("No existe una prestacion con ese ID", exception.getMessage());
    }

    @Test
    @DisplayName("Debe eliminar la prestación si el ID existe")
    void eliminarPrestacion_EscenarioExito() {
        // GIVEN
        var prestacionMock = new Prestacion(); // O un mock(Prestacion.class)
        // Simulamos que SÍ encuentra la prestación
        given(prestacionRepository.findById(1L)).willReturn(Optional.of(prestacionMock));

        // WHEN
        prestacionService.eliminarPrestacion(1L);

        // THEN
        // Verificamos que llame al delete con el ID correcto
        verify(prestacionRepository).deleteById(any());
    }
}