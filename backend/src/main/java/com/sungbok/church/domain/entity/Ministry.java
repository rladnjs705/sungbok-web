package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.MinistryCategory;
import jakarta.persistence.*;
import lombok.*;

/**
 * 교육/양육 부서 Entity
 */
@Entity
@Table(name = "ministry", indexes = {
    @Index(name = "idx_ministry_category", columnList = "category"),
    @Index(name = "idx_ministry_active", columnList = "isActive, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Ministry extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MinistryCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String targetAge;

    @Column(length = 200)
    private String schedule;

    @Column(length = 100)
    private String location;

    @Column(length = 50)
    private String leader;

    @Column(length = 100)
    private String contact;

    @Column(length = 500)
    private String photoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Integer displayOrder;
}
