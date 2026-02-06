package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Pastor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Pastor Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface PastorRepository extends JpaRepository<Pastor, Long> {

    /**
     * 활성화된 목사/교역자 목록 조회 (표시 순서)
     */
    List<Pastor> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 이름으로 조회
     */
    Optional<Pastor> findByName(String name);

    /**
     * 직책별 조회
     */
    List<Pastor> findByPositionAndIsActiveTrue(String position);

    /**
     * 이메일로 조회
     */
    Optional<Pastor> findByEmail(String email);

    /**
     * 활성화 상태별 목사/교역자 개수
     */
    long countByIsActive(Boolean isActive);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
}
