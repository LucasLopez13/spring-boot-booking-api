package com.lucaslopez.booking_api.domain.reservas;

import com.lucaslopez.booking_api.domain.prestaciones.PrestacionRepository;
import com.lucaslopez.booking_api.domain.reservas.validadores.ValidadorReserva;
import com.lucaslopez.booking_api.domain.usuarios.UsuarioRepository;
import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PrestacionRepository prestacionRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private List<ValidadorReserva> validadores;

    public Reserva registrarReserva(DatosRegistroReserva datos) {
        validadores.forEach(v -> v.validar(datos));

        var usuario = usuarioRepository.findById(datos.idUsuario())
                .orElseThrow(() -> new ValidacionException("No existe un usuario con ese ID"));

        var prestacion = prestacionRepository.findById(datos.idPrestacion())
                .orElseThrow(() -> new ValidacionException("No existe un prestacion con ese ID"));

        var reserva = new Reserva(datos,usuario,prestacion);

        usuario.agregarReserva(reserva);

        return reservaRepository.save(reserva);
    }

    public Reserva detallarReserva(Long id) {
        return buscarReservaPorId(id);
    }

    public Reserva actualizarReserva(Long id, DatosActualizacionReserva datos) {
        var reserva = buscarReservaPorId(id);

        var idFinal = datos.idPrestacion() != null ? datos.idPrestacion() : reserva.getPrestacion().getId();
        var fechaInicio = datos.fechaInicio() != null ? datos.fechaInicio() : reserva.getFechaDeInicio();
        var fechaFinal = datos.fechaFin() != null ? datos.fechaFin() : reserva.getFechaDeFin();

        var datosParaValidar = new DatosRegistroReserva(reserva.getUsuario().getId(),idFinal,fechaInicio,fechaFinal);

        validadores.forEach(v -> v.validar(datosParaValidar));

        if (datos.idPrestacion() != null) {
            var nuevaPrestacion = prestacionRepository.findById(datos.idPrestacion())
                    .orElseThrow(() -> new ValidacionException("No existe un prestacion con ese ID"));
            reserva.actualizarReserva(nuevaPrestacion);
        }
        if (datos.fechaInicio() != null) reserva.setFechaInicio(datos.fechaInicio());
        if (datos.fechaFin() != null) reserva.setFechaFin(datos.fechaFin());

        return reserva;
    }

    public void eliminarReserva(Long id) {
        var reserva = buscarReservaPorId(id);
        reservaRepository.deleteById(reserva.getId());
    }

    private Reserva buscarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Reserva no encontrada"));
    }
}
