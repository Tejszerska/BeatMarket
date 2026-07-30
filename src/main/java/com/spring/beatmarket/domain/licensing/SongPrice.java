package com.spring.beatmarket.domain.licensing;

import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@IdClass(SongPriceId.class)
class SongPrice extends BaseEntity {
    @Id
    private Long songId;

    @Id
    @Enumerated(EnumType.STRING)
    private LicenseTier tier;

    private BigDecimal price;
    private String currency;
}
