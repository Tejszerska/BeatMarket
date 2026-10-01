package com.spring.beatmarket.domain.licensing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
class LicenseRetriever {
    private final LicenseRepository licenseRepository;

    boolean hasAnyCurrentLicenses(String trackFileKey) {
        return licenseRepository.existsByTrackFileKeyAndActiveTrueAndValidToAfter(trackFileKey, Instant.now());
    }
}
