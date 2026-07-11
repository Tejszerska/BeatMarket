package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.AlbumDto;
import com.spring.beatmarket.domain.crud.dto.AlbumInfo;
import com.spring.beatmarket.domain.crud.exception.AlbumNotFoundException;
import com.spring.beatmarket.domain.crud.exception.ArtistNotFoundException;
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
    private final ArtistRetriever artistRetriever;
    private final AlbumMapper albumMapper;

    AlbumInfo findAlbumByReturnAlbumInfo(final Long id) {
        return albumRepository.findAlbumByIdReturnAlbumInfo(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with id=" + id + " not found"));
    }

    Set<Album> findAlbumsByArtistId(final Long artistId) {
        if (!artistRetriever.existsById(artistId)) {
            throw new ArtistNotFoundException("Artist by id=" + artistId + " wasn't found in the database");
        }
        return new HashSet<>(albumRepository.findAllAlbumsByArtistId(artistId));
    }

    Set<AlbumDto> findAlbumsDtoByArtistId(final Long artistId) {
        return findAlbumsByArtistId(artistId)
                .stream().map(albumMapper::mapFromEntityToAlbumDto)
                .collect(Collectors.toSet());
    }

    Album findById(final Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException("Album by id=" + albumId + "was not found"));
    }

    Slice<AlbumDto> findAllAlbums(Pageable pageable) {
        return albumRepository.findAllAlbums(pageable)
                .map(albumMapper::mapFromEntityToAlbumDto);
    }
}
