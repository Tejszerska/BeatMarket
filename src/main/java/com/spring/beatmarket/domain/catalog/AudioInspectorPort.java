package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.audio.AudioMetadata;

public interface AudioInspectorPort {
    AudioMetadata inspect(byte[] audioBytes);
}
