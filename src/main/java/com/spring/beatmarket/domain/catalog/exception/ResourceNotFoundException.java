package com.spring.beatmarket.domain.catalog.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(String.format("%s with id %d not found", resourceName, resourceId));
    }
}
