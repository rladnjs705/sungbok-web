package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 담임목사/교역자 Entity
 */
@Entity
@Table(name = "pastor", indexes = {
    @Index(name = "idx_pastor_active", columnList = "isActive, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Pastor extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String position;

    @Column(length = 500)
    private String photo;

    @Column(length = 500)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(length = 100)
    private String ministryArea;

    @Column(columnDefinition = "TEXT")
    private String career;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    private Integer displayOrder;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
