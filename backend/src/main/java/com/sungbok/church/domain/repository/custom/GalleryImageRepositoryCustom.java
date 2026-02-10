package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.dto.response.GalleryImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * GalleryImageRepository QueryDSL Custom Interface
 *
 * Context7 Best Practices:
 * - Custom Repository 패턴으로 QueryDSL 쿼리 분리
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - Native Query 제거로 LazyInitializationException 방지
 */
public interface GalleryImageRepositoryCustom {

    /**
     * 갤러리별 이미지 조회 (QueryDSL)
     *
     * @param galleryId 갤러리 ID
     * @param pageable  페이징 정보
     * @return 갤러리 이미지 목록
     */
    Page<GalleryImageResponse> findByGalleryId(Long galleryId, Pageable pageable);

    /**
     * 최신 이미지 조회 (QueryDSL)
     *
     * @param limit 조회할 개수
     * @return 최신 이미지 목록
     */
    List<GalleryImageResponse> findLatestImages(int limit);

    /**
     * 인기 이미지 조회 (조회수 기준, QueryDSL)
     * Note: 현재 GalleryImage 엔티티에 viewCount가 없으면 생략 가능
     *
     * @param limit 조회할 개수
     * @return 인기 이미지 목록
     */
    List<GalleryImageResponse> findPopularImages(int limit);
}
