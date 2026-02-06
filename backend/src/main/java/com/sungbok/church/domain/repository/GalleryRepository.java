package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Gallery Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    /**
     * 공개된 갤러리 목록 조회 (최신순, 페이징)
     */
    Page<Gallery> findByIsPublishedTrueOrderByEventDateDesc(Pageable pageable);

    /**
     * 날짜 범위로 갤러리 조회
     */
    Page<Gallery> findByEventDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 최신 갤러리 N개 조회
     */
    List<Gallery> findTop10ByIsPublishedTrueOrderByEventDateDesc();

    /**
     * 제목으로 검색
     */
    Page<Gallery> findByTitleContainingAndIsPublishedTrue(String keyword, Pageable pageable);

    /**
     * 제목 또는 설명으로 검색
     */
    @Query("SELECT g FROM Gallery g WHERE g.isPublished = true AND " +
           "(g.title LIKE %:keyword% OR g.description LIKE %:keyword%) " +
           "ORDER BY g.eventDate DESC")
    Page<Gallery> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Gallery g SET g.viewCount = g.viewCount + 1 WHERE g.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 공개된 갤러리 개수
     */
    long countByIsPublishedTrue();
}
