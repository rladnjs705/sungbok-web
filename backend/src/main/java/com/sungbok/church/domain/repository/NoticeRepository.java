package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.domain.repository.custom.NoticeRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Notice Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    /**
     * 공지사항 목록 조회 (최신순, 페이징)
     */
    Page<Notice> findAllByOrderByPublishedAtDesc(Pageable pageable);

    /**
     * 카테고리별 공지사항 조회
     */
    Page<Notice> findByCategoryOrderByPublishedAtDesc(NoticeCategory category, Pageable pageable);

    /**
     * 상단 고정 공지사항 조회
     */
    List<Notice> findByIsPinnedTrueOrderByPublishedAtDesc();

    /**
     * 제목 검색
     */
    Page<Notice> findByTitleContainingOrderByPublishedAtDesc(String keyword, Pageable pageable);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 카테고리별 공지사항 개수
     */
    long countByCategory(NoticeCategory category);

    /**
     * 관련 소식 조회 (같은 카테고리, 현재 ID 제외, 랜덤 N개)
     * Native Query 사용 - PostgreSQL의 RANDOM() 함수 활용
     */
    @Query(value = "SELECT * FROM notice WHERE category = :category AND id != :excludeId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Notice> findRandomByCategoryExcludingId(
        @Param("category") NoticeCategory category,
        @Param("excludeId") Long excludeId,
        @Param("limit") int limit
    );
}
