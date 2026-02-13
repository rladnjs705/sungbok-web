package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.projection.NoticeProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Notice Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface NoticeRepositoryCustom {

    /**
     * 제목 또는 내용 검색
     *
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 결과 (페이징)
     */
    Page<NoticeProjectionDto> searchByKeyword(String keyword, Pageable pageable);
}
