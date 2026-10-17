package com.spring.beatmarket.domain.catalog;

public interface FileStoragePort {
    String upload(byte[] content, String fileKey, String contentType);

    void delete(String fileKey);

    String urlFor(String fileKey);
}
