package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.exception.SongNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class SongRetriever {
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    Slice<SongDto> findAll(Pageable pageable) {
        log.info("retrieving all songs: ");
        return songRepository.findAllSongsWithGenre(pageable)
                .map(songMapper::mapFromEntityToSongDto);

    }

    SongDto findSongDtoById(Long id) {
        Song s = findSongById(id);
        return songMapper.mapFromEntityToSongDto(s);
    }

    Song findSongById(Long id) {
        return songRepository.findSongByIdWithGenre(id)
                .orElseThrow(() -> new SongNotFoundException("Song with id " + id + " not found"));
    }

    void existsById(Long id) {
        if (!songRepository.existsById(id)) {
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
    }
}
