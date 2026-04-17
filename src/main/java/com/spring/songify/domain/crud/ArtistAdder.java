package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistAdder {
    private final ArtistRepository artistRepository;
    private final AlbumAdder albumAdder;
    private final SongAdder songAdder;
    private final AlbumRetriever albumRetriever;
    private final ArtistMapper artistMapper;


    ArtistDto addArtist(final String name) {
        if (name == null || name.isBlank()) throw new NameIsBlankException("Artist needs a specified name!");
        Artist artist = new Artist(name);
        return artistMapper.mapFromEntityToArtistDto(
                artistRepository.save(artist));

    }

    ArtistDto addArtistWithDefaultAlbumAndSong(final ArtistRequestDto dto) {
        String artistName = dto.name();
        if (artistName == null || artistName.isBlank())
            throw new NameIsBlankException("Artist needs a specified name!");
        return artistMapper.mapFromEntityToArtistDto(saveArtistWithDefaultAlbumAndSong(artistName));
    }

    private Artist saveArtistWithDefaultAlbumAndSong(final String name) {
        Artist artist = new Artist(name);
        Artist savedArtist = artistRepository.save(artist);

        SongDto songDto = songAdder.addDefaultSong();
        AlbumDto albumDto = albumAdder.addDefaultAlbum(songDto.id());
        Album album = albumRetriever.findById(albumDto.id());

        artist.setAlbums(Set.of(album));
        album.addArtist(artist);
        return savedArtist;
    }
}
