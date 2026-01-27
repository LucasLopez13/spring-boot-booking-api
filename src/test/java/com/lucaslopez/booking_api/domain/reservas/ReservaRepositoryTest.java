package com.lucaslopez.booking_api.domain.reservas;

import com.lucaslopez.booking_api.domain.prestaciones.Prestacion;
import com.lucaslopez.booking_api.domain.prestaciones.Tipo;
import com.lucaslopez.booking_api.domain.usuarios.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservaRepositoryTest {
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Debe devolver TRUE si ya existe una reserva en ese horario (Solapamiento)")
    void existsSolapamiento_EscenarioOcupado() {
        // GIVEN
        // Necesitamos crear las entidades relacionadas primero (Usuario y Prestacion)
        var usuario = registrarUsuario("Lucas", "lucas@email.com");
        var prestacion = registrarPrestacion("Cancha 1", Tipo.PADEL);

        // Creamos una reserva existente de 10:00 a 11:00
        var fechaInicio = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        var fechaFin = fechaInicio.plusHours(1);
        registrarReserva(usuario, prestacion, fechaInicio, fechaFin);

        // WHEN
        // Intentamos verificar si podemos reservar en el mismo horario (Solapamiento total)
        var existeSolapamiento = reservaRepository.existsSolapamiento(
                prestacion.getId(),
                fechaInicio,
                fechaFin
        );

        // THEN
        assertThat(existeSolapamiento).isTrue();
    }

    @Test
    @DisplayName("Debe devolver FALSE si el horario está libre")
    void existsSolapamiento_EscenarioLibre() {
        // GIVEN
        var usuario = registrarUsuario("Lucas", "lucas@email.com");
        var prestacion = registrarPrestacion("Cancha 1", Tipo.PADEL);

        // Existe una reserva de 10:00 a 11:00
        var fechaInicio = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        var fechaFin = fechaInicio.plusHours(1);
        registrarReserva(usuario, prestacion, fechaInicio, fechaFin);

        // WHEN
        // Consultamos por un horario posterior (12:00 a 13:00)
        var existeSolapamiento = reservaRepository.existsSolapamiento(
                prestacion.getId(),
                LocalDateTime.now().plusDays(1).withHour(12),
                LocalDateTime.now().plusDays(1).withHour(13)
        );

        // THEN
        assertThat(existeSolapamiento).isFalse();
    }

    //Métodos Auxiliares.

    private Usuario registrarUsuario(String nombre, String email) {
        var usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .contrasenia("123456") // Datos obligatorios
                .build();

        return em.persist(usuario);
    }

    private Prestacion registrarPrestacion(String nombre, Tipo tipo) {
        var prestacion = Prestacion.builder()
                .nombre(nombre)
                .tipo(tipo)
                .build();

        return em.persist(prestacion);
    }

    private void registrarReserva(Usuario u, Prestacion p, LocalDateTime inicio, LocalDateTime fin) {
        var reserva = Reserva.builder()
                .usuario(u)
                .prestacion(p)
                .fechaDeInicio(inicio)
                .fechaDeFin(fin)
                .estado(Estado.RESERVADO)
                .build();

        em.persist(reserva);
    }
}