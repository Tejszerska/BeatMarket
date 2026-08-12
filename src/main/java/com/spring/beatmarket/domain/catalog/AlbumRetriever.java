package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }

    Set<Album> findAlbumsByArtistId(final Long artistId) {
        if (!artistRetriever.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist", artistId);
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
                .orElseThrow(() -> new  ResourceNotFoundException("Album", albumId));
    }

    Slice<AlbumDto> findAllAlbums(Pageable pageable) {
        return albumRepository.findAllAlbums(pageable)
                .map(albumMapper::mapFromEntityToAlbumDto);
    }

    Album getAlbumReferenceById(final Long id) {
       return albumRepository.getReferenceById(id);
    }
}
