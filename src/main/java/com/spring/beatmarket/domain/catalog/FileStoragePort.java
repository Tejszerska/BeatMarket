package com.spring.beatmarket.domain.catalog;

public interface FileStoragePort {
    String upload(byte[] content, String fileKey);

    void delete(String fileKey);
}
