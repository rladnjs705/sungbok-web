package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Bulletin;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Bulletin 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinResponse {

    private Long id;
    private String title;
    private LocalDate bulletinDate;
    private String pdfUrl;
    private Long fileSize;
    private String thumbnailUrl;
    private Integer downloadCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static BulletinResponse from(Bulletin bulletin) {
        return BulletinResponse.builder()
            .id(bulletin.getId())
            .title(bulletin.getTitle())
            .bulletinDate(bulletin.getBulletinDate())
            .pdfUrl(bulletin.getPdfUrl())
            .fileSize(bulletin.getFileSize())
            .thumbnailUrl(bulletin.getThumbnailUrl())
            .downloadCount(bulletin.getDownloadCount())
            .isPublished(bulletin.getIsPublished())
            .createdAt(bulletin.getCreatedAt())
            .updatedAt(bulletin.getUpdatedAt())
            .build();
    }
}
