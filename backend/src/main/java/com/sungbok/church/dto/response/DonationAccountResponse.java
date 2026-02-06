package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.DonationAccount;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DonationAccount 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationAccountResponse {

    private Long id;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String donationType;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 계좌번호 마스킹 처리
     * 마지막 4자리를 제외한 나머지를 * 로 마스킹
     */
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        return "*".repeat(accountNumber.length() - 4) +
               accountNumber.substring(accountNumber.length() - 4);
    }

    /**
     * Entity -> Response DTO 변환
     */
    public static DonationAccountResponse from(DonationAccount account) {
        return DonationAccountResponse.builder()
            .id(account.getId())
            .bankName(account.getBankName())
            .accountNumber(account.getAccountNumber())
            .accountHolder(account.getAccountHolder())
            .donationType(account.getDonationType())
            .description(account.getDescription())
            .displayOrder(account.getDisplayOrder())
            .isActive(account.getIsActive())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }
}
