package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.enums.NoticeCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notice 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

    private Long id;
    private NoticeCategory category;
    private String title;
    private String content;
    private String author;
    private Boolean isPinned;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
            .id(notice.getId())
            .category(notice.getCategory())
            .title(notice.getTitle())
            .content(notice.getContent())
            .author(notice.getAuthor())
            .isPinned(notice.getIsPinned())
            .viewCount(notice.getViewCount())
            .publishedAt(notice.getPublishedAt())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .build();
    }
}
