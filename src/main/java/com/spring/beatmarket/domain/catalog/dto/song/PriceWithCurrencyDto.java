package com.spring.beatmarket.domain.catalog.dto.song;

import java.math.BigDecimal;

public record PriceWithCurrencyDto(BigDecimal price, String currency) {
}
