package com.spring.songify.infrastructure.controller.crud.controller.album.dto.response;

import lombok.Builder;

import java.util.List;
@Builder
public record GetAlbumDetailsResponse(Long id, String title,
                                      List<ArtistSummary> artists,
                                      List<SongSummary> songs) {
    public record ArtistSummary(Long id, String name){}
    public record SongSummary(Long id, String title){}

}
