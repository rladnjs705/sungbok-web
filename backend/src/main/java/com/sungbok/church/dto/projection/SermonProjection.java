package com.sungbok.church.dto.projection;

import com.sungbok.church.domain.enums.WorshipType;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sermon DTO Projection (읽기 전용)
 * LazyInitializationException 방지 + 성능 최적화
 * 
 * Spring Data JPA Interface-based Projection을 통해
 * 필요한 필드만 SELECT하여 N+1 쿼리 방지 및 성능 향상
 */
public interface SermonProjection {
    Long getId();
    String getTitle();
    String getPreacher();
    LocalDate getSermonDate();
    String getBibleVerse();
    String getSummary();
    String getYoutubeVideoId();
    String getVideoUrl();
    String getThumbnailUrl();
    Integer getDuration();
    String getTags();
    String getDescription();
    Boolean getIsFeatured();
    Boolean getIsPublished();
    Integer getViewCount();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    // Worship 관련 필드 (JOIN으로 fetch)
    Long getWorshipId();
    String getWorshipTitle();
    WorshipType getWorshipType();
}
