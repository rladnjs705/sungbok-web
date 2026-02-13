package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.enums.MissionType;
import com.sungbok.church.domain.repository.custom.MissionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Mission Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, MissionRepositoryCustom {

    /**
     * 활성화된 선교 목록 조회 (최신순)
     */
    List<Mission> findByIsActiveTrueOrderByStartDateDesc();

    /**
     * 선교 유형별 조회
     */
    List<Mission> findByTypeAndIsActiveTrueOrderByStartDateDesc(MissionType type);

    /**
     * 국가별 선교 조회
     */
    List<Mission> findByCountryAndIsActiveTrue(String country);

    /**
     * 선교사 이름으로 검색
     */
    List<Mission> findByMissionaryNameContainingAndIsActiveTrue(String keyword);

    /**
     * 선교 유형별 개수
     */
    long countByTypeAndIsActive(MissionType type, Boolean isActive);
}
