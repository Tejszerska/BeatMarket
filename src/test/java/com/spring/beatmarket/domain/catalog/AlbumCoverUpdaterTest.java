package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.exception.InvalidAlbumCoverException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlbumCoverUpdaterTest {
    private final AlbumRetriever albumRetriever = mock(AlbumRetriever.class);
    private final AlbumCoverImageProcessorPort imageProcessor = mock(AlbumCoverImageProcessorPort.class);
    private final FileStoragePort fileStoragePort = mock(FileStoragePort.class);
    private final AlbumCoverUpdater albumCoverUpdater = new AlbumCoverUpdater(
            albumRetriever,
            imageProcessor,
            fileStoragePort
    );

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("Should upload the processed cover and remove the previous cover after commit")
    void should_upload_processed_cover_and_remove_previous_after_commit() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        byte[] originalImage = {1, 2, 3};
        byte[] processedImage = {4, 5, 6};
        Album album = TestObjectsFactory.createAlbumWithId(42L, "Album");
        album.changeCoverUrl("covers/old-cover.webp");
        AlbumDto.Cover expectedCover = new AlbumDto.Cover(42L, "Cover uploaded successfully", "cover-url");
        when(albumRetriever.getActive(42L)).thenReturn(album);
        when(imageProcessor.process(originalImage)).thenReturn(processedImage);
        when(fileStoragePort.urlFor(anyString())).thenReturn("cover-url");

        // when
        AlbumDto.Cover result = albumCoverUpdater.update(42L, originalImage);

        // then
        assertThat(result).isEqualTo(expectedCover);
        assertThat(album.getCoverFileKey()).matches("covers/album-\\d{5}\\.webp");
        verify(fileStoragePort).upload(processedImage, album.getCoverFileKey(), "image/webp");
        verify(fileStoragePort).urlFor(album.getCoverFileKey());
        verify(albumRetriever).getActive(42L);
        verify(fileStoragePort, never()).delete("covers/old-cover.webp");

        TransactionSynchronization synchronization = TransactionSynchronizationManager
                .getSynchronizations()
                .get(0);
        synchronization.afterCommit();
        verify(fileStoragePort).delete("covers/old-cover.webp");
    }

    @Test
    @DisplayName("Should remove the new cover and preserve the previous cover after rollback")
    void should_remove_new_cover_after_rollback() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        byte[] originalImage = {1};
        byte[] processedImage = {2};
        Album album = TestObjectsFactory.createAlbumWithId(42L, "Album");
        album.changeCoverUrl("covers/old-cover.webp");
        when(albumRetriever.getActive(42L)).thenReturn(album);
        when(imageProcessor.process(originalImage)).thenReturn(processedImage);
        when(fileStoragePort.urlFor(anyString())).thenReturn("cover-url");

        // when
        albumCoverUpdater.update(42L, originalImage);

        // then
        String newFileKey = album.getCoverFileKey();
        TransactionSynchronizationManager.getSynchronizations()
                .get(0)
                .afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(fileStoragePort).delete(newFileKey);
        verify(fileStoragePort, never()).delete("covers/old-cover.webp");
    }

    @Test
    @DisplayName("Should preserve the current cover when image processing fails")
    void should_preserve_current_cover_when_processing_fails() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        byte[] invalidImage = {1};
        Album album = TestObjectsFactory.createAlbumWithId(42L, "Album");
        album.changeCoverUrl("covers/old-cover.webp");
        when(albumRetriever.getActive(42L)).thenReturn(album);
        when(imageProcessor.process(invalidImage)).thenThrow(
                new InvalidAlbumCoverException("Invalid image.")
        );

        // when & then
        assertThatThrownBy(() -> albumCoverUpdater.update(42L, invalidImage))
                .isInstanceOf(InvalidAlbumCoverException.class);
        assertThat(album.getCoverFileKey()).isEqualTo("covers/old-cover.webp");
        verify(fileStoragePort, never()).upload(any(byte[].class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should preserve the current cover when S3 upload fails")
    void should_preserve_current_cover_when_s3_upload_fails() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        byte[] originalImage = {1};
        byte[] processedImage = {2};
        Album album = TestObjectsFactory.createAlbumWithId(42L, "Album");
        album.changeCoverUrl("covers/old-cover.webp");
        when(albumRetriever.getActive(42L)).thenReturn(album);
        when(imageProcessor.process(originalImage)).thenReturn(processedImage);
        doThrow(new IllegalStateException("S3 is unavailable"))
                .when(fileStoragePort)
                .upload(eq(processedImage), anyString(), eq("image/webp"));

        // when & then
        assertThatThrownBy(() -> albumCoverUpdater.update(42L, originalImage))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("S3 is unavailable");
        assertThat(album.getCoverFileKey()).isEqualTo("covers/old-cover.webp");
        assertThat(TransactionSynchronizationManager.getSynchronizations()).isEmpty();
    }
}
