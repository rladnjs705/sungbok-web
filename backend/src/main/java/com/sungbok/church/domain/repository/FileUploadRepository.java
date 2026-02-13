package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.FileUpload;
import com.sungbok.church.domain.enums.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FileUpload Repository
 */
@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    /**
     * 특정 상태이며 URL 만료 시간이 주어진 시간보다 이전인 파일 목록 조회
     * (만료된 Pre-signed URL 정리용)
     */
    List<FileUpload> findByStatusAndUrlExpiresAtBefore(UploadStatus status, LocalDateTime time);

    /**
     * 특정 상태이며 마지막 업데이트 시간이 주어진 시간보다 이전인 파일 목록 조회
     * (오래된 파일 정리용)
     */
    List<FileUpload> findByStatusAndUpdatedAtBefore(UploadStatus status, LocalDateTime time);
}
