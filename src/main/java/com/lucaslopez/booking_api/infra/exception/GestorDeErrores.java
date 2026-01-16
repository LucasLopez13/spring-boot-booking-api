package com.lucaslopez.booking_api.infra.exception;

import com.lucaslopez.booking_api.domain.ValidacionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestorDeErrores {
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity tratarErrorDeValidacion(ValidacionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErrorDeCredenciales() {
        return ResponseEntity.status(401).body("Error: Credenciales inválidas (usuario o contraseña incorrectos)");
    }
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity tratarErrorDeJsonMalFormado(org.springframework.http.converter.HttpMessageNotReadableException e) {

        if (e.getMessage().contains("com.lucaslopez.booking_api.domain.prestaciones.Tipo")) {
            return ResponseEntity.badRequest().body(new DatosErrorValidacion("tipo", "Valor inválido. Los valores permitidos son: CANCHA_FUTBOL, CANCHA_VOLEY, SALON, QUINCHO, PILETA"));
        }

        return ResponseEntity.badRequest().body(new DatosErrorValidacion("body", "Error en el formato del JSON o valor inválido en uno de los campos"));
    }

    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
