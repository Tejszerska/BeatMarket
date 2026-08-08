package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record AlbumSummaryDto(Long id,
                              String title,
                              String coverUrl) {
}
