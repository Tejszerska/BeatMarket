package com.spring.beatmarket.infrastructure.image;

import java.util.Set;
import java.util.stream.Collectors;

public final class ImageFormats {
    public static final Set<String> SUPPORTED_FORMATS = Set.of("jpg", "jpeg", "png", "webp");
    public static final Set<String> SUPPORTED_EXTENSIONS = SUPPORTED_FORMATS.stream()
            .map(format -> "." + format)
            .collect(Collectors.toUnmodifiableSet());

    private ImageFormats() {
    }
}
