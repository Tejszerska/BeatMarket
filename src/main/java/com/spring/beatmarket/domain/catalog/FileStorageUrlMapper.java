package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FileStorageUrlMapper {
    private final FileStoragePort fileStoragePort;

    @Named("fileStorageUrl")
    String toUrl(final String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return null;
        }
        return fileStoragePort.urlFor(fileKey);
    }
}
