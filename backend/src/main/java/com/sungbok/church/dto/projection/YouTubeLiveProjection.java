package com.sungbok.church.dto.projection;

import com.sungbok.church.domain.enums.WorshipType;
import java.time.LocalDateTime;

/**
 * YouTubeLive DTO Projection (읽기 전용)
 * LazyInitializationException 방지 + 성능 최적화
 * 
 * Spring Data JPA Interface-based Projection을 통해
 * 필요한 필드만 SELECT하여 N+1 쿼리 방지 및 성능 향상
 */
public interface YouTubeLiveProjection {
    Long getId();
    String getVideoId();
    String getTitle();
    String getDescription();
    LocalDateTime getScheduledStartTime();
    LocalDateTime getActualStartTime();
    LocalDateTime getActualEndTime();
    String getStatus();
    String getThumbnailUrl();
    Integer getViewCount();
    String getLiveChatId();
    String getConcurrentViewers();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    // Worship 관련 필드 (JOIN으로 fetch)
    Long getWorshipId();
    String getWorshipTitle();
    WorshipType getWorshipType();
}
