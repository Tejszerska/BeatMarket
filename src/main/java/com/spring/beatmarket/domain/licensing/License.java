package com.spring.beatmarket.domain.licensing;

import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Builder
@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
class License extends BaseEntity {

    @Id
    @GeneratedValue(generator = "license_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "license_id_seq",
            sequenceName = "license_id_seq",
            allocationSize = 1
    )
    private Long id;

    private String certificateKey;

    private Long userId;
    private Long paymentId;
    private Long songId;

    @Enumerated(EnumType.STRING)
    private LicenseTier tier;

    private Instant validFrom;
    private Instant validTo;
}