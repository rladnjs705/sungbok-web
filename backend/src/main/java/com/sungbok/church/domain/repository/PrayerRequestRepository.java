package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.enums.PrayerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PrayerRequest Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface PrayerRequestRepository extends JpaRepository<PrayerRequest, Long> {

    /**
     * 승인된 기도요청 목록 조회 (최신순, 페이징)
     */
    Page<PrayerRequest> findByIsApprovedTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 상태별 승인된 기도요청 조회
     */
    Page<PrayerRequest> findByStatusAndIsApprovedTrueOrderByCreatedAtDesc(
        PrayerStatus status,
        Pageable pageable
    );

    /**
     * 승인 대기 중인 기도요청 목록
     */
    Page<PrayerRequest> findByIsApprovedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 기도 중인 요청 목록
     */
    List<PrayerRequest> findByStatusAndIsApprovedTrueOrderByCreatedAtDesc(PrayerStatus status);

    /**
     * 응답받은 기도요청 목록
     */
    Page<PrayerRequest> findByStatusAndIsApprovedTrueOrderByAnsweredAtDesc(
        PrayerStatus status,
        Pageable pageable
    );

    /**
     * 제목으로 검색
     */
    Page<PrayerRequest> findByTitleContainingAndIsApprovedTrue(String keyword, Pageable pageable);

    /**
     * 기도수 증가
     */
    @Modifying
    @Query("UPDATE PrayerRequest p SET p.prayerCount = p.prayerCount + 1 WHERE p.id = :id")
    void incrementPrayerCount(@Param("id") Long id);

    /**
     * 상태별 기도요청 개수
     */
    long countByStatusAndIsApproved(PrayerStatus status, Boolean isApproved);

    /**
     * 승인 대기 중인 기도요청 개수
     */
    long countByIsApprovedFalse();

    /**
     * 최신 기도요청 N개 조회
     */
    List<PrayerRequest> findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
}
