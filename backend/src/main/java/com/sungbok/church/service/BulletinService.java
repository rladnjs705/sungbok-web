package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Bulletin;
import com.sungbok.church.domain.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Bulletin Service
 * 주보 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BulletinService {

    private final BulletinRepository bulletinRepository;

    /**
     * 공개된 주보 목록 조회 (페이징)
     */
    public Page<Bulletin> getPublishedBulletins(Pageable pageable) {
        return bulletinRepository.findByIsPublishedTrueOrderByBulletinDateDesc(pageable);
    }

    /**
     * 주보 ID로 조회
     */
    public Bulletin getBulletinById(Long id) {
        return bulletinRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주보를 찾을 수 없습니다: " + id));
    }

    /**
     * 날짜로 주보 조회
     */
    public Bulletin getBulletinByDate(LocalDate bulletinDate) {
        return bulletinRepository.findByBulletinDate(bulletinDate)
            .orElseThrow(() -> new IllegalArgumentException(
                "해당 날짜의 주보를 찾을 수 없습니다: " + bulletinDate
            ));
    }

    /**
     * 날짜 범위로 주보 조회
     */
    public Page<Bulletin> getBulletinsByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        return bulletinRepository.findByBulletinDateBetweenAndIsPublishedTrue(
            startDate, endDate, pageable
        );
    }

    /**
     * 최신 주보 N개 조회
     */
    public List<Bulletin> getLatestBulletins() {
        return bulletinRepository.findTop10ByIsPublishedTrueOrderByBulletinDateDesc();
    }

    /**
     * 제목으로 검색
     */
    public Page<Bulletin> searchByTitle(String keyword, Pageable pageable) {
        return bulletinRepository.findByTitleContainingAndIsPublishedTrue(keyword, pageable);
    }

    /**
     * 주보 다운로드 (다운로드 수 증가)
     */
    @Transactional
    public Bulletin downloadBulletin(Long id) {
        Bulletin bulletin = getBulletinById(id);

        // 다운로드 수 증가
        bulletinRepository.incrementDownloadCount(id);

        return bulletin;
    }

    /**
     * 주보 생성
     */
    @Transactional
    public Bulletin createBulletin(Bulletin bulletin) {
        validateBulletinDateUniqueness(bulletin.getBulletinDate());
        return bulletinRepository.save(bulletin);
    }

    /**
     * 주보 수정
     */
    @Transactional
    public Bulletin updateBulletin(Long id, Bulletin updatedBulletin) {
        Bulletin bulletin = getBulletinById(id);

        // 날짜 변경 시 중복 체크
        if (!bulletin.getBulletinDate().equals(updatedBulletin.getBulletinDate())) {
            validateBulletinDateUniqueness(updatedBulletin.getBulletinDate());
            bulletin.setBulletinDate(updatedBulletin.getBulletinDate());
        }

        bulletin.setTitle(updatedBulletin.getTitle());
        bulletin.setPdfUrl(updatedBulletin.getPdfUrl());
        bulletin.setFileSize(updatedBulletin.getFileSize());
        bulletin.setThumbnailUrl(updatedBulletin.getThumbnailUrl());
        bulletin.setIsPublished(updatedBulletin.getIsPublished());

        return bulletinRepository.save(bulletin);
    }

    /**
     * 주보 삭제
     */
    @Transactional
    public void deleteBulletin(Long id) {
        if (!bulletinRepository.existsById(id)) {
            throw new IllegalArgumentException("주보를 찾을 수 없습니다: " + id);
        }
        bulletinRepository.deleteById(id);
    }

    /**
     * 주보 날짜 중복 체크
     */
    private void validateBulletinDateUniqueness(LocalDate bulletinDate) {
        if (bulletinRepository.existsByBulletinDate(bulletinDate)) {
            throw new IllegalArgumentException("해당 날짜의 주보가 이미 존재합니다: " + bulletinDate);
        }
    }

    /**
     * 통계: 총 주보 개수
     */
    public long getTotalBulletinCount() {
        return bulletinRepository.countByIsPublishedTrue();
    }
}
