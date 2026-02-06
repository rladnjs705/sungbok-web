package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 주보 Entity
 */
@Entity
@Table(name = "bulletin", indexes = {
    @Index(name = "idx_bulletin_date", columnList = "bulletinDate DESC"),
    @Index(name = "idx_bulletin_published", columnList = "isPublished, bulletinDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Bulletin extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private LocalDate bulletinDate;

    @Column(nullable = false, length = 500)
    private String pdfUrl;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer downloadCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;
}
