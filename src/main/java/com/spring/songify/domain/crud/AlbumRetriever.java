package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumWithArtistsAndSongsDto;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumRetriever {
    private final AlbumRepository albumRepository;


    AlbumWithArtistsAndSongsDto findAlbumByIdWithArtistsAndSongs(final Long id) {
        Album album= albumRepository.findAlbumByIdWithSongsAndArtists(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with id=" + id + " not found"));

        Set<Artist> artists = album.getArtists();
        Set<Song> songs = album.getSongs();

        Set<ArtistDto> artistsDto = artists.stream().map(a -> new ArtistDto(a.getId(), a.getName())).collect(Collectors.toSet());
        Set<SongDto> songsDto = songs.stream().map(s -> new SongDto(s.getId(), s.getName())).collect(Collectors.toSet());
        AlbumDto albumDto = new AlbumDto(album.getId(), album.getTitle());

        return new AlbumWithArtistsAndSongsDto(albumDto, artistsDto, songsDto);
    }


    AlbumInfo findAlbumByReturnAlbumInfo(final Long id) {
        return albumRepository.findAlbumByIdReturnAlbumInfo(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with id=" + id + " not found"));
    }
}
