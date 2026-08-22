package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistAdder {
    private final ArtistRepository artistRepository;
    private final AlbumAdder albumAdder;
    private final AlbumRetriever albumRetriever;
    private final ArtistMapper artistMapper;


    ArtistDto.Info addArtist(final String name) {
        if (name == null || name.isBlank()) throw new NameIsBlankException("Artist needs a specified name!");
        Artist artist = new Artist(name);
        return artistMapper.toInfoDto(
                artistRepository.save(artist));

    }

    ArtistDto.Info addArtistWithDefaultAlbumAndSong(final ArtistRequestDto dto) {
        String artistName = dto.name();
        if (artistName == null || artistName.isBlank())
            throw new NameIsBlankException("Artist needs a specified name!");
        return artistMapper.toInfoDto(saveArtistWithDefaultAlbumAndSong(artistName));
    }

    private Artist saveArtistWithDefaultAlbumAndSong(final String name) {
        Artist artist = new Artist(name);
        Artist savedArtist = artistRepository.save(artist);

        //LegacySongDto songDtoOld = new LegacySongDto();
        LegacyAlbumDto legacyAlbumDto = albumAdder.addDefaultAlbum(1L);
        Album album = albumRetriever.findById(legacyAlbumDto.id());

        artist.setAlbums(List.of(album));
        album.addArtist(artist);
        return savedArtist;
    }
}
