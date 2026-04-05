package com.spring.songify.infrastructure.controller.crud.controller.artist;

import com.spring.songify.domain.crud.dto.ArtistDto;

import java.util.Set;

public record AllArtistDto(Set<ArtistDto> artists) {
}
