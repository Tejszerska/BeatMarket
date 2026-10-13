package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AacAdtsParser {
    static final int[] AAC_SAMPLE_RATES = {
            96000, 88200, 64000, 48000, 44100, 32000, 24000,
            22050, 16000, 12000, 11025, 8000, 7350
    };
    static Double calculateAacAdtsDuration(final byte[] audioBytes) {
        if (audioBytes.length < 7) {
            throw unreadableAacFile();
        }

        double durationInSeconds = 0.0;
        int offset = 0;

        while (offset < audioBytes.length) {
            AacAdtsFrame frame = parseAacAdtsFrame(audioBytes, offset);
            durationInSeconds += frame.durationInSeconds();
            offset += frame.length();
        }

        return durationInSeconds;
    }

    static AacAdtsFrame parseAacAdtsFrame(final byte[] audioBytes, final int offset) {
        int remainingBytes = audioBytes.length - offset;
        if (remainingBytes < 7) {
            throw unreadableAacFile();
        }

        int secondHeaderByte = Byte.toUnsignedInt(audioBytes[offset + 1]);
        if (Byte.toUnsignedInt(audioBytes[offset]) != 0xFF
                || (secondHeaderByte & 0xF0) != 0xF0
                || (secondHeaderByte & 0x06) != 0) {
            throw unreadableAacFile();
        }

        int thirdHeaderByte = Byte.toUnsignedInt(audioBytes[offset + 2]);
        int sampleRateIndex = (thirdHeaderByte & 0x3C) >> 2;
        if (sampleRateIndex >= AAC_SAMPLE_RATES.length) {
            throw unreadableAacFile();
        }

        int headerLength = (secondHeaderByte & 0x01) == 1 ? 7 : 9;
        int frameLength = ((Byte.toUnsignedInt(audioBytes[offset + 3]) & 0x03) << 11)
                | (Byte.toUnsignedInt(audioBytes[offset + 4]) << 3)
                | ((Byte.toUnsignedInt(audioBytes[offset + 5]) & 0xE0) >> 5);
        if (frameLength < headerLength || frameLength > remainingBytes) {
            throw unreadableAacFile();
        }

        int rawDataBlocks = (Byte.toUnsignedInt(audioBytes[offset + 6]) & 0x03) + 1;
        double frameDurationInSeconds = rawDataBlocks * 1024.0 / AAC_SAMPLE_RATES[sampleRateIndex];
        return new AacAdtsFrame(frameLength, frameDurationInSeconds);
    }

    record AacAdtsFrame(int length, double durationInSeconds) {
    }

    static UnreadableAudioFileException unreadableAacFile() {
        log.error("Invalid or truncated AAC ADTS frame");
        return new UnreadableAudioFileException("An error occurred while analysing duration of an AAC audio file.");
    }
}
