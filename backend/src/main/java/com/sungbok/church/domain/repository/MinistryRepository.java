package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Ministry;
import com.sungbok.church.domain.enums.MinistryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Ministry Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface MinistryRepository extends JpaRepository<Ministry, Long> {

    /**
     * 활성화된 부서 목록 조회 (표시 순서)
     */
    List<Ministry> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 카테고리별 부서 조회
     */
    List<Ministry> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(MinistryCategory category);

    /**
     * 대상 연령별 부서 조회
     */
    List<Ministry> findByTargetAgeAndIsActiveTrue(String targetAge);

    /**
     * 리더 이름으로 검색
     */
    List<Ministry> findByLeaderContaining(String keyword);

    /**
     * 부서 이름으로 검색
     */
    List<Ministry> findByNameContainingAndIsActiveTrue(String keyword);

    /**
     * 카테고리별 부서 개수
     */
    long countByCategoryAndIsActive(MinistryCategory category, Boolean isActive);
}
