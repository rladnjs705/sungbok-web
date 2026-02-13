package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Bulletin;
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
 * Bulletin Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, Long>, com.sungbok.church.domain.repository.custom.BulletinRepositoryCustom {

    /**
     * 공개된 주보 목록 조회 (최신순, 페이징)
     */
    Page<Bulletin> findByIsPublishedTrueOrderByBulletinDateDesc(Pageable pageable);

    /**
     * 날짜로 주보 조회
     */
    Optional<Bulletin> findByBulletinDate(LocalDate bulletinDate);

    /**
     * 날짜 범위로 주보 조회
     */
    Page<Bulletin> findByBulletinDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 최신 주보 N개 조회
     */
    List<Bulletin> findTop10ByIsPublishedTrueOrderByBulletinDateDesc();

    /**
     * 제목으로 검색
     */
    Page<Bulletin> findByTitleContainingAndIsPublishedTrue(String keyword, Pageable pageable);

    /**
     * 주보 존재 여부 확인
     */
    boolean existsByBulletinDate(LocalDate bulletinDate);

    /**
     * 공개된 주보 개수
     */
    long countByIsPublishedTrue();
}
