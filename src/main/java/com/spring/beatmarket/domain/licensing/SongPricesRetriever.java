package com.spring.beatmarket.domain.licensing;

import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class SongPricesRetriever {
    private final SongPriceRepository songPriceRepository;
    private final SongPriceMapper mapper;

    Map<Long, List<SongPriceDto>> getMultiplePricingDto(List<Long> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SongPrice> rawPrices = songPriceRepository.findBySongIdIn(songIds);

        return rawPrices.stream()
                .map(mapper::mapFromEntityToSongPriceDto)
                .collect(Collectors.groupingBy(SongPriceDto::songId));
    }


    List<SongPriceDto> getPricingForSingleSong(Long songId){
        if (songId == null) return Collections.emptyList();

        List<SongPrice> allBySongId = songPriceRepository.findAllBySongId(songId);

        return allBySongId.stream()
                .map(mapper::mapFromEntityToSongPriceDto)
                .collect(Collectors.toList());
    }

    Set<Long> findSongIdByMaxPrice (String currency, String tier, BigDecimal maxPrice){
        LicenseTier enumTier= LicenseTier.valueOf(tier);
        return songPriceRepository.findSongIdByCurrencyAndTierAndPriceLessThanEqual(currency, enumTier, maxPrice);
    }

}