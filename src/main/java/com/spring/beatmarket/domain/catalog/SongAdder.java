package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class SongAdder {
    private final SongRepository songRepository;
    private final GenreRetriever genreRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistRetriever artistRetriever;
    private final SongMapper songMapper;


    SongDto.Info addSong(final SongDto.Create dto) {

        Genre genre = dto.genreId() != null
                ? genreRetriever.getActive(dto.genreId()) : null;

        Album album = dto.albumId() != null
                ? albumRetriever.getActiveWithArtist(dto.albumId()) : null;

        List<Artist> artists = new ArrayList<>();
        if (dto.artistIds() != null && !dto.artistIds().isEmpty()) {
            artists = artistRetriever.getActiveArtists(dto.artistIds());
        }

        Song saved = songRepository.save(
                new Song(dto.title(), dto.releaseDate(), dto.duration(), dto.language(), genre, album, artists)
        );

        return songMapper.toInfoDto(saved);
    }

}
