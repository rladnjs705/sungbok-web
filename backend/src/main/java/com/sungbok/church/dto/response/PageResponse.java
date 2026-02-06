package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Page;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Page 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse {

    private Long id;
    private String slug;
    private String title;
    private String content;
    private String metaDescription;
    private Boolean isPublished;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static PageResponse from(Page page) {
        return PageResponse.builder()
            .id(page.getId())
            .slug(page.getSlug())
            .title(page.getTitle())
            .content(page.getContent())
            .metaDescription(page.getMetaDescription())
            .isPublished(page.getIsPublished())
            .displayOrder(page.getDisplayOrder())
            .createdAt(page.getCreatedAt())
            .updatedAt(page.getUpdatedAt())
            .build();
    }
}
