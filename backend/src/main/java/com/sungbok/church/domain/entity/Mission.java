package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.MissionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 선교 Entity
 */
@Entity
@Table(name = "mission", indexes = {
    @Index(name = "idx_mission_type", columnList = "type"),
    @Index(name = "idx_mission_active", columnList = "isActive, startDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Mission extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MissionType type;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String region;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String missionaryName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long supportAmount;

    @Column(length = 500)
    private String photoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
