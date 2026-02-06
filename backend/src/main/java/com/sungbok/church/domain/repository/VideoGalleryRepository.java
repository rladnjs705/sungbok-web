package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.VideoGallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * VideoGallery Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface VideoGalleryRepository extends JpaRepository<VideoGallery, Long> {

    /**
     * 공개된 영상 목록 조회 (최신순, 페이징)
     */
    Page<VideoGallery> findByIsPublishedTrueOrderByEventDateDesc(Pageable pageable);

    /**
     * 카테고리별 영상 조회
     */
    Page<VideoGallery> findByCategoryAndIsPublishedTrueOrderByEventDateDesc(
        String category,
        Pageable pageable
    );

    /**
     * YouTube Video ID로 조회
     */
    Optional<VideoGallery> findByYoutubeVideoId(String youtubeVideoId);

    /**
     * 날짜 범위로 영상 조회
     */
    Page<VideoGallery> findByEventDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 최신 영상 N개 조회
     */
    List<VideoGallery> findTop10ByIsPublishedTrueOrderByEventDateDesc();

    /**
     * 제목으로 검색
     */
    Page<VideoGallery> findByTitleContainingAndIsPublishedTrue(String keyword, Pageable pageable);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE VideoGallery v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 카테고리별 영상 개수
     */
    long countByCategoryAndIsPublished(String category, Boolean isPublished);

    /**
     * 공개된 영상 개수
     */
    long countByIsPublishedTrue();

    /**
     * 키워드로 검색 (제목 + 설명)
     */
    @Query("SELECT v FROM VideoGallery v WHERE v.isPublished = true " +
           "AND (v.title LIKE %:keyword% OR v.description LIKE %:keyword%) " +
           "ORDER BY v.eventDate DESC")
    Page<VideoGallery> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
