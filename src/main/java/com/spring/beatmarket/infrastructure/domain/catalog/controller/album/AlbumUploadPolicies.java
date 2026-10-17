package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumCoverImageProcessorPort;
import com.spring.beatmarket.infrastructure.upload.UploadFilePolicy;

import java.util.Set;

final class AlbumUploadPolicies {
    static final UploadFilePolicy COVER = new UploadFilePolicy(
            Set.of(".jpg", ".jpeg", ".png", ".webp"),
            AlbumCoverImageProcessorPort.MAX_FILE_SIZE_BYTES,
            "Invalid file format. Only JPG, JPEG, PNG, and WebP files are allowed."
    );

    private AlbumUploadPolicies() {
    }
}
