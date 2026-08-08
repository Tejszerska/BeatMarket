package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record ArtistSummaryDto(Long id,
                               String name,
                               String imageUrl,
                               Integer order) {
}
