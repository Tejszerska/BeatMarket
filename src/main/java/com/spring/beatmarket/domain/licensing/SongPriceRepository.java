package com.spring.beatmarket.domain.licensing;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Repository
interface SongPriceRepository extends Repository<SongPrice, SongPriceId> {
    List<SongPrice> findBySongIdIn(List<Long> songIds);

    @Query("SELECT sp.songId FROM SongPrice sp " +
            "WHERE sp.currency= :currency " +
            "AND sp.tier= :tier " +
            "AND sp.price <= :maxPrice")
    Set<Long>  findSongIdByCurrencyAndTierAndPriceLessThanEqual
            (@Param("currency") String currency,
             @Param("tier") LicenseTier tier,
             @Param("maxPrice") BigDecimal maxPrice);


    List<SongPrice> findAllBySongId(Long songId);
}
