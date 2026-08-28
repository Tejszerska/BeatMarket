package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AlbumFacade {

    Slice<AlbumDto.Summary> findAllAlbums(String title, Pageable pageable);

    AlbumDto.Details getAlbumDetails(final Long albumId);

    AlbumDto.Info addAlbum(AlbumDto.Create createDto);

    AlbumDto.Info updateAlbum(Long albumId, AlbumDto.Update dto);

    void deactivateAlbum(final Long albumId);
}
