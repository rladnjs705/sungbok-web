package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Sermon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Sermon Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long> {

    /**
     * ID로 조회 (Worship 포함)
     * N+1 쿼리 방지를 위해 Worship을 함께 로드
     */
    @EntityGraph(attributePaths = {"worship"})
    @Override
    Optional<Sermon> findById(Long id);

    /**
     * 공개된 설교 목록 조회 (최신순, 페이징)
     */
    Page<Sermon> findByIsPublishedTrueOrderBySermonDateDesc(Pageable pageable);

    /**
     * 설교자별 설교 목록 조회
     */
    Page<Sermon> findByPreacherAndIsPublishedTrue(String preacher, Pageable pageable);

    /**
     * 날짜 범위로 설교 조회
     */
    Page<Sermon> findBySermonDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * YouTube Video ID로 조회
     */
    Optional<Sermon> findByYoutubeVideoId(String youtubeVideoId);

    /**
     * 추천 설교 목록 조회
     */
    List<Sermon> findByIsFeaturedTrueAndIsPublishedTrueOrderBySermonDateDesc();

    /**
     * 최신 설교 N개 조회
     */
    List<Sermon> findTop10ByIsPublishedTrueOrderBySermonDateDesc();

    /**
     * 본문으로 검색
     */
    Page<Sermon> findByBibleVerseContainingAndIsPublishedTrue(
        String bibleVerse,
        Pageable pageable
    );

    /**
     * 태그로 검색
     */
    Page<Sermon> findByTagsContainingAndIsPublishedTrue(
        String tag,
        Pageable pageable
    );

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Sermon s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 설교 개수 조회
     */
    long countByIsPublishedTrue();

    /**
     * 설교자별 설교 개수
     */
    long countByPreacherAndIsPublishedTrue(String preacher);
}
