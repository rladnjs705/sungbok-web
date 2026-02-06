package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.PrayerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기도요청 Entity
 */
@Entity
@Table(name = "prayer_request", indexes = {
    @Index(name = "idx_prayer_status", columnList = "status, createdAt DESC"),
    @Index(name = "idx_prayer_approved", columnList = "isApproved, createdAt DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class PrayerRequest extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String requester;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnonymous = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isApproved = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrayerStatus status = PrayerStatus.PENDING;

    private LocalDateTime answeredAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer prayerCount = 0;
}
