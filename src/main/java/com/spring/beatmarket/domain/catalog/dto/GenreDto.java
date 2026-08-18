package com.spring.beatmarket.domain.catalog.dto;

public interface GenreDto {

    record Create(String name) {
    }

    record Update(String name) {
    }

    record Details(Long id, String name) {
    }

    record Summary(Long id, String name) {
    }

    record Reference(Long id, String name) {
    }
    record Info(Long id, String name) {
    }

}