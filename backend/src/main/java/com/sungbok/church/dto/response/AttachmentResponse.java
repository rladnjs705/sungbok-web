package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.NoticeAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 첨부 파일 Response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Long id;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static AttachmentResponse from(NoticeAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        return AttachmentResponse.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .fileUrl(attachment.getFileUrl())
            .fileSize(attachment.getFileSize())
            .fileType(attachment.getFileType())
            .createdAt(attachment.getCreatedAt())
            .build();
    }

    /**
     * 파일 크기를 human-readable 형식으로 변환
     * 예: 1024 -> "1 KB", 1048576 -> "1 MB"
     */
    public String getFormattedFileSize() {
        if (fileSize == null) return "-";
        
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }
}
