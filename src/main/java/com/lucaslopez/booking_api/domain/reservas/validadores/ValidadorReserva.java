package com.lucaslopez.booking_api.domain.reservas.validadores;

import com.lucaslopez.booking_api.domain.reservas.DatosRegistroReserva;

public interface ValidadorReserva {
    void validar(DatosRegistroReserva datos);
}
