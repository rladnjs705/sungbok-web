package com.sungbok.church.dto.projection;

import com.sungbok.church.domain.enums.MissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mission DTO Class-based Projection (읽기 전용)
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
public class MissionProjectionDto {
    private Long id;
    private String title;
    private MissionType type;
    private String country;
    private String region;
    private String description;
    private String missionaryName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long supportAmount;
    private String photoUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 진행 중인 선교인지 확인
     *
     * @param currentDate 기준 날짜
     * @return 진행 중이면 true
     */
    public boolean isOngoing(LocalDate currentDate) {
        if (startDate == null || !Boolean.TRUE.equals(isActive)) {
            return false;
        }
        if (startDate.isAfter(currentDate)) {
            return false;
        }
        return endDate == null || !endDate.isBefore(currentDate);
    }

    /**
     * 종료된 선교인지 확인
     *
     * @param currentDate 기준 날짜
     * @return 종료되었으면 true
     */
    public boolean isCompleted(LocalDate currentDate) {
        return endDate != null && endDate.isBefore(currentDate);
    }

    /**
     * 예정된 선교인지 확인
     *
     * @param currentDate 기준 날짜
     * @return 예정되었으면 true
     */
    public boolean isUpcoming(LocalDate currentDate) {
        return startDate != null && startDate.isAfter(currentDate);
    }

    /**
     * 후원이 필요한지 확인
     *
     * @param targetAmount 목표 금액
     * @return 후원이 더 필요하면 true
     */
    public boolean needsSupport(Long targetAmount) {
        if (targetAmount == null || supportAmount == null) {
            return true;
        }
        return supportAmount < targetAmount;
    }
}
