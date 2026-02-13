package com.sungbok.church.dto.projection;

import java.time.LocalDateTime;

/**
 * GalleryImage DTO Projection (읽기 전용)
 * LazyInitializationException 방지 + 성능 최적화
 * 
 * Spring Data JPA Interface-based Projection을 통해
 * 필요한 필드만 SELECT하여 N+1 쿼리 방지 및 성능 향상
 */
public interface GalleryImageProjection {
    Long getId();
    String getImageUrl();
    String getThumbnailUrl();
    String getCaption();
    Integer getDisplayOrder();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    // Gallery 관련 필드 (JOIN으로 fetch)
    Long getGalleryId();
    String getGalleryTitle();
}
