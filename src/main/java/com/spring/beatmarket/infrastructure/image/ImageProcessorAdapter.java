package com.spring.beatmarket.infrastructure.image;

import com.spring.beatmarket.domain.catalog.ImageProcessorPort;
import com.spring.beatmarket.domain.catalog.exception.ImageFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.InvalidImageException;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

@Service
class ImageProcessorAdapter implements ImageProcessorPort {
    private static final int OUTPUT_SIZE = 150;
    private static final long MAX_PIXEL_COUNT = 40_000_000;
    private static final float WEBP_QUALITY = 0.8f;

    @Override
    public byte[] process(final byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new InvalidImageException("Uploaded file must not be empty.");
        }
        if (imageBytes.length > MAX_FILE_SIZE_BYTES) {
            throw new ImageFileTooBigException(imageBytes.length, MAX_FILE_SIZE_BYTES);
        }

        BufferedImage decodedImage = decode(imageBytes);
        BufferedImage croppedImage = centerCropAndResize(decodedImage);
        return encodeAsWebP(croppedImage);
    }

    private BufferedImage decode(final byte[] imageBytes) {
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes))) {
            if (input == null) {
                throw invalidImage();
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw invalidImage();
            }

            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!ImageFormats.SUPPORTED_FORMATS.contains(format)) {
                    throw invalidImage();
                }

                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    throw invalidImage();
                }
                if ((long) width * height > MAX_PIXEL_COUNT) {
                    throw new InvalidImageException("Image dimensions cannot exceed 40 megapixels.");
                }

                BufferedImage image = reader.read(0);
                if (image == null) {
                    throw invalidImage();
                }
                return image;
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw invalidImage();
        }
    }

    private BufferedImage centerCropAndResize(final BufferedImage source) {
        int cropSize = Math.min(source.getWidth(), source.getHeight());
        int cropX = (source.getWidth() - cropSize) / 2;
        int cropY = (source.getHeight() - cropSize) / 2;
        BufferedImage output = new BufferedImage(OUTPUT_SIZE, OUTPUT_SIZE, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = output.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(
                    source,
                    0,
                    0,
                    OUTPUT_SIZE,
                    OUTPUT_SIZE,
                    cropX,
                    cropY,
                    cropX + cropSize,
                    cropY + cropSize,
                    null
            );
        } finally {
            graphics.dispose();
        }
        return output;
    }

    private byte[] encodeAsWebP(final BufferedImage image) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No WebP image writer is available.");
        }

        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            if (output == null) {
                throw new IllegalStateException("Unable to create an image output stream.");
            }

            writer.setOutput(output);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            if (!writeParam.canWriteCompressed()) {
                throw new IllegalStateException("The WebP image writer does not support quality configuration.");
            }
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            String[] compressionTypes = writeParam.getCompressionTypes();
            if (compressionTypes == null || compressionTypes.length == 0) {
                throw new IllegalStateException("The WebP image writer does not expose a compression type.");
            }
            writeParam.setCompressionType(compressionTypes[0]);
            writeParam.setCompressionQuality(WEBP_QUALITY);
            writer.write(null, new IIOImage(image, null, null), writeParam);
            output.flush();
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to encode the image as WebP.", exception);
        } finally {
            writer.dispose();
        }
    }

    private InvalidImageException invalidImage() {
        return new InvalidImageException("Only valid JPEG, PNG, or WebP image files are supported.");
    }
}
