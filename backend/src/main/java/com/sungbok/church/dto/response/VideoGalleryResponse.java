package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.VideoGallery;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * VideoGallery 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoGalleryResponse {

    private Long id;
    private String title;
    private String description;
    private String youtubeVideoId;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDate eventDate;
    private String category;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static VideoGalleryResponse from(VideoGallery video) {
        return VideoGalleryResponse.builder()
            .id(video.getId())
            .title(video.getTitle())
            .description(video.getDescription())
            .youtubeVideoId(video.getYoutubeVideoId())
            .videoUrl(video.getVideoUrl())
            .thumbnailUrl(video.getThumbnailUrl())
            .eventDate(video.getEventDate())
            .category(video.getCategory())
            .viewCount(video.getViewCount())
            .isPublished(video.getIsPublished())
            .createdAt(video.getCreatedAt())
            .updatedAt(video.getUpdatedAt())
            .build();
    }
}
