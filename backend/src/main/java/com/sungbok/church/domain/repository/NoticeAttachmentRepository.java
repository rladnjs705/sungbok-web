package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.entity.NoticeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NoticeAttachment Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface NoticeAttachmentRepository extends JpaRepository<NoticeAttachment, Long> {

    /**
     * 공지사항별 첨부파일 목록 조회
     */
    List<NoticeAttachment> findByNotice(Notice notice);

    /**
     * 공지사항 ID로 첨부파일 목록 조회
     */
    List<NoticeAttachment> findByNoticeId(Long noticeId);

    /**
     * 공지사항 ID로 첨부파일 삭제
     */
    void deleteByNoticeId(Long noticeId);

    /**
     * 파일 타입별 첨부파일 조회
     */
    List<NoticeAttachment> findByFileType(String fileType);

    /**
     * 공지사항의 첨부파일 개수
     */
    long countByNoticeId(Long noticeId);
}
