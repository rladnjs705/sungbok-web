package com.sungbok.church.dto.upload;

import com.sungbok.church.domain.entity.FileUpload;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Pre-signed URL 발급 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignUrlResponse {

    private Long uploadId;
    private String url;
    private String key;
    private LocalDateTime expiresAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static PresignUrlResponse from(FileUpload fileUpload, String presignedUrl) {
        return PresignUrlResponse.builder()
            .uploadId(fileUpload.getId())
            .url(presignedUrl)
            .key(fileUpload.getStorageKey())
            .expiresAt(fileUpload.getUrlExpiresAt())
            .build();
    }
}
