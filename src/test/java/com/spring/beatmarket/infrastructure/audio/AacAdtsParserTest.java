package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class AacAdtsParserTest {

    @Test
    @DisplayName("Calculates duration for a single AAC ADTS frame")
    void calculates_duration_for_single_frame() {
        // given
        byte[] frame = createFrame(7, 3, 0);

        // when
        double duration = AacAdtsParser.calculateAacAdtsDuration(frame);

        // then
        // One AAC raw data block has 1024 samples; ADTS index 3 means 48 kHz.
        assertThat(duration).isCloseTo(1024.0 / 48000, within(1e-9));
    }

    @Test
    @DisplayName("Sums durations across AAC ADTS frames")
    void sums_durations_across_frames() {
        // given
        byte[] firstFrame = createFrame(7, 3, 0);
        byte[] secondFrame = createFrame(9, 4, 1);
        byte[] audio = concatenate(firstFrame, secondFrame);

        // when
        double duration = AacAdtsParser.calculateAacAdtsDuration(audio);

        // then
        assertThat(duration).isEqualTo(1024.0 / 48000 + 2048.0 / 44100);
    }

    @Test
    @DisplayName("Rejects an AAC input shorter than one ADTS header")
    void rejects_input_shorter_than_header() {
        // given
        byte[] audio = new byte[6];

        // when & then
        assertUnreadable(() -> AacAdtsParser.calculateAacAdtsDuration(audio));
    }

    @Test
    @DisplayName("Rejects an AAC input with a truncated trailing frame")
    void rejects_truncated_trailing_frame() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        byte[] audio = concatenate(frame, new byte[]{(byte) 0xFF, (byte) 0xF1});

        // when & then
        assertUnreadable(() -> AacAdtsParser.calculateAacAdtsDuration(audio));
    }

    @Test
    @DisplayName("Rejects an invalid ADTS sync word")
    void rejects_invalid_sync_word() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        frame[0] = 0;

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Rejects an invalid ADTS sync nibble")
    void rejects_invalid_sync_nibble() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        frame[1] = (byte) 0xE1;

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Rejects an unsupported ADTS layer")
    void rejects_unsupported_layer() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        frame[1] = (byte) 0xF3;

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Rejects an unsupported AAC sample rate index")
    void rejects_unsupported_sample_rate() {
        // given
        byte[] frame = createFrame(7, 13, 0);

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Rejects a frame shorter than its ADTS header")
    void rejects_frame_shorter_than_header() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        setFrameLength(frame, 6);

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Rejects a frame length that exceeds the available bytes")
    void rejects_frame_length_exceeding_available_bytes() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        setFrameLength(frame, 8);

        // when & then
        assertUnreadable(() -> AacAdtsParser.parseAacAdtsFrame(frame, 0));
    }

    @Test
    @DisplayName("Parses a frame with a CRC header and multiple raw data blocks")
    void parses_crc_header_and_raw_data_blocks() {
        // given
        byte[] frame = createFrame(9, 3, 2);

        // when
        AacAdtsParser.AacAdtsFrame parsed = AacAdtsParser.parseAacAdtsFrame(frame, 0);

        // then
        assertThat(parsed.length()).isEqualTo(9);
        assertThat(parsed.durationInSeconds()).isEqualTo(3072.0 / 48000);
    }

    @Test
    @DisplayName("Parses an AAC ADTS frame starting at the supplied offset")
    void parses_frame_at_offset() {
        // given
        byte[] frame = createFrame(7, 3, 0);
        byte[] audio = concatenate(new byte[]{1, 2, 3}, frame);

        // when
        AacAdtsParser.AacAdtsFrame parsed = AacAdtsParser.parseAacAdtsFrame(audio, 3);

        // then
        assertThat(parsed.length()).isEqualTo(7);
        assertThat(parsed.durationInSeconds()).isEqualTo(1024.0 / 48000);
    }

    private byte[] createFrame(int headerLength, int sampleRateIndex, int rawDataBlocks) {
        byte[] frame = new byte[headerLength];
        frame[0] = (byte) 0xFF;
        frame[1] = (byte) (headerLength == 7 ? 0xF1 : 0xF0);
        frame[2] = (byte) (sampleRateIndex << 2);
        frame[3] = (byte) ((headerLength >> 11) & 0x03);
        frame[4] = (byte) ((headerLength >> 3) & 0xFF);
        frame[5] = (byte) ((headerLength & 0x07) << 5);
        frame[6] = (byte) rawDataBlocks;
        return frame;
    }

    private void setFrameLength(byte[] frame, int frameLength) {
        frame[3] = (byte) ((frameLength >> 11) & 0x03);
        frame[4] = (byte) ((frameLength >> 3) & 0xFF);
        frame[5] = (byte) ((frameLength & 0x07) << 5);
    }

    private byte[] concatenate(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private void assertUnreadable(ThrowableAssert.ThrowingCallable operation) {
        assertThatThrownBy(operation)
                .isInstanceOf(UnreadableAudioFileException.class)
                .hasMessage("An error occurred while analysing duration of an AAC audio file.");
    }
}
