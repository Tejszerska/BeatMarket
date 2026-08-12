package com.spring.beatmarket.infrastructure.domain.shared;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JsonNullableMapper {
    public <T> Optional<T> mapJsonNullableToOptional(JsonNullable<T> jsonNullable) {
        if (jsonNullable == null || !jsonNullable.isPresent()) {
            return null;
        }
        if (jsonNullable.get() == null) {
            return Optional.empty();
        }
        return Optional.of(jsonNullable.get());
    }
}
