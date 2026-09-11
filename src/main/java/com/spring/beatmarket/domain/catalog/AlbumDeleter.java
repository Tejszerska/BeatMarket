package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class AlbumDeleter {
    private final AlbumRepository albumRepository;

    void deleteAllAlbumsByIds(final Set<Long> albumIds) {
        if (albumIds == null || albumIds.isEmpty()) return;
        log.info("soft deleting albums by ids: " + albumIds);
        albumRepository.deactivateAllByIds(albumIds, Instant.now());
    }

    void deactivate(final Long id) {
        if (id == null) return;
        log.info("soft deleting album by id: " + id);
        albumRepository.deactivateId(id, Instant.now());

    }
}
