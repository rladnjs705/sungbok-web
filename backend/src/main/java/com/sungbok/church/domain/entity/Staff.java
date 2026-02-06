package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.StaffRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * 섬기는 이들 Entity (장로, 권사, 집사)
 */
@Entity
@Table(name = "staff", indexes = {
    @Index(name = "idx_staff_role", columnList = "role"),
    @Index(name = "idx_staff_active", columnList = "isActive, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Staff extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StaffRole role;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String position;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String photo;

    @Column(length = 500)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Integer displayOrder;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
