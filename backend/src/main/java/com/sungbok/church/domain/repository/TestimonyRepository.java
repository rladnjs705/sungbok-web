package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Testimony;
import com.sungbok.church.domain.repository.custom.TestimonyRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Testimony Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface TestimonyRepository extends JpaRepository<Testimony, Long>, TestimonyRepositoryCustom {

    /**
     * 승인된 간증 목록 조회 (최신순, 페이징)
     */
    Page<Testimony> findByIsApprovedTrueOrderByPublishedAtDesc(Pageable pageable);

    /**
     * 카테고리별 승인된 간증 조회
     */
    Page<Testimony> findByCategoryAndIsApprovedTrueOrderByPublishedAtDesc(
        String category,
        Pageable pageable
    );

    /**
     * 작성자별 간증 조회
     */
    Page<Testimony> findByAuthorAndIsApprovedTrue(String author, Pageable pageable);

    /**
     * 승인 대기 중인 간증 목록
     */
    Page<Testimony> findByIsApprovedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 제목으로 검색
     */
    Page<Testimony> findByTitleContainingAndIsApprovedTrue(String keyword, Pageable pageable);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Testimony t SET t.viewCount = t.viewCount + 1 WHERE t.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 승인된 간증 개수
     */
    long countByIsApprovedTrue();

    /**
     * 승인 대기 중인 간증 개수
     */
    long countByIsApprovedFalse();

    /**
     * 최신 간증 N개 조회
     */
    List<Testimony> findTop10ByIsApprovedTrueOrderByPublishedAtDesc();
}
