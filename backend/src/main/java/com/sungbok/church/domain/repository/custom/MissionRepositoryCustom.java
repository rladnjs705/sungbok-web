package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.projection.MissionProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Mission Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface MissionRepositoryCustom {

    /**
     * 진행 중인 선교 조회 (현재 날짜 기준)
     *
     * @param currentDate 기준 날짜
     * @param pageable 페이징 정보
     * @return 진행 중인 선교 목록 (페이징)
     */
    Page<MissionProjectionDto> findOngoingMissions(LocalDate currentDate, Pageable pageable);

    /**
     * 종료된 선교 조회
     *
     * @param currentDate 기준 날짜
     * @param pageable 페이징 정보
     * @return 종료된 선교 목록 (페이징)
     */
    Page<MissionProjectionDto> findCompletedMissions(LocalDate currentDate, Pageable pageable);

    /**
     * 총 후원금액 집계
     *
     * @return 총 후원금액
     */
    Long calculateTotalSupportAmount();
}
