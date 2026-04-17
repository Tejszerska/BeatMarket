package com.spring.songify.domain.crud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class AlbumDeleter {
    private final AlbumRepository albumRepository;

    void deleteAllAlbumsByIds(final Set<Long> albumIds) {
        albumRepository.deleteByIdIn(albumIds);
    }
}
