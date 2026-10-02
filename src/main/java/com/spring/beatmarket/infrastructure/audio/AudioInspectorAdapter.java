package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.AudioInspectorPort;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.gagravarr.flac.FlacFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
class AudioInspectorAdapter implements AudioInspectorPort {

    /**
     * Checks "magic bytes" to extract file extension without parsing full file
     */
    @Override
    public AudioFileExtension inspectExtension(final byte[] audioBytes) {
        Tika tika = new Tika();
        String contentType = tika.detect(audioBytes);
        return mapContentTypeToExtension(contentType);
    }

    @Override
    public Double inspectDuration(final byte[] audioBytes, final AudioFileExtension extension) {
        Metadata metadata;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes)) {
            metadata = TikaAnalysis.extractMetadatatUsingParser(inputStream);
        } catch (IOException | SAXException | TikaException e) {
            log.error("Error parsing audio file with Tika", e);
            throw new UnreadableAudioFileException("An error occurred while analysing audio file.");
        }

        // Value is in Seconds, unless xmpDM:scale is also set.
        String durationString = metadata.get("xmpDM:duration");
        String scalingString = metadata.get("xmpDM:scale");
        Double durationInSeconds = null;

        if (durationString != null) {
            try {
                double scaling = 1.0;
                if (scalingString != null) {
                    if (scalingString.contains("/")) {
                        String[] parts = scalingString.split("/");
                        scaling = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
                    } else {
                        scaling = Double.parseDouble(scalingString);
                    }
                }
                durationInSeconds = Double.parseDouble(durationString) / scaling;
            } catch (NumberFormatException e) {
                log.warn("Error parsing audio duration or scale from Tika's Metadata. Duration: {}, Scale: {}", durationString, scalingString);
            }
        }

        // if getting duration from metadata fails fallback to hardware-based frame reading from the audio stream itself
        if (durationInSeconds == null) {
            durationInSeconds = calculateDurationFromBytes(extension, audioBytes);
        }
        return durationInSeconds;
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
            } catch (IOException | IllegalArgumentException e) {
                log.error("Error parsing audio file with FlacFile", e);
                throw new UnreadableAudioFileException("An error occurred while analysing duration of a FLAC audio file.");
            }
        }

        if (extension == AudioFileExtension.MP3) {
            Path tempFile = null;

            try {
                tempFile = Files.createTempFile("temp_audio", ".mp3");
                Files.write(tempFile, audioBytes);
                try {
                    AudioFile audioFile = AudioFileIO.read(tempFile.toFile());
                    return audioFile.getAudioHeader().getTrackLength() + 0.0;
                } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                    log.error("Error parsing file with AudioFile", e);
                    throw new UnreadableAudioFileException("An error occurred while analysing duration of a MP3 audio file.");
                }
            } catch (IOException e) {
                log.error("Error parsing audio file with jaudiotagger", e);
                throw new UnreadableAudioFileException("An error occurred while analysing duration of an MP3 audio file.");
            } finally {
                if (tempFile != null) {
                    try {
                        Files.deleteIfExists(tempFile);
                    } catch (IOException e) {
                        log.warn("Failed to delete temp file", e);
                    }
                }
            }
        }
        return null;
    }


    private AudioFileExtension mapContentTypeToExtension(String contentType) {
        if (contentType == null) {
            return null;
        }
        return switch (contentType.toLowerCase()) {
            case "audio/wav", "audio/vnd.wave", "audio/x-wav" -> AudioFileExtension.WAV;
            case "audio/flac", "audio/x-flac" -> AudioFileExtension.FLAC;
            case "audio/mpeg", "audio/mp3" -> AudioFileExtension.MP3;
            default -> null;
        };
    }
}
