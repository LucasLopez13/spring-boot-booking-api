package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;
import com.lucaslopez.booking_api.domain.reservas.ReservaRepository;
import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDeDisponibilidad implements ValidadorReserva{
    @Autowired
    private ReservaRepository repository;

    @Override
    public void validar(DatosRegistroReserva datos) {
        var ocupado = repository.existsSolapamiento(
                datos.idPrestacion(),
                datos.fechaInicio(),
                datos.fechaFin()
        );

        if(ocupado){
            throw new ValidacionException("Lo sentimos, la prestacion seleccionada ya esta reservada en el horario seleccionado");
        }
    }
}
