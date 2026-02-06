package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 사진첩/갤러리 Entity
 */
@Entity
@Table(name = "gallery", indexes = {
    @Index(name = "idx_gallery_date", columnList = "eventDate DESC"),
    @Index(name = "idx_gallery_published", columnList = "isPublished, eventDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Gallery extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(length = 500)
    private String coverImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;
}
