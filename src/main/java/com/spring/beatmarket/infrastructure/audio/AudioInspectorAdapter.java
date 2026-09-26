package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.AudioInspectorPort;
import com.spring.beatmarket.domain.catalog.audio.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.audio.AudioMetadata;
import com.spring.beatmarket.domain.catalog.audio.UnreadableAudioFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.gagravarr.flac.FlacFile;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.lang.Double.parseDouble;

@Service
@Slf4j
class AudioInspectorAdapter implements AudioInspectorPort {

    @Override
    public AudioMetadata inspect(final byte[] audioBytes) {
        Metadata metadata;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes)) {
            metadata = TikaAnalysis.extractMetadatatUsingParser(inputStream);
        } catch (IOException | SAXException | TikaException e) {
            log.error("Error parsing audio file with Tika", e);
            throw new UnreadableAudioFileException("An error occurred while analysing audio file.");
        }

        String contentType = Metadata.CONTENT_TYPE;
        AudioFileExtension extension = mapContentTypeToExtension(metadata.get(contentType));

        String durationString = metadata.get("xmpDM:duration");
        Double durationInSeconds = null;

        if (durationString != null) {
            try {
                durationInSeconds = parseDouble(durationString) / 1000.0;
            } catch (NumberFormatException e) {
                log.warn("Error while parsing audio files duration from Tika's Metadata: {}", durationString);
            }
        }

        // if getting duration from metadata fails fallback to hardware-based frame reading from the audio stream itself
        if (durationInSeconds == null) {
            durationInSeconds = calculateDurationFromBytes(extension, audioBytes);
        }

        return new AudioMetadata(extension, durationInSeconds);
    }

    /**
     * Stops the process and throws an exception because getting an improper
     * number of frames or sample rate indicates the file header is corrupted.
     */
    private Double calculateDurationFromBytes(final AudioFileExtension extension, final byte[] audioBytes) {
        if (extension == AudioFileExtension.WAV) {
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes))) {
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                return (frames + 0.0) / format.getFrameRate();
            } catch (UnsupportedAudioFileException | IOException e) {
                log.error("Error parsing audio file with AudioInputStream", e);
                throw new UnreadableAudioFileException("An error occurred while analysing duration of a WAV audio file.");
            }
        }
        if (extension == AudioFileExtension.FLAC) {
            try (FlacFile flacFile = FlacFile.open(new ByteArrayInputStream(audioBytes))) {
                long frames = flacFile.getInfo().getNumberOfSamples();
                return (frames + 0.0) / flacFile.getInfo().getSampleRate();
            } catch (IOException e) {
                log.error("Error parsing audio file with FlacFile", e);
                throw new UnreadableAudioFileException("An error occurred while analysing duration of a FLAC audio file.");
            }
        }
        return null;
    }


    private AudioFileExtension mapContentTypeToExtension(String contentType) {
        if (contentType == null) {
            return null;
        }
        String lowerCaseType = contentType.toLowerCase();
        if (lowerCaseType.contains("wav") || lowerCaseType.contains("wave")) {
            return AudioFileExtension.WAV;
        } else if (lowerCaseType.contains("flac")) {
            return AudioFileExtension.FLAC;
        } else if (lowerCaseType.contains("mp3") || lowerCaseType.contains("mpeg")) {
            return AudioFileExtension.MP3;
        }

        return null;
    }
}
