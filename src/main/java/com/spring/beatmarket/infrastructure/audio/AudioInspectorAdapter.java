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
    private static final int[] AAC_SAMPLE_RATES = {
            96000, 88200, 64000, 48000, 44100, 32000, 24000,
            22050, 16000, 12000, 11025, 8000, 7350
    };

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

        if (extension == AudioFileExtension.AAC) {
            return calculateAacAdtsDuration(audioBytes);
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

    private Double calculateAacAdtsDuration(final byte[] audioBytes) {
        if (audioBytes.length < 7) {
            throw unreadableAacFile();
        }

        double durationInSeconds = 0.0;
        int offset = 0;

        while (offset < audioBytes.length) {
            int remainingBytes = audioBytes.length - offset;
            // Each ADTS frame has a minimum header size of 7 bytes (without CRC) or 9 bytes (with CRC)
            if (remainingBytes < 7) {
                throw unreadableAacFile();
            }

            // Check for ADTS syncword (0xFFF) and other header bits
            int secondHeaderByte = Byte.toUnsignedInt(audioBytes[offset + 1]);
            if (Byte.toUnsignedInt(audioBytes[offset]) != 0xFF
                    || (secondHeaderByte & 0xF0) != 0xF0
                    || (secondHeaderByte & 0x06) != 0) {
                throw unreadableAacFile();
            }

            // Extract sample rate index from the ADTS header
            int thirdHeaderByte = Byte.toUnsignedInt(audioBytes[offset + 2]);
            int sampleRateIndex = (thirdHeaderByte & 0x3C) >> 2;
            if (sampleRateIndex >= AAC_SAMPLE_RATES.length) {
                throw unreadableAacFile();
            }

            // Calculate the frame length from the ADTS header
            int headerLength = (secondHeaderByte & 0x01) == 1 ? 7 : 9;
            int frameLength = ((Byte.toUnsignedInt(audioBytes[offset + 3]) & 0x03) << 11)
                    | (Byte.toUnsignedInt(audioBytes[offset + 4]) << 3)
                    | ((Byte.toUnsignedInt(audioBytes[offset + 5]) & 0xE0) >> 5);
            if (frameLength < headerLength || frameLength > remainingBytes) {
                throw unreadableAacFile();
            }

            int rawDataBlocks = (Byte.toUnsignedInt(audioBytes[offset + 6]) & 0x03) + 1;
            durationInSeconds += rawDataBlocks * 1024.0 / AAC_SAMPLE_RATES[sampleRateIndex];
            offset += frameLength;
        }

        return durationInSeconds;
    }

    private UnreadableAudioFileException unreadableAacFile() {
        log.error("Invalid or truncated AAC ADTS frame");
        return new UnreadableAudioFileException("An error occurred while analysing duration of an AAC audio file.");
    }


    private AudioFileExtension mapContentTypeToExtension(String contentType) {
        if (contentType == null) {
            return null;
        }
        return switch (contentType.toLowerCase()) {
            case "audio/wav", "audio/vnd.wave", "audio/x-wav" -> AudioFileExtension.WAV;
            case "audio/flac", "audio/x-flac" -> AudioFileExtension.FLAC;
            case "audio/mpeg", "audio/mp3" -> AudioFileExtension.MP3;
            case "audio/aac", "audio/x-aac" -> AudioFileExtension.AAC;
            default -> null;
        };
    }
}
