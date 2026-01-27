package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.reservas.*;
import com.lucaslopez.booking_api.domain.prestaciones.Prestacion;
import com.lucaslopez.booking_api.domain.prestaciones.Tipo;
import com.lucaslopez.booking_api.domain.usuarios.Role;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
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

import java.time.LocalDateTime;
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
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private ReservaRepository reservaRepository;

    @Autowired
    private JacksonTester<DatosRegistroReserva> jsonRegistro;

    @Autowired
    private JacksonTester<DatosDetalleReserva> jsonDetalle;

    @Autowired
    private JacksonTester<DatosActualizacionReserva> jsonActualizacion;

    private final LocalDateTime fechaInicio = LocalDateTime.now().plusDays(1).withHour(10);
    private final LocalDateTime fechaFin = fechaInicio.plusHours(1);
    private final List<Reserva> lista = new ArrayList<>();

    private final Usuario usuarioMock = new Usuario(1L, "Lucas", "lucas@email.com", "pass", Role.USER,true, lista);
    private final Prestacion prestacionMock = new Prestacion(1L, "Cancha 1", Tipo.CANCHA_FUTBOL,true);
    private final Reserva reservaMock = new Reserva(1L, usuarioMock, prestacionMock, fechaInicio, fechaFin, Estado.RESERVADO);

    @Test
    @DisplayName("Debe devolver HTTP 400 cuando los datos no son válidos (ej: fechas pasadas)")
    @WithMockUser
    void registrar_EscenarioFallido() throws Exception {
        // GIVEN: Fechas inválidas o datos nulos
        var datosInvalidos = new DatosRegistroReserva(null, null, null, null);

        // WHEN
        var response = mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegistro.write(datosInvalidos).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 201 cuando los datos son válidos")
    @WithMockUser
    void registrar_EscenarioExitoso() throws Exception {
        // GIVEN
        var datosValidos = new DatosRegistroReserva(1L, 1L, fechaInicio, fechaFin);

        when(reservaService.registrarReserva(any())).thenReturn(reservaMock);

        // WHEN
        var response = mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegistro.write(datosValidos).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        //Verificamos Json esperado.
        var jsonEsperado = jsonDetalle.write(new DatosDetalleReserva(reservaMock)).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y la lista vacía si no hay reservas")
    @WithMockUser(roles = "ADMIN")
    void listar_EscenarioListaVacia() throws Exception {
        //GIVEN
        when(reservaRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        //WHEN
        var response = mockMvc.perform(get("/reservas"))
                .andReturn().getResponse();

        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"content\":[]");
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y la lista de reservas")
    @WithMockUser(roles = "ADMIN")
    void listar_EscenarioExitoso() throws Exception {
        //GIVEN
        var pagina = new PageImpl<>(List.of(reservaMock));
        when(reservaRepository.findAll(any(Pageable.class))).thenReturn(pagina);

        //WHEN
        var response = mockMvc.perform(get("/reservas"))
                .andReturn().getResponse();

        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // Verificamos que aparezca el ID o algún dato de la reserva
        assertThat(response.getContentAsString()).contains("\"id\":1");
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si ID no existe")
    @WithMockUser
    void detallar_EscenarioFallido_IdNoExiste() throws Exception {
        //GIVEN
        when(reservaService.detallarReserva(999L)).thenThrow(new EntityNotFoundException());

        //WHEN
        var response = mockMvc.perform(get("/reservas/999"))
                .andReturn().getResponse();

        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si ID no existe al actualizar")
    @WithMockCustomUser(id = 999L)
    void actualizar_EscenarioFallido_IdNoExiste() throws Exception {
        //GIVEN
        var datosUpdate = new DatosActualizacionReserva(1L,fechaInicio.plusDays(1), fechaFin.plusDays(1));

        when(reservaService.actualizarReserva(any(), any())).thenThrow(new EntityNotFoundException());
        //WHEN
        var response = mockMvc.perform(put("/reservas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizacion.write(datosUpdate).getJson()))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 al actualizar correctamente")
    @WithMockCustomUser(id = 1L)
    void actualizar_EscenarioExitoso() throws Exception {
        //GIVEN
        var datosUpdate = new DatosActualizacionReserva(1L,fechaInicio.plusDays(1), fechaFin.plusDays(1));
        var reservaActualizada = new Reserva(1L, usuarioMock, prestacionMock, fechaInicio.plusDays(1), fechaFin.plusDays(1), Estado.RESERVADO);

        when(reservaService.actualizarReserva(any(), any())).thenReturn(reservaActualizada);
        //WHEN
        var response = mockMvc.perform(put("/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizacion.write(datosUpdate).getJson()))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 si ID no existe")
    @WithMockUser(roles = "ADMIN")
    void eliminar_EscenarioFallido_IdNoExiste() throws Exception {
        //GIVEN
        doThrow(new EntityNotFoundException()).when(reservaService).eliminarReserva(999L);
        //WHEN
        var response = mockMvc.perform(delete("/reservas/999"))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 204 al eliminar")
    @WithMockUser(roles = "ADMIN")
    void eliminar_EscenarioExitoso() throws Exception {
        //GIVEN & WHEN
        var response = mockMvc.perform(delete("/reservas/1"))
                .andReturn().getResponse();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}