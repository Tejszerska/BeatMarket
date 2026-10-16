package com.spring.beatmarket.domain.catalog;

public interface FileStoragePort {
    String upload(byte[] content, String fileKey);

    default String upload(final byte[] content, final String fileKey, final String contentType) {
        return upload(content, fileKey);
    }

    void delete(String fileKey);

    default String urlFor(final String fileKey) {
        return fileKey;
    }
}
