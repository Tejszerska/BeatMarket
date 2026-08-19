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
    private final SongRetriever songRetriever;


    SongDto.Info addSong(final SongDto.Create dto) {

        Genre genreProxy = dto.genreId() != null
                ? genreRetriever.getGenreReference(dto.genreId()) : null;

        Album albumProxy = dto.albumId() != null
                ? albumRetriever.getAlbumReferenceById(dto.albumId()) : null;

        List<Artist> artistsProxyList = new ArrayList<>();
        if (dto.artistIds() != null) {
            for (Long artistsId : dto.artistIds()) {
                artistsProxyList.add(artistRetriever.getArtistReference(artistsId));
            }
        }

        Song saved = songRepository.save(
                new Song(dto.title(), dto.releaseDate(), dto.duration(), dto.language(), genreProxy, albumProxy, artistsProxyList));

        Song songForResponse = songRetriever.findSongByIdEagerly(saved.getId());
        return songMapper.toInfoDto(songForResponse);
    }

}
