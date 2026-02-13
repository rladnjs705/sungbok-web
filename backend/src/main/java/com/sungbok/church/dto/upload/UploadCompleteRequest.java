package com.sungbok.church.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 파일 업로드 완료 확인 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadCompleteRequest {

    @NotBlank(message = "저장소 키는 필수입니다")
    private String key;

    @Size(max = 64, message = "체크섬은 64자 이하여야 합니다")
    private String checksum;
}
