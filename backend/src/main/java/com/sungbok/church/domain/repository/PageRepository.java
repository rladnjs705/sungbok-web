package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Page Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    /**
     * Slug로 페이지 조회
     */
    Optional<Page> findBySlug(String slug);

    /**
     * 공개된 페이지 목록 조회 (표시 순서)
     */
    List<Page> findByIsPublishedTrueOrderByDisplayOrderAsc();

    /**
     * 페이지 존재 여부 확인
     */
    boolean existsBySlug(String slug);

    /**
     * 제목으로 검색
     */
    List<Page> findByTitleContaining(String keyword);
}
