package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
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

    Slice<SongDto> findAll(Pageable pageable) {
        log.info("retrieving all songs: ");
        return songRepository.findAllSongsWithGenre(pageable).map(song -> SongDto.builder()
                        .id(song.getId())
                        .title(song.getTitle())
                        .genre(new GenreDto(song.getGenre().getId(), song.getGenre().getName()))
                        .build());
    }

    SongDto findSongDtoById(Long id) {
        Song s = findSongById(id);
        return SongDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .genre(new GenreDto(s.getGenre().getId(), s.getGenre().getName()))
                .build();
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
