package com.spring.beatmarket.domain.catalog;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFileStorageAdapter implements FileStoragePort {
    private final Map<String, byte[]> storage = new HashMap<>();

    @Override
    public String upload(byte[] content, String fileKey) {
        storage.put(fileKey, content);
        return fileKey;
    }

    @Override
    public void delete(String fileKey) {
        storage.remove(fileKey);
    }

    public boolean containsFile(String fileKey) {
        return storage.containsKey(fileKey);
    }
}