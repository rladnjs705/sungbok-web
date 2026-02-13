package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Testimony;
import com.sungbok.church.domain.repository.TestimonyRepository;
import com.sungbok.church.dto.projection.TestimonyProjectionDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Testimony Service
 * 간증 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestimonyService {

    private final TestimonyRepository testimonyRepository;
    private final EntityManager entityManager;

    /**
     * 승인된 간증 목록 조회 (페이징)
     */
    public Page<Testimony> getApprovedTestimonies(Pageable pageable) {
        return testimonyRepository.findByIsApprovedTrueOrderByPublishedAtDesc(pageable);
    }

    /**
     * 카테고리별 승인된 간증 조회
     */
    public Page<Testimony> getTestimoniesByCategory(String category, Pageable pageable) {
        return testimonyRepository.findByCategoryAndIsApprovedTrueOrderByPublishedAtDesc(
            category, pageable
        );
    }

    /**
     * 간증 ID로 조회 및 조회수 증가
     */
    @Transactional
    public Testimony getTestimonyById(Long id) {
        Testimony testimony = testimonyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("간증을 찾을 수 없습니다: " + id));

        // 승인된 간증만 조회수 증가
        if (testimony.getIsApproved()) {
            testimonyRepository.incrementViewCount(id);
            // 엔티티 새로고침 (업데이트된 조회수 반영)
            entityManager.refresh(testimony);
        }

        return testimony;
    }

    /**
     * 작성자별 간증 조회
     */
    public Page<Testimony> getTestimoniesByAuthor(String author, Pageable pageable) {
        return testimonyRepository.findByAuthorAndIsApprovedTrue(author, pageable);
    }

    /**
     * 승인 대기 중인 간증 목록
     */
    public Page<Testimony> getPendingTestimonies(Pageable pageable) {
        return testimonyRepository.findByIsApprovedFalseOrderByCreatedAtDesc(pageable);
    }

    /**
     * 최신 간증 N개 조회
     */
    public List<Testimony> getLatestTestimonies() {
        return testimonyRepository.findTop10ByIsApprovedTrueOrderByPublishedAtDesc();
    }

    /**
     * 제목으로 검색
     */
    public Page<Testimony> searchByTitle(String keyword, Pageable pageable) {
        return testimonyRepository.findByTitleContainingAndIsApprovedTrue(keyword, pageable);
    }

    /**
     * 제목 또는 내용으로 검색
     */
    public Page<TestimonyProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        return testimonyRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 간증 생성
     */
    @Transactional
    public Testimony createTestimony(Testimony testimony) {
        // 기본적으로 승인 대기 상태
        testimony.setIsApproved(false);
        return testimonyRepository.save(testimony);
    }

    /**
     * 간증 승인
     */
    @Transactional
    public Testimony approveTestimony(Long id) {
        Testimony testimony = testimonyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("간증을 찾을 수 없습니다: " + id));

        testimony.setIsApproved(true);
        testimony.setPublishedAt(LocalDateTime.now());

        return testimonyRepository.save(testimony);
    }

    /**
     * 간증 수정
     */
    @Transactional
    public Testimony updateTestimony(Long id, Testimony updatedTestimony) {
        Testimony testimony = testimonyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("간증을 찾을 수 없습니다: " + id));

        testimony.setTitle(updatedTestimony.getTitle());
        testimony.setContent(updatedTestimony.getContent());
        testimony.setCategory(updatedTestimony.getCategory());

        return testimonyRepository.save(testimony);
    }

    /**
     * 간증 삭제
     */
    @Transactional
    public void deleteTestimony(Long id) {
        if (!testimonyRepository.existsById(id)) {
            throw new IllegalArgumentException("간증을 찾을 수 없습니다: " + id);
        }
        testimonyRepository.deleteById(id);
    }

    /**
     * 통계: 승인된 간증 개수
     */
    public long getApprovedTestimonyCount() {
        return testimonyRepository.countByIsApprovedTrue();
    }

    /**
     * 통계: 승인 대기 중인 간증 개수
     */
    public long getPendingTestimonyCount() {
        return testimonyRepository.countByIsApprovedFalse();
    }
}
