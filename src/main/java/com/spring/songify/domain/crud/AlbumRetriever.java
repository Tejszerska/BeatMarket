package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumWithArtistsAndSongsDto;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumRetriever {
    private final AlbumRepository albumRepository;


    AlbumWithArtistsAndSongsDto findAlbumByIdWithArtistsAndSongs(final Long id) {
        Album album = albumRepository.findAlbumByIdWithSongsAndArtists(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with id=" + id + " not found"));

        Set<Artist> artists = album.getArtists();
        Set<Song> songs = album.getSongs();

        Set<ArtistDto> artistsDto = artists.stream().map(a -> new ArtistDto(a.getId(), a.getName())).collect(Collectors.toSet());
        Set<SongDto> songsDto = songs.stream().map(s -> new SongDto(s.getId(), s.getTitle(), new GenreDto(s.getId(), s.getTitle()))).collect(Collectors.toSet());
        AlbumDto albumDto = new AlbumDto(album.getId(), album.getTitle());

        return new AlbumWithArtistsAndSongsDto(albumDto, artistsDto, songsDto);
    }


    AlbumInfo findAlbumByReturnAlbumInfo(final Long id) {
        return albumRepository.findAlbumByIdReturnAlbumInfo(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with id=" + id + " not found"));
    }

    Set<Album> findAlbumsByArtistId(final Long artistId) {
        return new HashSet<>(albumRepository.findAllAlbumsByArtistId(artistId));
    }

    Set<AlbumDto> findAlbumsDtoByArtistId(final Long artistId) {
        return findAlbumsByArtistId(artistId)
                .stream().map(
                        album -> new AlbumDto(album.getId(), album.getTitle()))
                .collect(Collectors.toSet());
    }

    Album findById(final Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException("Album by id=" + albumId + "was not found"));
    }

    Slice<AlbumDto> findAllAlbums(Pageable pageable) {
        return albumRepository.findAllAlbums(pageable)
                .map(album -> AlbumDto.builder()
                        .id(album.getId())
                        .title(album.getTitle())
                        .build());
    }
}
