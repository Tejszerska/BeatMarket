package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import lombok.Getter;

@Getter
public class InvalidSearchCriteriaException extends RuntimeException {
    private final String field;
    public InvalidSearchCriteriaException(String field, String message) {
        super(message);
        this.field = field;
    }
}