package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class ArtistImageUpdater {
    private final ArtistRetriever artistRetriever;
    private final FileKeyGenerator fileKeyGenerator;
    private final ImageUploader imageUploader;

    ImageDto.Upload update(final Long artistId, final byte[] imageBytes) {
        Artist artist = artistRetriever.getActive(artistId);
        String previousFileKey = artist.getImageFileKey();
        String newFileKey = fileKeyGenerator.generate("artists", artist.getName(), "webp");
        String imageUrl = imageUploader.upload(imageBytes, newFileKey, previousFileKey);
        artist.changeImageFileKey(newFileKey);

        log.info("Successfully uploaded artist image for artist id={}", artistId);
        return new ImageDto.Upload(artist.getId(), "Artist image uploaded successfully", imageUrl);
    }
}
