package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.audio.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.audio.AudioMetadata;
import com.spring.beatmarket.domain.catalog.audio.IncorrectAudioFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class AudioFileValidator {
    private final AudioInspectorPort inspectorPort;

    AudioMetadata validateFullTrack(final byte[] trackBytes) {
        AudioMetadata audioMetadata = inspectorPort.inspect(trackBytes);

            if (audioMetadata.fileExtension() != AudioFileExtension.FLAC &&
                    audioMetadata.fileExtension() != AudioFileExtension.WAV) {
                throw new IncorrectAudioFormat("Only .WAV or .FLAC files accepted as full track files.");
            }

        //@TODO walidacja długość piosenki w bazie vs w metadanych
        //@TODO ustalić granice wielkości plików i tez zwalidować

        return audioMetadata;
    }
}