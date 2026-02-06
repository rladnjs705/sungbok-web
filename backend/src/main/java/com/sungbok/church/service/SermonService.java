package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.repository.SermonRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Sermon Service
 * 설교 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SermonService {

    private final SermonRepository sermonRepository;
    private final EntityManager entityManager;

    /**
     * 공개된 설교 목록 조회 (페이징)
     */
    public Page<Sermon> getPublishedSermons(Pageable pageable) {
        return sermonRepository.findByIsPublishedTrueOrderBySermonDateDesc(pageable);
    }

    /**
     * 설교 ID로 조회 및 조회수 증가
     */
    @Transactional
    public Sermon getSermonById(Long id) {
        Sermon sermon = sermonRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("설교를 찾을 수 없습니다: " + id));

        // 조회수 증가
        sermonRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(sermon);

        return sermon;
    }

    /**
     * 설교자별 설교 조회
     */
    public Page<Sermon> getSermonsByPreacher(String preacher, Pageable pageable) {
        return sermonRepository.findByPreacherAndIsPublishedTrue(preacher, pageable);
    }

    /**
     * 날짜 범위로 설교 조회
     */
    public Page<Sermon> getSermonsByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        return sermonRepository.findBySermonDateBetweenAndIsPublishedTrue(
            startDate, endDate, pageable
        );
    }

    /**
     * 추천 설교 목록
     */
    public List<Sermon> getFeaturedSermons() {
        return sermonRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderBySermonDateDesc();
    }

    /**
     * 최신 설교 N개 조회
     */
    public List<Sermon> getLatestSermons() {
        return sermonRepository.findTop10ByIsPublishedTrueOrderBySermonDateDesc();
    }

    /**
     * 본문으로 검색
     */
    public Page<Sermon> searchByBibleVerse(String bibleVerse, Pageable pageable) {
        return sermonRepository.findByBibleVerseContainingAndIsPublishedTrue(
            bibleVerse, pageable
        );
    }

    /**
     * 태그로 검색
     */
    public Page<Sermon> searchByTag(String tag, Pageable pageable) {
        return sermonRepository.findByTagsContainingAndIsPublishedTrue(tag, pageable);
    }

    /**
     * 설교 생성
     */
    @Transactional
    public Sermon createSermon(Sermon sermon) {
        return sermonRepository.save(sermon);
    }

    /**
     * 설교 수정
     */
    @Transactional
    public Sermon updateSermon(Long id, Sermon updatedSermon) {
        Sermon sermon = sermonRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("설교를 찾을 수 없습니다: " + id));

        sermon.setTitle(updatedSermon.getTitle());
        sermon.setPreacher(updatedSermon.getPreacher());
        sermon.setSermonDate(updatedSermon.getSermonDate());
        sermon.setBibleVerse(updatedSermon.getBibleVerse());
        sermon.setSummary(updatedSermon.getSummary());
        sermon.setYoutubeVideoId(updatedSermon.getYoutubeVideoId());
        sermon.setThumbnailUrl(updatedSermon.getThumbnailUrl());
        sermon.setDuration(updatedSermon.getDuration());
        sermon.setTags(updatedSermon.getTags());
        sermon.setIsFeatured(updatedSermon.getIsFeatured());
        sermon.setIsPublished(updatedSermon.getIsPublished());

        return sermonRepository.save(sermon);
    }

    /**
     * 설교 삭제
     */
    @Transactional
    public void deleteSermon(Long id) {
        if (!sermonRepository.existsById(id)) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + id);
        }
        sermonRepository.deleteById(id);
    }

    /**
     * 통계: 총 설교 개수
     */
    public long getTotalSermonCount() {
        return sermonRepository.countByIsPublishedTrue();
    }

    /**
     * 통계: 설교자별 설교 개수
     */
    public long getSermonCountByPreacher(String preacher) {
        return sermonRepository.countByPreacherAndIsPublishedTrue(preacher);
    }
}
