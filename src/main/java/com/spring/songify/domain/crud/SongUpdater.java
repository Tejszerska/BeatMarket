package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
class SongUpdater {
    private final SongRetriever songRetriever;
    private final SongMapper songMapper;

    public SongDto updatePartiallyByIdAndDto(Long id, SongDto requestDto) {
        Song songFromDatabase = songRetriever.findSongById(id);
        songMapper.updateSongFromDto(requestDto, songFromDatabase);
        return songMapper.mapFromEntityToSongDto(songFromDatabase);
    }
}
