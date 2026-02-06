package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * YouTube 재생목록 Entity
 */
@Entity
@Table(name = "youtube_playlist", indexes = {
    @Index(name = "idx_playlist_id", columnList = "playlistId", unique = true),
    @Index(name = "idx_playlist_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class YouTubePlaylist extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String playlistId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer videoCount = 0;

    @Column(length = 50)
    private String category;

    private LocalDateTime lastSyncedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoSync = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
