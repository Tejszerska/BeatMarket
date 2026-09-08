package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class SongAdder {
    private final SongRepository songRepository;
    private final GenreRetriever genreRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistRoleAssigner artistRoleAssigner;
    private final SongMapper songMapper;

    SongDto.Info add(final SongDto.Create dto) {
        Genre genre = dto.genreId() != null
                ? genreRetriever.getActive(dto.genreId()) : null;

        Album album = dto.albumId() != null
                ? albumRetriever.getActiveWithArtist(dto.albumId()) : null;

        Song song = Song.builder()
                .title(dto.title())
                .releaseDate(dto.releaseDate())
                .duration(dto.duration())
                .language(dto.language())
                .genre(genre)
                .album(album)
                .build();

        artistRoleAssigner.assign(dto.mainArtistId(), dto.featArtistIds(), "Song", song::assignArtist);


        Song saved = songRepository.save(song);
        return songMapper.toInfoDto(saved);
    }
}