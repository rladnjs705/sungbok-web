package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.WorshipType;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 예배 Entity
 */
@Entity
@Table(
    name = "worship",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_worship_type_day",
            columnNames = {"type", "dayOfWeek"}
        )
    },
    indexes = {
        @Index(name = "idx_worship_active", columnList = "isActive, dayOfWeek")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {})
public class Worship extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorshipType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(length = 100)
    private String location;

    @Column(length = 500)
    private String liveStreamUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isLiveNow = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
