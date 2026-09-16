package com.spring.beatmarket.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class AlbumDeleterTest {

    private final AlbumRepository albumRepository = mock(AlbumRepository.class);
    private final AlbumRetriever albumRetriever = mock(AlbumRetriever.class);

    private final AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository, albumRetriever);

    @Test
    @DisplayName("Should do nothing when album ids set is null")
    void should_do_nothing_when_ids_null() {
        // when
        albumDeleter.bulkDeactivate(null);

        // then
        verifyNoInteractions(albumRepository);
    }

    @Test
    @DisplayName("Should do nothing when album ids set is empty")
    void should_do_nothing_when_ids_empty() {
        // when
        albumDeleter.bulkDeactivate(Collections.emptySet());

        // then
        verifyNoInteractions(albumRepository);
    }

    @Test
    @DisplayName("Should delegate bulk soft deletion to repository")
    void should_delegate_bulk_deletion_to_repository() {
        // given
        Set<Long> idsToDelete = Set.of(1L, 2L, 3L);

        // when
        albumDeleter.bulkDeactivate(idsToDelete);

        // then
        verify(albumRepository).deactivateAllByIds(eq(idsToDelete), any(Instant.class));
    }
}