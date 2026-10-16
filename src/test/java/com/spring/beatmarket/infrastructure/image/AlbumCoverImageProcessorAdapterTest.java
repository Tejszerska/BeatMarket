package com.spring.beatmarket.infrastructure.image;

import com.spring.beatmarket.domain.catalog.AlbumCoverImageProcessorPort;
import com.spring.beatmarket.domain.catalog.exception.AlbumCoverFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.InvalidAlbumCoverException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlbumCoverImageProcessorAdapterTest {
    private final AlbumCoverImageProcessorAdapter imageProcessor = new AlbumCoverImageProcessorAdapter();

    @Test
    @DisplayName("Should center crop and encode an image as a 150 pixel WebP")
    void should_center_crop_and_encode_webp() throws IOException {
        // given
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 200, 100);
        graphics.setColor(Color.RED);
        graphics.fillRect(50, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream inputBytes = new ByteArrayOutputStream();
        ImageIO.write(source, "png", inputBytes);

        // when
        byte[] webpBytes = imageProcessor.process(inputBytes.toByteArray());
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(webpBytes));

        // then
        assertThat(new String(webpBytes, 0, 4)).isEqualTo("RIFF");
        assertThat(new String(webpBytes, 8, 4)).isEqualTo("WEBP");
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(150);
        Color centerPixel = new Color(result.getRGB(75, 75));
        assertThat(centerPixel.getRed()).isGreaterThan(200);
        assertThat(centerPixel.getBlue()).isLessThan(50);
    }

    @Test
    @DisplayName("Should accept JPEG and WebP input images")
    void should_accept_jpeg_and_webp_inputs() throws IOException {
        // given
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream jpegBytes = new ByteArrayOutputStream();
        ImageIO.write(source, "jpeg", jpegBytes);

        // when
        byte[] firstWebp = imageProcessor.process(jpegBytes.toByteArray());
        byte[] secondWebp = imageProcessor.process(firstWebp);

        // then
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(secondWebp));
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should reject bytes that do not contain a supported image")
    void should_reject_unsupported_image_bytes() {
        // given
        byte[] invalidBytes = {1, 2, 3};

        // when & then
        assertThatThrownBy(() -> imageProcessor.process(invalidBytes))
                .isInstanceOf(InvalidAlbumCoverException.class)
                .hasMessage("Only valid JPEG, PNG, or WebP image files are supported.");
    }

    @Test
    @DisplayName("Should reject an image that exceeds the upload size limit")
    void should_reject_image_over_size_limit() {
        // given
        byte[] oversizedBytes = new byte[(int) AlbumCoverImageProcessorPort.MAX_FILE_SIZE_BYTES + 1];

        // when & then
        assertThatThrownBy(() -> imageProcessor.process(oversizedBytes))
                .isInstanceOf(AlbumCoverFileTooBigException.class)
                .hasMessage("Album cover file exceeds the maximum allowed size.");
    }
}
