package com.lucaslopez.booking_api.domain.reservas;

import com.lucaslopez.booking_api.domain.ValidacionException;
import com.lucaslopez.booking_api.domain.prestaciones.Prestacion;
import com.lucaslopez.booking_api.domain.prestaciones.PrestacionRepository;
import com.lucaslopez.booking_api.domain.reservas.validadores.ValidadorReserva;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import com.lucaslopez.booking_api.domain.usuarios.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PrestacionRepository prestacionRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private List<ValidadorReserva> validadores; // Mockeamos la lista de reglas

    @InjectMocks
    private ReservaService reservaService; // Inyectamos los mocks aquí dentro

    private final DatosRegistroReserva datosReserva = new DatosRegistroReserva(
            1L, // idUsuario
            1L, // idPrestacion
            LocalDateTime.now().plusDays(1).withHour(10), // Mañana a las 10
            LocalDateTime.now().plusDays(1).withHour(11)  // Mañana a las 11
    );

    @Test
    @DisplayName("Debe registrar la reserva exitosamente cuando todos los datos son válidos")
    void registrarReserva_EscenarioExito() {
        // GIVEN (DADO QUE...)
        // Simulamos que el usuario y la prestación existen en la BD
        var usuarioMock = mock(Usuario.class);
        var prestacionMock = mock(Prestacion.class);

        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuarioMock));
        given(prestacionRepository.findById(1L)).willReturn(Optional.of(prestacionMock));

        // WHEN (CUANDO...)
        reservaService.registrarReserva(datosReserva);

        // THEN (ENTONCES...)
        // Verificamos que se llamó al repositorio para guardar
        verify(reservaRepository).save(any(Reserva.class));
        // Verificamos que se ejecutaron las validaciones
        verify(validadores).forEach(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el Usuario no existe")
    void registrarReserva_UsuarioNoEncontrado() {
        // GIVEN
        // Simulamos que el usuario NO existe (retorna vacío)
        given(usuarioRepository.findById(1L)).willReturn(Optional.empty());

        // WHEN & THEN
        // Verificamos que lance ValidacionException con el mensaje correcto
        var excepcion = assertThrows(ValidacionException.class, () ->
                reservaService.registrarReserva(datosReserva)
        );

        assertEquals("No existe un usuario con ese ID", excepcion.getMessage());

        // Aseguramos que NUNCA se guardó nada en la BD
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la Prestación no existe")
    void registrarReserva_PrestacionNoEncontrada() {
        // GIVEN
        // El usuario sí existe, pero la prestación no
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(mock(Usuario.class)));
        given(prestacionRepository.findById(1L)).willReturn(Optional.empty());

        // WHEN & THEN
        var excepcion = assertThrows(ValidacionException.class, () ->
                reservaService.registrarReserva(datosReserva)
        );

        assertEquals("No existe un prestacion con ese ID", excepcion.getMessage());
        verify(reservaRepository, never()).save(any());
    }

}