package com.lucaslopez.booking_api.controllers;

import com.lucaslopez.booking_api.domain.prestaciones.*;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PrestacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosRegistroPrestacion> datosRegistroPrestacionJson;

    @Autowired
    private JacksonTester<DatosDetallePrestacion> datosDetallePrestacionJson;

    @MockBean
    private PrestacionService prestacionService;

    @Autowired
    private JacksonTester<DatosActualizacionPrestacion> datosActualizacionJson;

    @MockBean
    private PrestacionRepository prestacionRepository;

    @Test
    @DisplayName("Debe devolver HTTP 400 cuando los datos no son válidos")
    @WithMockUser(roles = "ADMIN")
    void registrar_EscenarioFallido() throws Exception {
        //Guardo en una variable la URL a testear.
        //GIVEN & WHEN
        var response = mockMvc.perform(post("/prestaciones"))
                .andReturn().getResponse();

        //THEN
        //Verifico si el estado de response es igual a HTTP400
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 201 cuando los datos son válidos")
    @WithMockUser(roles = "ADMIN")
    void registrar_EscenarioExitoso() throws Exception {
        //GIVEN
        var tipo = Tipo.CANCHA_FUTBOL;
        var nombre = "CANCHA FUTSAL 1";

        // Simulamos la respuesta: cuando el servicio intente registrar,
        // devolvemos una Prestacion con ID 1 fijo.
        var prestacionSimulada = new Prestacion(1L, nombre, tipo,true);
        when(prestacionService.registrarPrestacion(any())).thenReturn(prestacionSimulada);

        //WHEN
        var response = mockMvc.perform(post("/prestaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosRegistroPrestacionJson.write(
                                        new DatosRegistroPrestacion(nombre, tipo)
                                ).getJson()
                        )
                )
                .andReturn().getResponse();

        var jsonEsperado = datosDetallePrestacionJson.write(
                new DatosDetallePrestacion(1L, nombre, tipo)
        ).getJson();
        //THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y una lista vacía si no hay prestaciones")
    @WithMockUser
    void listar_EscenarioListaVacia() throws Exception {
        // GIVEN
        // Entrenamos al repositorio para devolver una página vacía
        when(prestacionRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        // WHEN
        var response = mockMvc.perform(get("/prestaciones"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // Verificamos que el contenido sea un JSON de página vacía
        assertThat(response.getContentAsString()).contains("\"content\":[]");
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y lista de prestaciones")
    @WithMockUser
    void listar_EscenarioExitoso() throws Exception {
        // GIVEN
        var prestacion = new Prestacion(1L, "Cancha 1", Tipo.PADEL,true);
        // Simulamos una página de Spring Data
        var pagina = new PageImpl<>(List.of(prestacion));

        // Entrenamos al REPOSITORIO (porque el controller llama al repo directo)
        when(prestacionRepository.findAll(any(Pageable.class))).thenReturn(pagina);

        // WHEN
        var response = mockMvc.perform(get("/prestaciones"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        //Verificar que el JSON contenga "Cancha 1"
        assertThat(response.getContentAsString()).contains("Cancha 1");
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 cuando se busca un ID inexistente")
    @WithMockUser
    void detallar_EscenarioFallido_IdNoExiste() throws Exception {
        // GIVEN
        // Simulamos que el servicio lanza la excepción típica de JPA cuando no encuentra algo
        when(prestacionService.detallarPrestacion(999L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        // WHEN
        var response = mockMvc.perform(get("/prestaciones/999"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y detalle al buscar por ID")
    @WithMockUser
    void detallar_EscenarioExitoso() throws Exception {
        // GIVEN
        var prestacion = new DatosDetallePrestacion(1L,"Cancha 1", Tipo.CANCHA_FUTBOL);
        when(prestacionService.detallarPrestacion(1L)).thenReturn(prestacion);

        // WHEN
        var response = mockMvc.perform(get("/prestaciones/1"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Cancha 1");
    }

    @Test
    @DisplayName("Debe devolver HTTP 400 si los datos de actualización son inválidos")
    @WithMockUser(roles = "ADMIN")
    void actualizar_EscenarioFallido_DatosInvalidos() throws Exception {
        // GIVEN
        // Creamos un DTO con datos inválidos (ej: tipo null ya que tengo @NotNull)
        var datosInvalidos = new DatosActualizacionPrestacion("Cancha 2", null);

        // WHEN
        var response = mockMvc.perform(put("/prestaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosActualizacionJson.write(datosInvalidos).getJson())
                )
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    @DisplayName("Debe devolver HTTP 404 si el ID no existe")
    @WithMockUser(roles = "ADMIN")
    void actualizar_EscenarioFallido_IdNoExiste() throws Exception {
        // GIVEN
        // Entrenamos al mock para que LANCE una excepción cuando lo llamen
        when(prestacionService.actualizarPrestacion(any(), any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        // WHEN
        var response = mockMvc.perform(put("/prestaciones/9999") // ID inexistente
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosActualizacionJson.write(
                                new DatosActualizacionPrestacion("Cancha", Tipo.PADEL)
                        ).getJson())
                )
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 200 y datos actualizados al hacer PUT")
    @WithMockUser(roles = "ADMIN")
    void actualizar_EscenarioExitoso() throws Exception {
        // GIVEN
        var prestacionActualizada = new Prestacion(1L, "Cancha Nueva", Tipo.PADEL, true);

        // Simulamos que el servicio devuelve la prestación ya cambiada
        when(prestacionService.actualizarPrestacion(any(), any())).thenReturn(prestacionActualizada);

        // WHEN
        var response = mockMvc.perform(put("/prestaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosActualizacionJson.write(
                                new DatosActualizacionPrestacion("Cancha Nueva", Tipo.PADEL )
                        ).getJson())
                )
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Cancha Nueva");
    }

    @Test
    @DisplayName("Debe devolver HTTP 404 cuando se intenta eliminar un ID inexistente")
    @WithMockUser(roles = "ADMIN")
    void eliminar_EscenarioFallido_IdNoExiste() throws Exception {
        // GIVEN
        // Usamos doThrow porque el método es void
        doThrow(new jakarta.persistence.EntityNotFoundException())
                .when(prestacionService).eliminarPrestacion(999L);

        // WHEN
        var response = mockMvc.perform(delete("/prestaciones/999"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debe devolver HTTP 204 al eliminar")
    @WithMockUser(roles = "ADMIN")
    void eliminar_EscenarioExitoso() throws Exception {
        // GIVEN

        // WHEN
        var response = mockMvc.perform(delete("/prestaciones/1"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}