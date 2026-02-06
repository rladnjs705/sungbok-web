package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Testimony;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Testimony 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestimonyResponse {

    private Long id;
    private String title;
    private String author;
    private String content;
    private String category;
    private Boolean isApproved;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static TestimonyResponse from(Testimony testimony) {
        return TestimonyResponse.builder()
            .id(testimony.getId())
            .title(testimony.getTitle())
            .author(testimony.getAuthor())
            .content(testimony.getContent())
            .category(testimony.getCategory())
            .isApproved(testimony.getIsApproved())
            .viewCount(testimony.getViewCount())
            .publishedAt(testimony.getPublishedAt())
            .createdAt(testimony.getCreatedAt())
            .updatedAt(testimony.getUpdatedAt())
            .build();
    }
}
