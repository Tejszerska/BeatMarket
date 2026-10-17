package com.spring.beatmarket.infrastructure.upload;

import java.util.Set;

public record UploadFilePolicy(
        Set<String> allowedExtensions,
        long maxSizeBytes,
        String invalidFormatMessage) {

    public UploadFilePolicy {
        allowedExtensions = Set.copyOf(allowedExtensions);
        if (allowedExtensions.isEmpty()) {
            throw new IllegalArgumentException("At least one allowed file extension is required.");
        }
        if (maxSizeBytes <= 0) {
            throw new IllegalArgumentException("Maximum file size must be greater than zero.");
        }
    }
}
