package com.spring.beatmarket.domain.licensing;

import org.springframework.data.repository.Repository;

import java.time.Instant;

@org.springframework.stereotype.Repository
interface LicenseRepository extends Repository<License, Long> {

    boolean existsByTrackFileKeyAndActiveTrueAndValidToAfter(String trackFileKey, Instant now);
}
