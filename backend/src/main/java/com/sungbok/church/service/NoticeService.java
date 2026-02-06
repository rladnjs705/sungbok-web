package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.domain.repository.NoticeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Notice Service
 * 공지사항 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final EntityManager entityManager;

    /**
     * 공지사항 목록 조회 (페이징)
     */
    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findAllByOrderByPublishedAtDesc(pageable);
    }

    /**
     * 카테고리별 공지사항 조회
     */
    public Page<Notice> getNoticesByCategory(NoticeCategory category, Pageable pageable) {
        return noticeRepository.findByCategoryOrderByPublishedAtDesc(category, pageable);
    }

    /**
     * 상단 고정 공지사항 조회
     */
    public List<Notice> getPinnedNotices() {
        return noticeRepository.findByIsPinnedTrueOrderByPublishedAtDesc();
    }

    /**
     * 공지사항 ID로 조회 및 조회수 증가
     */
    @Transactional
    public Notice getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));

        // 조회수 증가
        noticeRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(notice);

        return notice;
    }

    /**
     * 제목으로 검색
     */
    public Page<Notice> searchByTitle(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContainingOrderByPublishedAtDesc(keyword, pageable);
    }

    /**
     * 제목 또는 내용으로 검색
     */
    public Page<Notice> searchByKeyword(String keyword, Pageable pageable) {
        return noticeRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 공지사항 생성
     */
    @Transactional
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));

        notice.setCategory(updatedNotice.getCategory());
        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        notice.setAuthor(updatedNotice.getAuthor());
        notice.setIsPinned(updatedNotice.getIsPinned());
        notice.setPublishedAt(updatedNotice.getPublishedAt());

        return noticeRepository.save(notice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id);
        }
        noticeRepository.deleteById(id);
    }

    /**
     * 통계: 카테고리별 공지사항 개수
     */
    public long getNoticeCountByCategory(NoticeCategory category) {
        return noticeRepository.countByCategory(category);
    }
}
