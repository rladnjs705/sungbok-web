package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GalleryImage Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

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
