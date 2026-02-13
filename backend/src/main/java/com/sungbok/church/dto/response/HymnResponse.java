package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Hymn;
import com.sungbok.church.dto.projection.HymnProjectionDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hymn 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HymnResponse {

    private Long id;
    private String title;
    private String artist;
    private String youtubeVideoId;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDate performanceDate;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static HymnResponse from(Hymn hymn) {
        return HymnResponse.builder()
            .id(hymn.getId())
            .title(hymn.getTitle())
            .artist(hymn.getArtist())
            .youtubeVideoId(hymn.getYoutubeVideoId())
            .videoUrl(hymn.getVideoUrl())
            .thumbnailUrl(hymn.getThumbnailUrl())
            .performanceDate(hymn.getPerformanceDate())
            .viewCount(hymn.getViewCount())
            .isPublished(hymn.getIsPublished())
            .createdAt(hymn.getCreatedAt())
            .updatedAt(hymn.getUpdatedAt())
            .build();
    }

    /**
     * ProjectionDto -> Response DTO 변환
     */
    public static HymnResponse from(HymnProjectionDto dto) {
        return HymnResponse.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .artist(dto.getArtist())
            .youtubeVideoId(dto.getYoutubeVideoId())
            .videoUrl(dto.getVideoUrl())
            .thumbnailUrl(dto.getThumbnailUrl())
            .performanceDate(dto.getPerformanceDate())
            .viewCount(dto.getViewCount())
            .isPublished(dto.getIsPublished())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
}
