package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.projection.TestimonyProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Testimony Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface TestimonyRepositoryCustom {

    /**
     * 제목 또는 내용으로 검색 (승인된 간증만)
     *
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 결과 (페이징)
     */
    Page<TestimonyProjectionDto> searchByKeyword(String keyword, Pageable pageable);
}
