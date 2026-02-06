package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 찬양 Entity
 */
@Entity
@Table(name = "hymn", indexes = {
    @Index(name = "idx_hymn_date", columnList = "performanceDate DESC"),
    @Index(name = "idx_hymn_published", columnList = "isPublished, performanceDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Hymn extends BaseEntity {

    @Column(unique = true)
    private Integer hymnNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String composer;

    @Column(length = 100)
    private String lyricist;

    @Column(columnDefinition = "TEXT")
    private String lyrics;

    @Column(length = 100)
    private String artist;

    @Column(length = 50)
    private String youtubeVideoId;

    @Column(length = 500)
    private String videoUrl;

    @Column(length = 500)
    private String youtubeUrl;

    @Column(length = 500)
    private String sheetMusicUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    private LocalDate performanceDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer performanceCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;
}
