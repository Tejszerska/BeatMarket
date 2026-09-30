package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.AudioFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.IncorrectAudioFormatException;
import com.spring.beatmarket.domain.catalog.exception.SongDurationMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class AudioFileValidator {
    private final AudioInspectorPort inspectorPort;
    // The file size limitation stems from the assumed 16-bit quality and max duration= 15 min
    private static final double MAX_WAV_SIZE = 165.00; // [MB]
    private static final double MAX_FLAC_SIZE = 95.00; // [MB]

    AudioFileExtension validateFullTrack(final byte[] trackBytes, final int databaseDuration) {
        double fileMB = trackBytes.length / (1024.0 * 1024.0);

        AudioFileExtension extension = inspectorPort.inspectExtension(trackBytes);

        if (extension != AudioFileExtension.FLAC
            && extension != AudioFileExtension.WAV) {
            throw new IncorrectAudioFormatException("Only .WAV or .FLAC files accepted as full track files.");
        }

        if ((extension == AudioFileExtension.WAV) && fileMB > MAX_WAV_SIZE) {
            throw new AudioFileTooBigException(fileMB, MAX_WAV_SIZE);
        }

        if ((extension == AudioFileExtension.FLAC) && fileMB > MAX_FLAC_SIZE) {
            throw new AudioFileTooBigException(fileMB, MAX_FLAC_SIZE);
        }

        Double fileDuration = inspectorPort.inspectDuration(trackBytes, extension);

        if (fileDuration> databaseDuration + 3
                || fileDuration < databaseDuration - 3) {
            throw new SongDurationMismatchException(databaseDuration);
        }

        return extension;
    }

}