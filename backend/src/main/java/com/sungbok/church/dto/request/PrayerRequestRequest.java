package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * PrayerRequest 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerRequestRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotBlank(message = "요청자는 필수입니다")
    @Size(max = 50, message = "요청자는 50자 이하여야 합니다")
    private String requester;

    @NotNull(message = "익명 여부는 필수입니다")
    @Builder.Default
    private Boolean isAnonymous = false;
}
