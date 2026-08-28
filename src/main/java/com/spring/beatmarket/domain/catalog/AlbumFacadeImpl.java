package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
class AlbumFacadeImpl implements AlbumFacade {

    public Slice<AlbumDto.Summary> findAllAlbums(final String title, final Pageable pageable) {
        return null;
    }
    public AlbumDto.Details getAlbumDetails(final Long albumId) {
        return null;
    }

    public AlbumDto.Info addAlbum(final AlbumDto.Create createDto) {
        return null;
    }

    public AlbumDto.Info updateAlbum(final Long albumId, final AlbumDto.Update dto) {
        return null;
    }

    public void deactivateAlbum(final Long albumId) {

    }
}
