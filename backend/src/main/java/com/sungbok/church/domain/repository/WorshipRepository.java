package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Worship Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface WorshipRepository extends JpaRepository<Worship, Long> {

    /**
     * 활성화된 예배 목록 조회 (요일 순서)
     */
    List<Worship> findByIsActiveTrueOrderByDayOfWeekAsc();

    /**
     * 예배 유형으로 조회
     */
    List<Worship> findByTypeAndIsActiveTrue(WorshipType type);

    /**
     * 요일로 조회
     */
    List<Worship> findByDayOfWeekAndIsActiveTrue(DayOfWeek dayOfWeek);

    /**
     * 현재 라이브 중인 예배 조회
     */
    Optional<Worship> findByIsLiveNowTrue();

    /**
     * 라이브 스트리밍 URL이 있는 예배 조회
     */
    List<Worship> findByLiveStreamUrlIsNotNullAndIsActiveTrue();

    /**
     * 예배 존재 여부 확인
     */
    boolean existsByTypeAndDayOfWeek(WorshipType type, DayOfWeek dayOfWeek);
}
