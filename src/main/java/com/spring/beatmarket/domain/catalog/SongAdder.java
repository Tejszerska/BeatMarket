package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongCreatedDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
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


    SongCreatedDto addSong(final SongRequestDto dto) {

        Genre genreProxy = null;
        if (dto.genreId() != null) {
            genreProxy = genreRetriever.getGenreReference(dto.genreId());
        }

        Album albumProxy = null;
        if (dto.albumId() != null) {
            albumProxy = albumRetriever.getAlbumReferenceById(dto.albumId());
        }

        List<Artist> artistsProxyList = new ArrayList<>();
        List<Long> artistIdsFromDto = dto.artistIds();
        if (dto.artistIds() != null) {
            for (Long artistsId : artistIdsFromDto) {
                artistsProxyList.add(artistRetriever.getArtistReference(artistsId));
            }
        }

        Song save = songRepository.save(
                new Song(dto.title(), dto.releaseDate(), dto.duration(), dto.language(), genreProxy, albumProxy, artistsProxyList));

        return songMapper.mapFromEntityToSongCreatedDto(save);
    }

    SongDto addDefaultSong() {
        Song song = new Song();
        song.assignDefaultTitle();
        Genre defaultGenre = genreRetriever.retrieveDefaultGenre();
        song.setGenre(defaultGenre);
        Song savedSong = songRepository.save(song);
        return songMapper.mapFromEntityToSongDto(savedSong);
    }
}
