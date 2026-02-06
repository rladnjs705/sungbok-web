package com.sungbok.church.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Pastor 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastorRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;

    @NotBlank(message = "직책은 필수입니다")
    @Size(max = 50, message = "직책은 50자 이하여야 합니다")
    private String position;

    @Size(max = 500, message = "사진 URL은 500자 이하여야 합니다")
    private String photo;

    @Size(max = 500, message = "사진 URL은 500자 이하여야 합니다")
    private String photoUrl;

    private String bio;

    private String education;

    @Size(max = 100, message = "사역 분야는 100자 이하여야 합니다")
    private String ministryArea;

    private String career;

    private String message;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;

    private Integer displayOrder;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;
}
