package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.response.GalleryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * GalleryRepository QueryDSL Custom Interface
 *
 * Context7 Best Practices:
 * - Custom Repository 패턴으로 QueryDSL 쿼리 분리
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - @EntityGraph 제거로 LazyInitializationException 방지
 */
public interface GalleryRepositoryCustom {

    /**
     * 공개된 갤러리 목록 조회 (페이징)
     */
    Page<GalleryResponse> findPublishedGalleriesWithProjection(Pageable pageable);

    /**
     * 날짜 범위로 갤러리 조회 (페이징)
     */
    Page<GalleryResponse> findByDateRangeWithProjection(
        LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 제목으로 검색 (페이징)
     */
    Page<GalleryResponse> searchByTitleWithProjection(String keyword, Pageable pageable);

    /**
     * 제목 또는 설명으로 검색 (페이징)
     */
    Page<GalleryResponse> searchByKeywordWithProjection(String keyword, Pageable pageable);

    /**
     * 최신 갤러리 N개 조회
     */
    List<GalleryResponse> findLatestWithProjection(int limit);
}
