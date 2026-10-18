package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class AlbumCoverUpdater {
    private final AlbumRetriever albumRetriever;
    private final FileKeyGenerator fileKeyGenerator;
    private final ImageUploader imageUploader;

    AlbumDto.Cover update(final Long albumId, final byte[] imageBytes) {
        Album album = albumRetriever.getActive(albumId);
        String previousFileKey = album.getCoverFileKey();
        String newFileKey = fileKeyGenerator.generate("covers", album.getTitle(), "webp");
        String imageUrl = imageUploader.upload(imageBytes, newFileKey, previousFileKey);
        album.changeCoverFileKey(newFileKey);

        log.info("Successfully uploaded album cover for album id={}", albumId);
        return new AlbumDto.Cover(album.getId(), "Cover uploaded successfully", imageUrl);
    }
}
