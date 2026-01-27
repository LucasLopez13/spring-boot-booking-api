package com.lucaslopez.booking_api.domain.prestaciones;

import com.lucaslopez.booking_api.domain.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestacionService {

    @Autowired
    private PrestacionRepository prestacionRepository;

    public Prestacion registrarPrestacion(DatosRegistroPrestacion datos) {
        validarNombreUnico(datos.nombre());

        var prestacion = new Prestacion(datos);

        return prestacionRepository.save(prestacion);
    }

    public DatosDetallePrestacion detallarPrestacion(Long id) {
        var prestacion = buscarPrestacionPorId(id);
        return new DatosDetallePrestacion(prestacion);
    }

    public Prestacion actualizarPrestacion(Long id, DatosActualizacionPrestacion datos) {
        var prestacion = buscarPrestacionPorId(id);

        if (datos.nombre() != null && !datos.nombre().equals(prestacion.getNombre())) {
            validarNombreUnico(datos.nombre());
        }

        prestacion.actualizarDatos(datos);

        return prestacion;
    }

    public void eliminarPrestacion(Long id) {
        var prestacion = buscarPrestacionPorId(id);
        prestacionRepository.deleteById(prestacion.getId());
    }

    private Prestacion buscarPrestacionPorId(Long id) {
        return prestacionRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("No existe una prestacion con ese ID"));
    }

    private void validarNombreUnico(String nombre) {
        if (prestacionRepository.existsByNombre(nombre)) {
            throw new ValidacionException("Ya existe una prestacion con ese nombre");
        }
    }
}
