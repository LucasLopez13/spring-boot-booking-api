package com.lucaslopez.booking_api.infra.exception;

import com.lucaslopez.booking_api.domain.ValidacionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity tratarErrorDeJsonMalFormado(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException errorFormato) {
            if (errorFormato.getTargetType().isEnum()) {
                var valoresPermitidos = errorFormato.getTargetType().getEnumConstants();
                return ResponseEntity.badRequest().body(new DatosErrorValidacion("enum",
                        "Valor inválido. Valores permitidos: " + Arrays.toString(valoresPermitidos)));
            }
        }
        return ResponseEntity.badRequest()
                .body(new DatosErrorValidacion("body", "Error en el formato del JSON o valor inválido"));
    }

    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
