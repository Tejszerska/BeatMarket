package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
@RequiredArgsConstructor
class ImageUploader {
    private final ImageProcessorPort imageProcessor;
    private final FileStoragePort fileStoragePort;

    String upload(final byte[] imageBytes, final String newFileKey, final String previousFileKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Image uploads require an active transaction.");
        }

        byte[] processedImage = imageProcessor.process(imageBytes);
        fileStoragePort.upload(processedImage, newFileKey, "image/webp");
        registerStorageCleanup(previousFileKey, newFileKey);

        log.info("Successfully uploaded image with key={}", newFileKey);
        return fileStoragePort.urlFor(newFileKey);
    }

    private void registerStorageCleanup(final String previousFileKey, final String newFileKey) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteFileIfPresent(previousFileKey, "old image");
            }

            @Override
            public void afterCompletion(final int status) {
                if (status == STATUS_ROLLED_BACK) {
                    deleteFileIfPresent(newFileKey, "uncommitted image");
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
