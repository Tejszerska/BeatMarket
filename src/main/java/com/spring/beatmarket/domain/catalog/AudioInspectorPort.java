package com.spring.beatmarket.domain.catalog;

public interface AudioInspectorPort {
    AudioFileExtension inspectExtension(byte[] audioBytes);
    Double inspectDuration(byte[] audioBytes, AudioFileExtension extension);
}
