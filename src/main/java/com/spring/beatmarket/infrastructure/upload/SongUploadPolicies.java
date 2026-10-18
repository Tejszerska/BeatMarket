package com.spring.beatmarket.infrastructure.upload;

import java.util.Set;

public final class SongUploadPolicies {
    public static final UploadFilePolicy FULL_TRACK = new UploadFilePolicy(
            Set.of(".wav", ".flac"),
            165L * 1024 * 1024,
            "Invalid file format. Only audio/wav (WAV) and audio/flac (FLAC) are supported."
    );
    public static final UploadFilePolicy PREVIEW = new UploadFilePolicy(
            Set.of(".mp3", ".aac"),
            40L * 1024 * 1024,
            "Invalid file format. Only audio/mpeg (MP3) and audio/aac (AAC) are supported."
    );

    private SongUploadPolicies() {
    }
}
