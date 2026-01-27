package com.lucaslopez.booking_api.domain.usuarios;

import com.lucaslopez.booking_api.domain.ValidacionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private final DatosRegistroUsuario datosUsuario = new DatosRegistroUsuario(
            "Lucas",
            "lucas@email.com",
            "123456"
    );

    @Test
    @DisplayName("Debe registrar usuario exitosamente si el email no existe")
    void registrarUsuario_EscenarioExito() {
        // GIVEN
        // 1. Simulamos que existsByEmail devuelve FALSE
        given(usuarioRepository.existsByEmail(datosUsuario.email())).willReturn(false);

        // 2. Simulamos la encriptación
        given(passwordEncoder.encode(datosUsuario.contrasenia())).willReturn("clave_encriptada_falsa");

        // WHEN
        usuarioService.registrarUsuario(datosUsuario);

        // THEN
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe fallar si el email ya está registrado")
    void registrarUsuario_FallaPorEmailDuplicado() {
        // GIVEN
        // Simulamos que existsByEmail devuelve TRUE (Ya existe el usuario)
        given(usuarioRepository.existsByEmail(datosUsuario.email())).willReturn(true);

        // WHEN & THEN
        var exception = assertThrows(ValidacionException.class, () ->
                usuarioService.registrarUsuario(datosUsuario)
        );

        assertEquals("El email ingresado ya se encuentra registrado.", exception.getMessage());

        verify(usuarioRepository, never()).save(any());
    }
}