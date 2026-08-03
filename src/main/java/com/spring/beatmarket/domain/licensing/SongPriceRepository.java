package com.spring.beatmarket.domain.licensing;

import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
interface SongPriceRepository extends Repository<SongPrice, SongPriceId> {
    List<SongPrice> findBySongIdIn(List<Long> songIds);
}
