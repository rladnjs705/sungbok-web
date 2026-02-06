package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Hymn;
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
 * Hymn Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface HymnRepository extends JpaRepository<Hymn, Long> {

    /**
     * 공개된 찬양 목록 조회 (최신순, 페이징)
     */
    Page<Hymn> findByIsPublishedTrueOrderByPerformanceDateDesc(Pageable pageable);

    /**
     * YouTube Video ID로 조회
     */
    Optional<Hymn> findByYoutubeVideoId(String youtubeVideoId);

    /**
     * 아티스트별 찬양 조회
     */
    Page<Hymn> findByArtistAndIsPublishedTrue(String artist, Pageable pageable);

    /**
     * 날짜 범위로 찬양 조회
     */
    Page<Hymn> findByPerformanceDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 최신 찬양 N개 조회
     */
    List<Hymn> findTop10ByIsPublishedTrueOrderByPerformanceDateDesc();

    /**
     * 제목으로 검색
     */
    Page<Hymn> findByTitleContainingAndIsPublishedTrue(String keyword, Pageable pageable);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Hymn h SET h.viewCount = h.viewCount + 1 WHERE h.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 공개된 찬양 개수
     */
    long countByIsPublishedTrue();

    /**
     * 찬송가 번호로 조회
     */
    Optional<Hymn> findByHymnNumber(Integer hymnNumber);

    /**
     * 찬송가 번호 존재 여부
     */
    boolean existsByHymnNumber(Integer hymnNumber);

    /**
     * 제목으로 검색 (공개 여부 무관)
     */
    Page<Hymn> findByTitleContaining(String keyword, Pageable pageable);

    /**
     * 가사로 검색
     */
    Page<Hymn> findByLyricsContaining(String keyword, Pageable pageable);

    /**
     * 키워드로 검색 (제목 + 가사 + 아티스트)
     */
    @Query("SELECT h FROM Hymn h WHERE h.isPublished = true " +
           "AND (h.title LIKE %:keyword% OR h.lyrics LIKE %:keyword% OR h.artist LIKE %:keyword%) " +
           "ORDER BY h.performanceDate DESC")
    Page<Hymn> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 찬양 상위 20개
     */
    List<Hymn> findTop20ByOrderByPerformanceCountDesc();

    /**
     * 최신 찬양 상위 20개
     */
    List<Hymn> findTop20ByOrderByCreatedAtDesc();

    /**
     * 연주 횟수 증가
     */
    @Modifying
    @Query("UPDATE Hymn h SET h.performanceCount = h.performanceCount + 1 WHERE h.id = :id")
    void incrementPerformanceCount(@Param("id") Long id);
}
