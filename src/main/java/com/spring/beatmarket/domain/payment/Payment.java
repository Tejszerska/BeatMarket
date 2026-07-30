package com.spring.beatmarket.domain.payment;

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

import java.math.BigDecimal;

@Builder
@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
class Payment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "payment_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "payment_id_seq",
            sequenceName = "payment_id_seq",
            allocationSize = 1
    )
    private Long id;

    private Long userId;
    private Long licenseId;

    private BigDecimal amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String stripeSessionId;
}
