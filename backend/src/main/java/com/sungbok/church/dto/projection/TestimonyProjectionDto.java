package com.sungbok.church.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Testimony DTO Class-based Projection (읽기 전용)
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
public class TestimonyProjectionDto {
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
     * 최근 간증 여부 확인
     *
     * @param days 기준 일수
     * @return 기준 일수 이내면 true
     */
    public boolean isRecent(int days) {
        if (publishedAt == null) return false;
        return publishedAt.isAfter(LocalDateTime.now().minusDays(days));
    }

    /**
     * 인기 간증 여부 확인
     *
     * @param threshold 조회수 기준
     * @return 기준 이상이면 true
     */
    public boolean isPopular(int threshold) {
        return viewCount != null && viewCount >= threshold;
    }
}
