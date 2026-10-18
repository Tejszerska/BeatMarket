package com.spring.beatmarket.domain.catalog;

public interface ImageProcessorPort {
    long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    byte[] process(byte[] imageBytes);
}
