package com.spring.beatmarket.domain.catalog;

public class FakeAudioInspector implements AudioInspectorPort {

    @Override
    public AudioFileExtension inspectExtension(final byte[] audioBytes) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Double inspectDuration(final byte[] audioBytes, final AudioFileExtension extension) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
