package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.dto.projection.SermonProjectionDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sermon 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SermonResponse {

    private Long id;
    private Long worshipId;
    private String worshipName;
    private String worshipType;
    private String title;
    private String preacher;
    private LocalDate sermonDate;
    private String bibleVerse;
    private String summary;
    private String youtubeVideoId;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer duration;
    private String tags;
    private String description;
    private Boolean isFeatured;
    private Boolean isPublished;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static SermonResponse from(Sermon sermon) {
        return SermonResponse.builder()
            .id(sermon.getId())
            .worshipId(sermon.getWorship() != null ? sermon.getWorship().getId() : null)
            .worshipName(sermon.getWorship() != null ? sermon.getWorship().getTitle() : null)
            .worshipType(sermon.getWorship() != null && sermon.getWorship().getType() != null ?
                        sermon.getWorship().getType().name() : null)
            .title(sermon.getTitle())
            .preacher(sermon.getPreacher())
            .sermonDate(sermon.getSermonDate())
            .bibleVerse(sermon.getBibleVerse())
            .summary(sermon.getSummary())
            .youtubeVideoId(sermon.getYoutubeVideoId())
            .videoUrl(sermon.getVideoUrl())
            .thumbnailUrl(sermon.getThumbnailUrl())
            .duration(sermon.getDuration())
            .tags(sermon.getTags())
            .description(sermon.getDescription())
            .isFeatured(sermon.getIsFeatured())
            .isPublished(sermon.getIsPublished())
            .viewCount(sermon.getViewCount())
            .createdAt(sermon.getCreatedAt())
            .updatedAt(sermon.getUpdatedAt())
            .build();
    }

    /**
     * Projection DTO -> Response DTO 변환
     */
    public static SermonResponse fromProjectionDto(SermonProjectionDto dto) {
        return SermonResponse.builder()
            .id(dto.getId())
            .worshipId(dto.getWorshipId())
            .worshipName(dto.getWorshipTitle())
            .worshipType(dto.getWorshipType())
            .title(dto.getTitle())
            .preacher(dto.getPreacher())
            .sermonDate(dto.getSermonDate())
            .bibleVerse(dto.getBibleVerse())
            .summary(dto.getSummary())
            .youtubeVideoId(dto.getYoutubeVideoId())
            .videoUrl(dto.getVideoUrl())
            .thumbnailUrl(dto.getThumbnailUrl())
            .duration(dto.getDuration())
            .tags(dto.getTags())
            .description(dto.getDescription())
            .isFeatured(dto.getIsFeatured())
            .isPublished(dto.getIsPublished())
            .viewCount(dto.getViewCount())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
}
