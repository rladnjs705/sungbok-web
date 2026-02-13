package com.sungbok.church.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Pre-signed URL 발급 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignUrlRequest {

    @NotBlank(message = "파일명은 필수입니다")
    @Size(max = 255, message = "파일명은 255자 이하여야 합니다")
    private String filename;

    @NotBlank(message = "폴터 경로는 필수입니다")
    @Size(max = 100, message = "폴터 경로는 100자 이하여야 합니다")
    private String folder;

    @NotBlank(message = "Content-Type은 필수입니다")
    @Size(max = 100, message = "Content-Type은 100자 이하여야 합니다")
    private String contentType;

    @NotNull(message = "파일 크기는 필수입니다")
    @Positive(message = "파일 크기는 0보다 커야 합니다")
    private Long fileSize;

    @Size(max = 64, message = "체크섬은 64자 이하여야 합니다")
    private String checksum;
}
