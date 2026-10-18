package com.spring.beatmarket.domain.catalog.dto;

public interface ImageDto {
    record Upload(Long id, String message, String imageUrl) {
    }
}
