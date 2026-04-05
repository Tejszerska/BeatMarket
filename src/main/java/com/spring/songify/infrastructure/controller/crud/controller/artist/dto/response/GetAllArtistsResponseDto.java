package com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response;

import com.spring.songify.domain.crud.dto.ArtistDto;

import java.util.List;

public record GetAllArtistsResponseDto(List<ArtistDto> artists, boolean hasNext) {
}
