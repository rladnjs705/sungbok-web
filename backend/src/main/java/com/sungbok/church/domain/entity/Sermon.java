package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 설교 Entity
 */
@Entity
@Table(name = "sermon", indexes = {
    @Index(name = "idx_sermon_date", columnList = "sermonDate DESC"),
    @Index(name = "idx_sermon_worship", columnList = "worship_id"),
    @Index(name = "idx_sermon_published", columnList = "isPublished, sermonDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"worship"})
public class Sermon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_id")
    private Worship worship;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String bibleVerse;

    @Column(nullable = false, length = 50)
    private String preacher;

    @Column(nullable = false)
    private LocalDate sermonDate;

    @Column(length = 50)
    private String youtubeVideoId;

    @Column(length = 500)
    private String videoUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    private Integer duration; // 초 단위

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 500)
    private String tags;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
}
