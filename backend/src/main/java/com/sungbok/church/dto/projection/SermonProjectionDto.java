package com.sungbok.church.dto.projection;

import com.sungbok.church.domain.enums.WorshipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sermon DTO Class-based Projection (읽기 전용)
 * Interface-based Projection의 대안으로, Spring Data JPA native query와 완벽 호환
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SermonProjectionDto {
    private Long id;
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

    // Worship 관련 필드
    private Long worshipId;
    private String worshipTitle;
    private String worshipType; // WorshipType을 String으로 받음 (native query 호환)
    
    /**
     * worshipType을 Enum으로 변환
     */
    public WorshipType getWorshipTypeEnum() {
        return worshipType != null ? WorshipType.valueOf(worshipType) : null;
    }
}
