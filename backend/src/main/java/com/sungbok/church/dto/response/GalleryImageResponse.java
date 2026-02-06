package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.GalleryImage;
import lombok.*;

import java.time.LocalDateTime;

/**
 * GalleryImage 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageResponse {

    private Long id;
    private Long galleryId;
    private String imageUrl;
    private String thumbnailUrl;
    private String caption;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static GalleryImageResponse from(GalleryImage image) {
        return GalleryImageResponse.builder()
            .id(image.getId())
            .galleryId(image.getGallery() != null ? image.getGallery().getId() : null)
            .imageUrl(image.getImageUrl())
            .thumbnailUrl(image.getThumbnailUrl())
            .caption(image.getCaption())
            .fileSize(image.getFileSize())
            .width(image.getWidth())
            .height(image.getHeight())
            .displayOrder(image.getDisplayOrder())
            .createdAt(image.getCreatedAt())
            .updatedAt(image.getUpdatedAt())
            .build();
    }
}
