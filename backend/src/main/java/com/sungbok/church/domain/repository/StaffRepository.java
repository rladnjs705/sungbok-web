package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Staff;
import com.sungbok.church.domain.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Staff Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * 활성화된 섬기는 이들 목록 조회 (표시 순서)
     */
    List<Staff> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 역할별 조회
     */
    List<Staff> findByRoleAndIsActiveTrueOrderByDisplayOrderAsc(StaffRole role);

    /**
     * 부서별 조회
     */
    List<Staff> findByDepartmentAndIsActiveTrue(String department);

    /**
     * 이름으로 검색
     */
    List<Staff> findByNameContaining(String keyword);

    /**
     * 역할별 인원 수
     */
    long countByRoleAndIsActive(StaffRole role, Boolean isActive);
}
