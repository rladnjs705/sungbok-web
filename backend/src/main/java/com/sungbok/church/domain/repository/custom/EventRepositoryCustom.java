package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.projection.EventProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Repository Custom Interface
 *
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface EventRepositoryCustom {

    /**
     * 진행 중인 행사 목록 조회
     *
     * 조건:
     * - isPublished = true
     * - startDate <= currentDate <= endDate
     *
     * @param currentDate 현재 날짜/시간
     * @return 진행 중인 행사 목록
     */
    List<EventProjectionDto> findOngoingEvents(LocalDateTime currentDate);

    /**
     * 참가 가능한 행사 조회 (정원 미달)
     *
     * 조건:
     * - isPublished = true
     * - registrationRequired = true
     * - currentParticipants < maxParticipants
     *
     * @return 참가 가능한 행사 목록
     */
    List<EventProjectionDto> findAvailableEvents();

    /**
     * 다가오는 행사 조회 (LocalDate 버전)
     *
     * 조건:
     * - isPublished = true
     * - CAST(startDate AS date) >= currentDate
     *
     * @param currentDate 현재 날짜
     * @return 다가오는 행사 목록
     */
    List<EventProjectionDto> findUpcomingEvents(LocalDate currentDate);

    /**
     * 키워드로 행사 검색 (제목 + 설명 + 장소)
     *
     * 조건:
     * - isPublished = true
     * - title OR description OR location LIKE %keyword%
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색된 행사 페이지
     */
    Page<EventProjectionDto> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 카테고리별 공개된 행사 조회
     *
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 행사 페이지
     */
    Page<EventProjectionDto> findByCategoryAndPublished(String category, Pageable pageable);

    /**
     * 기간별 공개된 행사 조회
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 행사 페이지
     */
    Page<EventProjectionDto> findByDateRangeAndPublished(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
}
