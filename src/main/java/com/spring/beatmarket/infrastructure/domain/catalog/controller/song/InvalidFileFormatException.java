package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import lombok.Getter;

@Getter
public class InvalidFileFormatException extends RuntimeException {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}