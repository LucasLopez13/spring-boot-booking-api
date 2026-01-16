package com.lucaslopez.booking_api.domain;

public class ValidacionException extends RuntimeException {
    public ValidacionException(String message) {
        super(message);
    }
}
