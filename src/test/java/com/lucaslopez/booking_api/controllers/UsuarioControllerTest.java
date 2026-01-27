package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.reservas.Reserva;
import com.lucaslopez.booking_api.domain.usuarios.*;
import com.lucaslopez.booking_api.security.WithMockCustomUser;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JacksonTester<DatosRegistroUsuario> jsonRegistro;

    @Autowired
    private JacksonTester<DatosDetalleUsuario> jsonDetalle;

    @Autowired
    private JacksonTester<DatosActualizacionUsuario> jsonActualizacion;

    @Autowired
    private JacksonTester<DatosCambioRol> jsonCambioRol;

    private final List<Reserva> lista = new ArrayList<>();
    private final Usuario usuarioMock = new Usuario(1L, "Lucas", "lucas@email.com", "123456",Role.USER,true,lista);

    @Test
    @DisplayName("Debe devolver HTTP 400 si faltan datos")
    @WithMockUser
    void registrar_EscenarioFallido() throws Exception {
        var datosInvalidos = new DatosRegistroUsuario(null, "email-invalido", null);

        var response = mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegistro.write(datosInvalidos).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 201 si todo ok")
    @WithMockUser
    void registrar_EscenarioExitoso() throws Exception {
        var datosValidos = new DatosRegistroUsuario("Lucas", "lucas@email.com", "123456");
        when(usuarioService.registrarUsuario(any())).thenReturn(usuarioMock);

        var response = mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegistro.write(datosValidos).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var jsonEsperado = jsonDetalle.write(new DatosDetalleUsuario(usuarioMock)).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y una lista vacía si no hay usuarios")
    @WithMockCustomUser(role = "ADMIN")
    void listar_EscenarioListaVacia() throws Exception {
        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        var response = mockMvc.perform(get("/usuarios"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"content\":[]");
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y lista de usuarios")
    @WithMockCustomUser(role = "ADMIN")
    void listar_EscenarioExitoso() throws Exception {
        var pagina = new PageImpl<>(List.of(usuarioMock));
        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(pagina);

        var response = mockMvc.perform(get("/usuarios"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("lucas@email.com");
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si no existe usuario con ese ID")
    @WithMockCustomUser(id = 99L, role = "USER")
    void detallar_EscenarioFallido() throws Exception {
        // GIVEN
        when(usuarioService.detallarUsuario(99L)).thenThrow(new EntityNotFoundException());

        // WHEN
        var response = mockMvc.perform(get("/usuarios/99"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si no existe usuario con ese ID")
    @WithMockCustomUser(id = 1L)
    void detallar_EscenarioExitoso() throws Exception {
        // GIVEN
        var dtoRespuesta = new DatosDetalleUsuario(1L, "Lucas", "lucas@email.com", Role.USER);

        // WHEN
        when(usuarioService.detallarUsuario(1L)).thenReturn(dtoRespuesta);

        var response = mockMvc.perform(get("/usuarios/1"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // Verificamos el JSON
        var jsonEsperado = jsonDetalle.write(dtoRespuesta).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Debe devolver HTTP 400 si los datos son inválidos")
    @WithMockCustomUser(id = 1L)
    void actualizar_EscenarioFallido_DatosInvalidos() throws Exception {
        //GIVEN
        var datosInvalidos = new DatosActualizacionUsuario(null, "email-malo");
        //WHEN
        var response = mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizacion.write(datosInvalidos).getJson()))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 si los datos son válidos")
    @WithMockCustomUser(id = 1L)
    void actualizar_EscenarioExitoso() throws Exception {
        //GIVEN
        var datosValidos = new DatosActualizacionUsuario("Lucas Nuevo", "nuevo@email.com");
        var usuarioActualizado = new Usuario(1L, "Lucas Nuevo", "nuevo@email.com", "123456", Role.USER,true, lista);

        when(usuarioService.actualizarUsuario(any(), any())).thenReturn(usuarioActualizado);
        //WHEN
        var response = mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizacion.write(datosValidos).getJson()))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Lucas Nuevo");
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si no existe usuario con ese ID")
    @WithMockCustomUser(role = "ADMIN") //
    void eliminar_EscenarioFallido() throws Exception {
        //GIVEN
        doThrow(new EntityNotFoundException()).when(usuarioService).eliminarUsuario(99L);
        //WHEN
        var response = mockMvc.perform(delete("/usuarios/99"))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 204 si se pudo eliminar con exito")
    @WithMockCustomUser(role = "ADMIN") //
    void eliminar_EscenarioExitoso() throws Exception {
        //GIVEN & WHEN
        var response = mockMvc.perform(delete("/usuarios/1"))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 403 Forbidden si un USER intenta cambiar roles")
    @WithMockCustomUser(role = "USER")
    void cambiarRol_EscenarioFallido_NoAdmin() throws Exception {
        //GIVEN
        var datos = new DatosCambioRol(Role.ADMIN);
        //WHEN
        var response = mockMvc.perform(put("/usuarios/1/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCambioRol.write(datos).getJson()))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y el usuario con nuevo rol si soy ADMIN")
    @WithMockCustomUser(role = "ADMIN")
    void cambiarRol_EscenarioExitoso() throws Exception {
        // GIVEN
        var datos = new DatosCambioRol(Role.ADMIN);
        var usuarioConNuevoRol = new Usuario(1L, "Lucas", "lucas@email.com", "123456", Role.ADMIN,true, lista);

        // Entrenamos al mock
        when(usuarioService.cambiarRol(1L, Role.ADMIN)).thenReturn(usuarioConNuevoRol);

        // WHEN
        var response = mockMvc.perform(put("/usuarios/1/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCambioRol.write(datos).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"role\":\"ADMIN\"");
    }
}