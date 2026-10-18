package com.spring.beatmarket.infrastructure.upload;

import com.spring.beatmarket.domain.catalog.ImageProcessorPort;
import com.spring.beatmarket.infrastructure.image.ImageFormats;

public final class ImageUploadPolicies {
    private static final String INVALID_IMAGE_FORMAT_MESSAGE =
            "Invalid file format. Only JPG, JPEG, PNG, and WebP files are allowed.";
    public static final UploadFilePolicy ALBUM_COVER = create();
    public static final UploadFilePolicy ARTIST_IMAGE = create();

    private ImageUploadPolicies() {
    }

    private static UploadFilePolicy create() {
        return new UploadFilePolicy(
                ImageFormats.SUPPORTED_EXTENSIONS,
                ImageProcessorPort.MAX_FILE_SIZE_BYTES,
                ImageUploadPolicies.INVALID_IMAGE_FORMAT_MESSAGE
        );
    }
}
