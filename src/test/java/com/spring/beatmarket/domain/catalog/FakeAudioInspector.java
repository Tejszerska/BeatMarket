package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.audio.AudioMetadata;

public class FakeAudioInspector implements AudioInspectorPort {
    @Override
    public AudioMetadata inspect(final byte[] audioBytes) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
