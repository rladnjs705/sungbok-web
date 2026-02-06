package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DonationAccount 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationAccountRequest {

    @NotBlank(message = "은행명은 필수입니다")
    @Size(max = 50, message = "은행명은 50자 이하여야 합니다")
    private String bankName;

    @NotBlank(message = "계좌번호는 필수입니다")
    @Size(max = 50, message = "계좌번호는 50자 이하여야 합니다")
    private String accountNumber;

    @NotBlank(message = "예금주는 필수입니다")
    @Size(max = 50, message = "예금주는 50자 이하여야 합니다")
    private String accountHolder;

    @Size(max = 50, message = "헌금 유형은 50자 이하여야 합니다")
    private String donationType;

    private String description;

    private Integer displayOrder;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;
}
