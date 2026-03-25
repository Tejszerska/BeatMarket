package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
class SongAdder {
    private final SongRepository songRepository;

    SongDto addSong(final SongRequestDto dto) {
        String languageFromDto = dto.languageDto().name();
        SongLanguage songLanguage = SongLanguage.valueOf(languageFromDto);
        Song song = new Song(dto.name(), dto.releaseDate(), dto.duration(), songLanguage);
        Song save = songRepository.save(song);
        return new SongDto(save.getId(), save.getName());
    }
}
