package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.entity.NoticeAttachment;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.domain.repository.NoticeAttachmentRepository;
import com.sungbok.church.domain.repository.NoticeRepository;
import com.sungbok.church.dto.projection.NoticeProjectionDto;
import com.sungbok.church.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Notice Service
 * 공지사항 관리 서비스
 * 
 * Cloudflare R2 연동 (파일 업로드/다운로드)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeAttachmentRepository attachmentRepository;
    private final R2StorageService r2StorageService;
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
            .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + id));

        // 조회수 증가
        noticeRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(notice);

        return notice;
    }

    /**
     * 공지사항 상세 조회 (첨부 파일 포함)
     */
    public Notice getNoticeWithAttachments(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + id));
    }

    /**
     * 공지사항의 첨부 파일 목록 조회
     */
    public List<NoticeAttachment> getAttachments(Long noticeId) {
        return attachmentRepository.findByNoticeId(noticeId);
    }

    /**
     * 관련 소식 조회 (같은 카테고리, 랜덤 4개)
     * 현재 공지사항 제외
     */
    public List<Notice> getRelatedNotices(Long currentId, NoticeCategory category) {
        return noticeRepository.findRandomByCategoryExcludingId(category, currentId, 4);
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
    public Page<NoticeProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
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
     * 공지사항 생성 + 첨부 파일 업로드
     */
    @Transactional
    public Notice createNoticeWithAttachments(Notice notice, List<MultipartFile> files) {
        Notice savedNotice = noticeRepository.save(notice);
        
        if (files != null && !files.isEmpty()) {
            uploadAttachments(savedNotice, files);
        }
        
        return savedNotice;
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + id));

        notice.setCategory(updatedNotice.getCategory());
        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        notice.setAuthor(updatedNotice.getAuthor());
        notice.setIsPinned(updatedNotice.getIsPinned());
        notice.setPublishedAt(updatedNotice.getPublishedAt());

        return noticeRepository.save(notice);
    }

    /**
     * 공지사항 삭제 (첨부 파일도 함께 삭제)
     */
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다: " + id));
        
        // 첨부 파일 삭제 (R2 + DB)
        List<NoticeAttachment> attachments = attachmentRepository.findByNoticeId(id);
        for (NoticeAttachment attachment : attachments) {
            r2StorageService.deleteFile(attachment.getFileUrl());
            attachmentRepository.delete(attachment);
        }
        
        noticeRepository.delete(notice);
    }

    /**
     * 첨부 파일 업로드
     */
    @Transactional
    public void uploadAttachments(Notice notice, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            
            // R2에 업로드
            String fileUrl = r2StorageService.uploadFile(file, "notices");
            
            // DB에 저장
            NoticeAttachment attachment = NoticeAttachment.builder()
                .notice(notice)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .build();
            
            attachmentRepository.save(attachment);
        }
    }

    /**
     * 첨부 파일 삭제
     */
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        NoticeAttachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new ResourceNotFoundException("첨부 파일을 찾을 수 없습니다: " + attachmentId));
        
        // R2에서 삭제
        r2StorageService.deleteFile(attachment.getFileUrl());
        
        // DB에서 삭제
        attachmentRepository.delete(attachment);
    }

    /**
     * 통계: 카테고리별 공지사항 개수
     */
    public long getNoticeCountByCategory(NoticeCategory category) {
        return noticeRepository.countByCategory(category);
    }
}
