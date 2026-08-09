package com.spring.beatmarket.domain.catalog.exception;

public class MissingRequiredFieldException extends RuntimeException {
    public MissingRequiredFieldException(String fieldName) {
        super(String.format("Required field '%s' cannot be blank or null.", fieldName));
    }
}
