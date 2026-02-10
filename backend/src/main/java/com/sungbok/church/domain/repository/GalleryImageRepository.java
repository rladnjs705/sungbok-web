package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.domain.repository.custom.GalleryImageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GalleryImage Repository
 * Context7 네이밍 규칙 적용
 *
 * QueryDSL Custom Repository 통합:
 * - GalleryImageRepositoryCustom 상속으로 QueryDSL 쿼리 메서드 사용 가능
 * - 명시적 LEFT JOIN으로 LazyInitializationException 방지
 * - DTO Projection으로 N+1 쿼리 최적화
 */
@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long>, GalleryImageRepositoryCustom {

    /**
     * 갤러리별 이미지 목록 조회 (표시 순서)
     */
    List<GalleryImage> findByGalleryOrderByDisplayOrderAsc(Gallery gallery);

    /**
     * 갤러리 ID로 이미지 목록 조회 (표시 순서)
     */
    List<GalleryImage> findByGalleryIdOrderByDisplayOrderAsc(Long galleryId);

    /**
     * 갤러리 ID로 이미지 삭제
     */
    void deleteByGalleryId(Long galleryId);

    /**
     * 갤러리의 이미지 개수
     */
    long countByGalleryId(Long galleryId);

    /**
     * 갤러리별 첫 번째 이미지 조회
     */
    GalleryImage findFirstByGalleryIdOrderByDisplayOrderAsc(Long galleryId);
}
