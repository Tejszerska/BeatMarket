package com.spring.beatmarket.domain.catalog;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFileStorageAdapter implements FileStoragePort {
    private final Map<String, byte[]> storage = new HashMap<>();

    @Override
    public String upload(byte[] content, String fileKey, String contentType) {
        storage.put(fileKey, content);
        return fileKey;
    }

    @Override
    public void delete(String fileKey) {
        storage.remove(fileKey);
    }

    @Override
    public String urlFor(String fileKey) {
        return fileKey;
    }

    public boolean containsFile(String fileKey) {
        return storage.containsKey(fileKey);
    }
}