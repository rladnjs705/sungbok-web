package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 헌금 계좌 Entity
 */
@Entity
@Table(name = "donation_account", indexes = {
    @Index(name = "idx_donation_active", columnList = "isActive, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class DonationAccount extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String accountHolder;

    @Column(length = 50)
    private String donationType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer displayOrder;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
