package com.spring.beatmarket.domain.licensing.dto;

import java.math.BigDecimal;

public record SongPriceDto(Long songId,
                           String tier,
                           BigDecimal price,
                           String currency) {
}
