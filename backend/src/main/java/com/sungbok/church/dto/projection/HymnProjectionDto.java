package com.sungbok.church.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hymn DTO Class-based Projection (읽기 전용)
 *
 * 목적:
 * - QueryDSL Projections.constructor()와 호환
 * - N+1 쿼리 방지 (모든 필드를 단일 쿼리로 조회)
 * - DTO 변환 오버헤드 최소화
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HymnProjectionDto {
    private Long id;
    private Integer hymnNumber;
    private String title;
    private String composer;
    private String lyricist;
    private String lyrics;
    private String artist;
    private String youtubeVideoId;
    private String videoUrl;
    private String youtubeUrl;
    private String sheetMusicUrl;
    private String thumbnailUrl;
    private LocalDate performanceDate;
    private Integer viewCount;
    private Integer performanceCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 인기 찬양 여부 (연주 횟수 기준)
     *
     * @param threshold 기준 횟수
     * @return 기준 이상이면 true
     */
    public boolean isPopular(int threshold) {
        return performanceCount != null && performanceCount >= threshold;
    }

    /**
     * 최신 찬양 여부
     *
     * @param days 기준 일수
     * @return 기준 일수 이내면 true
     */
    public boolean isRecent(int days) {
        if (performanceDate == null) return false;
        return performanceDate.isAfter(LocalDate.now().minusDays(days));
    }
}
