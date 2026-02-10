package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.dto.response.YouTubeLiveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * YouTubeLiveRepository QueryDSL Custom Interface
 *
 * Context7 Best Practices:
 * - Custom Repository 패턴으로 QueryDSL 쿼리 분리
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - Native Query 제거로 LazyInitializationException 방지
 */
public interface YouTubeLiveRepositoryCustom {

    /**
     * 전체 YouTube Live 목록 조회 (QueryDSL)
     *
     * @param pageable 페이징 정보
     * @return YouTube Live 목록
     */
    Page<YouTubeLiveResponse> findAllWithDetails(Pageable pageable);

    /**
     * 예배 유형별 YouTube Live 조회 (QueryDSL)
     *
     * @param worshipType 예배 유형
     * @return YouTube Live 목록
     */
    List<YouTubeLiveResponse> findByWorshipType(WorshipType worshipType);

    /**
     * 최신 YouTube Live 조회 (QueryDSL)
     *
     * @param limit 조회할 개수
     * @return 최신 YouTube Live 목록
     */
    List<YouTubeLiveResponse> findLatest(int limit);

    /**
     * 예정된 YouTube Live 조회 (QueryDSL)
     *
     * @param pageable 페이징 정보
     * @return 예정된 YouTube Live 목록
     */
    Page<YouTubeLiveResponse> findUpcoming(Pageable pageable);
}
