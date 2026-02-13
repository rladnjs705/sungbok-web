package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.projection.HymnProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Hymn Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface HymnRepositoryCustom {

    /**
     * 키워드로 검색 (제목 + 가사 + 아티스트)
     *
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 결과 (페이징)
     */
    Page<HymnProjectionDto> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 아티스트별 찬양 조회 (통계 포함)
     *
     * @param artist 아티스트명
     * @param pageable 페이징 정보
     * @return 아티스트의 찬양 목록 (페이징)
     */
    Page<HymnProjectionDto> findByArtistWithStats(String artist, Pageable pageable);

    /**
     * 전체 연주 횟수 합계 계산
     *
     * @return 전체 연주 횟수 합계
     */
    Long calculateTotalPerformanceCount();
}
