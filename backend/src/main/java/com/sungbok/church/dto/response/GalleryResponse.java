package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Gallery;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Gallery 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String coverImageUrl;
    private Integer viewCount;
    private Boolean isPublished;
    private Integer imageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static GalleryResponse from(Gallery gallery) {
        return GalleryResponse.builder()
            .id(gallery.getId())
            .title(gallery.getTitle())
            .description(gallery.getDescription())
            .eventDate(gallery.getEventDate())
            .coverImageUrl(gallery.getCoverImageUrl())
            .viewCount(gallery.getViewCount())
            .isPublished(gallery.getIsPublished())
            .createdAt(gallery.getCreatedAt())
            .updatedAt(gallery.getUpdatedAt())
            .build();
    }
}
