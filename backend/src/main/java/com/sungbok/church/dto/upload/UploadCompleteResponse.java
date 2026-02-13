package com.sungbok.church.dto.upload;

import com.sungbok.church.domain.entity.FileUpload;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 파일 업로드 완료 확인 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadCompleteResponse {

    private Boolean success;
    private String fileUrl;
    private String error;
    private LocalDateTime verifiedAt;

    /**
     * 성공 응답 생성
     */
    public static UploadCompleteResponse success(FileUpload fileUpload) {
        return UploadCompleteResponse.builder()
            .success(true)
            .fileUrl(fileUpload.getFileUrl())
            .verifiedAt(fileUpload.getVerifiedAt())
            .build();
    }

    /**
     * 실패 응답 생성
     */
    public static UploadCompleteResponse failure(String error) {
        return UploadCompleteResponse.builder()
            .success(false)
            .error(error)
            .build();
    }
}
