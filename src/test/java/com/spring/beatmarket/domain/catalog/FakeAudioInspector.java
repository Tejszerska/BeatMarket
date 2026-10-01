package com.spring.beatmarket.domain.catalog;

public class FakeAudioInspector implements AudioInspectorPort {

    private AudioFileExtension extensionToReturn = AudioFileExtension.WAV;
    private Double durationToReturn = 100.0;

    @Override
    public AudioFileExtension inspectExtension(final byte[] audioBytes) {
        return extensionToReturn;
    }

    @Override
    public Double inspectDuration(final byte[] audioBytes, final AudioFileExtension extension) {
        return durationToReturn;
    }

    public void setExtensionToReturn(AudioFileExtension extensionToReturn) {
        this.extensionToReturn = extensionToReturn;
    }

    public void setDurationToReturn(Double durationToReturn) {
        this.durationToReturn = durationToReturn;
    }
}