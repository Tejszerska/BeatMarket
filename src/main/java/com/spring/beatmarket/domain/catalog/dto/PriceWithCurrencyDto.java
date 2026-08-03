package com.spring.beatmarket.domain.catalog.dto;

import java.math.BigDecimal;

public record PriceWithCurrencyDto(BigDecimal price, String currency) {
}
