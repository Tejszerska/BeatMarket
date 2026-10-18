package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
@Slf4j
@RequiredArgsConstructor
class SongFilesUpdater {
    private final SongRetriever songRetriever;
    private final AudioFileValidator fileValidator;
    private final FileStoragePort fileStoragePort;
    private final LicensingFacade licensingFacade;
    private final FileKeyGenerator fileKeyGenerator;

    SongDto.Track updateTrackFile(final byte[] trackBytes, final Long songId) {
        Song song = songRetriever.getLazily(songId);
        AudioFileExtension extension = fileValidator.validateFullTrack(trackBytes, song.getDuration());
        String previousFileKey = song.getTrackFileKey();
        boolean previousFileHasActiveLicenses = hasActiveLicenses(previousFileKey);
        String newFileKey = fileKeyGenerator.generate("tracks", song.getTitle(), extension.toString());

        ensureTransactionSynchronizationActive();
        fileStoragePort.upload(trackBytes, newFileKey, extension.contentType());
        registerStorageCleanup(
                newFileKey,
                previousFileKey,
                !previousFileHasActiveLicenses
        );
        song.changeTrackFileKey(newFileKey);
        log.info("Successfully uploaded full track file for song id={}", song.getId());

        return new SongDto.Track(song.getId(), "Track uploaded successfully");
    }

    SongDto.Preview updatePreviewFile(final byte[] previewBytes, final Long songId) {
        Song song = songRetriever.getLazily(songId);
        AudioFileExtension extension = fileValidator.validatePreviewTrack(previewBytes, song.getDuration());
        String previousFileKey = song.getPreviewFileKey();
        String newFileKey = fileKeyGenerator.generate("previews", song.getTitle(), extension.toString());

        ensureTransactionSynchronizationActive();
        fileStoragePort.upload(previewBytes, newFileKey, extension.contentType());
        registerStorageCleanup(newFileKey, previousFileKey, true);
        song.changePreviewFileKey(newFileKey);
        log.info("Successfully uploaded preview track file for song id={}", song.getId());

        return new SongDto.Preview(song.getId(), fileStoragePort.urlFor(newFileKey));
    }

    private boolean hasActiveLicenses(final String fileKey) {
        return fileKey != null && !fileKey.isBlank() && licensingFacade.hasAnyCurrentLicenses(fileKey);
    }

    private void registerStorageCleanup(
            final String newFileKey,
            final String previousFileKey,
            final boolean deletePreviousFileAfterCommit) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (deletePreviousFileAfterCommit) {
                    deleteFileIfPresent(previousFileKey, "replaced song file");
                }
            }

            @Override
            public void afterCompletion(final int status) {
                if (status == STATUS_ROLLED_BACK) {
                    deleteFileIfPresent(newFileKey, "uncommitted song file");
                }
            }
        });
    }

    private void ensureTransactionSynchronizationActive() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Song file updates require an active transaction.");
        }
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
