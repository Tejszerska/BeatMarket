package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.shared.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
class AlbumCoverUpdater {
    private final AlbumRetriever albumRetriever;
    private final AlbumCoverImageProcessorPort imageProcessor;
    private final FileStoragePort fileStoragePort;

    AlbumDto.Cover update(final Long albumId, final byte[] imageBytes) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Album cover updates require an active transaction.");
        }

        Album album = albumRetriever.getActive(albumId);
        byte[] processedImage = imageProcessor.process(imageBytes);
        String previousFileKey = album.getCoverFileKey();

        String timestamp = String.valueOf(Instant.now().toEpochMilli()).substring(8);
        String newFileKey = "covers/" + StringUtils.createSlug(album.getTitle()) +
                "-" + timestamp + ".webp";

        fileStoragePort.upload(processedImage, newFileKey, "image/webp");
        registerStorageCleanup(previousFileKey, newFileKey);
        album.changeCoverUrl(newFileKey);

        log.info("Successfully uploaded album cover for album id={}", albumId);
        return new AlbumDto.Cover(album.getId(), "Cover uploaded successfully", fileStoragePort.urlFor(newFileKey));
    }

    private void registerStorageCleanup(final String previousFileKey, final String newFileKey) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteFileIfPresent(previousFileKey, "old album cover");
            }

            @Override
            public void afterCompletion(final int status) {
                if (status == STATUS_ROLLED_BACK) {
                    deleteFileIfPresent(newFileKey, "uncommitted album cover");
                }
            }
        });
    }

    private void deleteFileIfPresent(final String fileKey, final String description) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }
        try {
            fileStoragePort.delete(fileKey);
        } catch (RuntimeException exception) {
            log.error("Failed to delete {} with key={}", description, fileKey, exception);
        }
    }
}
