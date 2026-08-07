package com.spring.beatmarket.domain.licensing;

import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Service
@Transactional
public class LicensingFacade {
    private final SongPricesRetriever songPricesRetriever;

    public Map<Long, List<SongPriceDto>> getMultiplePricingDto(List<Long> songIds) {
        return songPricesRetriever.getMultiplePricingDto(songIds);
    }

    public Set<Long> findSongIdByMaxPrice(String currency, String tier, BigDecimal maxPrice) {
        return songPricesRetriever.findSongIdByMaxPrice(currency, tier, maxPrice);
    }
}
