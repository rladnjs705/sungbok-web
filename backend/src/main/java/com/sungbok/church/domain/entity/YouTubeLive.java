package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.LiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * YouTube 라이브 Entity
 */
@Entity
@Table(
    name = "youtube_live",
    indexes = {
        @Index(name = "idx_youtube_video_id", columnList = "youtubeVideoId", unique = true),
        @Index(name = "idx_live_status", columnList = "status, actualStartTime")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"worship"})
public class YouTubeLive extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_id")
    private Worship worship;

    @Column(nullable = false, length = 50)
    private String youtubeVideoId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String thumbnailUrl;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LiveStatus status = LiveStatus.SCHEDULED;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewerCount = 0;
}
