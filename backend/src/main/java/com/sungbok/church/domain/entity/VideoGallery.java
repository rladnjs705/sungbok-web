package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 영상 갤러리 Entity
 */
@Entity
@Table(name = "video_gallery", indexes = {
    @Index(name = "idx_video_date", columnList = "eventDate DESC"),
    @Index(name = "idx_video_published", columnList = "isPublished, eventDate DESC"),
    @Index(name = "idx_video_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class VideoGallery extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String youtubeVideoId;

    @Column(length = 500)
    private String videoUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    private LocalDate eventDate;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;
}
