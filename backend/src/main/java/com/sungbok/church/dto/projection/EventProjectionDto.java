package com.sungbok.church.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO Class-based Projection (읽기 전용)
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
public class EventProjectionDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String organizer;
    private String posterImageUrl;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean registrationRequired;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 참가 가능 여부 계산
     *
     * @return 참가 가능하면 true
     */
    public boolean isAvailable() {
        if (!registrationRequired) {
            return true; // 등록이 필요 없으면 항상 참가 가능
        }
        return currentParticipants < maxParticipants;
    }

    /**
     * 정원 마감 여부
     *
     * @return 정원이 찼으면 true
     */
    public boolean isFull() {
        if (!registrationRequired) {
            return false; // 등록이 필요 없으면 항상 false
        }
        return currentParticipants >= maxParticipants;
    }

    /**
     * 진행 중 여부 계산
     *
     * @return 진행 중이면 true
     */
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * 종료 여부 계산
     *
     * @return 종료되었으면 true
     */
    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }

    /**
     * 예정 여부 계산
     *
     * @return 예정이면 true
     */
    public boolean isUpcoming() {
        return LocalDateTime.now().isBefore(startDate);
    }
}
