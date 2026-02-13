package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.domain.repository.custom.EventRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Repository
 * Context7 네이밍 규칙 적용
 *
 * Custom Repository 상속으로 QueryDSL 사용
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    /**
     * 공개된 행사 목록 조회 (시작일 순, 페이징)
     */
    Page<Event> findByIsPublishedTrueOrderByStartDateAsc(Pageable pageable);

    /**
     * 공개된 행사 목록 조회 (시작일 역순, 페이징)
     */
    Page<Event> findByIsPublishedTrueOrderByStartDateDesc(Pageable pageable);

    /**
     * 예정된 행사 목록 조회 (현재 시간 이후)
     */
    Page<Event> findByStartDateAfterAndIsPublishedTrueOrderByStartDateAsc(
        LocalDateTime currentDate,
        Pageable pageable
    );

    /**
     * 종료된 행사 목록 조회
     */
    Page<Event> findByEndDateBeforeAndIsPublishedTrueOrderByStartDateDesc(
        LocalDateTime currentDate,
        Pageable pageable
    );

    /**
     * 등록이 필요한 행사 목록
     */
    Page<Event> findByRegistrationRequiredTrueAndIsPublishedTrueOrderByStartDateAsc(Pageable pageable);

    /**
     * 제목으로 검색
     */
    Page<Event> findByTitleContainingAndIsPublishedTrue(String keyword, Pageable pageable);

    /**
     * 장소로 검색
     */
    Page<Event> findByLocationContainingAndIsPublishedTrue(String location, Pageable pageable);

    /**
     * 참가자 수 증가
     */
    @Modifying
    @Query("UPDATE Event e SET e.currentParticipants = e.currentParticipants + 1 WHERE e.id = :id")
    void incrementParticipants(@Param("id") Long id);

    /**
     * 참가자 수 감소
     */
    @Modifying
    @Query("UPDATE Event e SET e.currentParticipants = e.currentParticipants - 1 WHERE e.id = :id AND e.currentParticipants > 0")
    void decrementParticipants(@Param("id") Long id);

    /**
     * 공개된 행사 개수
     */
    long countByIsPublishedTrue();

    /**
     * 카테고리와 공개 여부로 행사 수 조회
     */
    long countByCategoryAndIsPublished(String category, boolean isPublished);

    /**
     * 카테고리별 공개된 행사 조회
     */
    Page<Event> findByCategoryAndIsPublishedTrue(String category, Pageable pageable);

    /**
     * 기간별 공개된 행사 조회
     */
    Page<Event> findByStartDateBetweenAndIsPublishedTrue(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Event e SET e.viewCount = e.viewCount + 1 WHERE e.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 등록이 필요한 행사 목록 (List 버전)
     */
    List<Event> findByRegistrationRequiredTrueAndIsPublishedTrue();
}
