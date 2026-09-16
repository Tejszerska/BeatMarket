package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.licensing.LicensingFacade;
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

class SongDeleterTest {
    private final SongRepository songRepository = mock(SongRepository.class);
    private final SongRetriever songRetriever = mock(SongRetriever.class);
    private final LicensingFacade licensingFacade = mock(LicensingFacade.class);
    private final SongDeleter songDeleter = new SongDeleter(songRepository, songRetriever,licensingFacade);

    @Test
    @DisplayName("Should do nothing when album ids set is null")
    void should_do_nothing_when_ids_null() {
        // when
        songDeleter.bulkDeactivate(null);

        // then
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should do nothing when album ids set is empty")
    void should_do_nothing_when_ids_empty() {
        // when
        songDeleter.bulkDeactivate(Collections.emptySet());

        // then
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should delegate bulk soft deletion to repository")
    void should_delegate_bulk_deletion_to_repository() {
        // given
        Set<Long> idsToDelete = Set.of(1L, 2L, 3L);

        // when
        songDeleter.bulkDeactivate(idsToDelete);

        // then
        verify(songRepository).deactivateAllByIds(eq(idsToDelete), any(Instant.class));
        verify(licensingFacade).deactivatePricesForSongs(eq(idsToDelete));
    }
}
