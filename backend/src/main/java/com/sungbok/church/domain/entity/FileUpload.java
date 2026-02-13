package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 파일 업로드 Entity
 */
@Entity
@Table(name = "file_upload", indexes = {
    @Index(name = "idx_file_upload_status", columnList = "status"),
    @Index(name = "idx_file_upload_url_expires", columnList = "url_expires_at"),
    @Index(name = "idx_file_upload_uploaded_by", columnList = "uploaded_by"),
    @Index(name = "idx_file_upload_status_updated", columnList = "status, updated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"checksum"})
public class FileUpload extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UploadStatus status = UploadStatus.PENDING;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, length = 500, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(length = 64)
    private String checksum;

    @Column(nullable = false, length = 50)
    private String uploadedBy;

    @Column(length = 1000)
    private String fileUrl;

    private LocalDateTime urlExpiresAt;

    private LocalDateTime uploadedAt;

    private LocalDateTime verifiedAt;
}
