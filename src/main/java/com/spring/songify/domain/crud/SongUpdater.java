package com.spring.songify.domain.crud;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
class SongUpdater {
    private final SongRepository songRepository;

    void updateById(Long id, Song newSong) {
        songRepository.updateById(id, newSong);
    }

}
