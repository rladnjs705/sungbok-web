package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.DonationAccount;
import com.sungbok.church.dto.request.DonationAccountRequest;
import com.sungbok.church.dto.response.DonationAccountResponse;
import com.sungbok.church.service.DonationAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DonationAccount Controller
 * 헌금 계좌 관리 REST API
 */
@Tag(name = "헌금 계좌", description = "헌금 계좌 관리 API")
@RestController
@RequestMapping("/api/donation-accounts")
@RequiredArgsConstructor
public class DonationAccountController {

    private final DonationAccountService donationAccountService;

    /**
     * 활성화된 계좌 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<DonationAccountResponse>> getActiveAccounts() {
        List<DonationAccountResponse> accounts = donationAccountService.getActiveAccounts().stream()
            .map(DonationAccountResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    /**
     * 헌금 유형별 계좌 조회
     */
    @GetMapping("/type/{donationType}")
    public ResponseEntity<List<DonationAccountResponse>> getAccountsByDonationType(
            @PathVariable String donationType) {
        List<DonationAccountResponse> accounts = donationAccountService
            .getAccountsByDonationType(donationType).stream()
            .map(DonationAccountResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    /**
     * 계좌 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<DonationAccountResponse> getAccountById(@PathVariable Long id) {
        DonationAccount account = donationAccountService.getAccountById(id);
        return ResponseEntity.ok(DonationAccountResponse.from(account));
    }

    /**
     * 계좌 생성
     */
    @PostMapping
    public ResponseEntity<DonationAccountResponse> createAccount(
            @Valid @RequestBody DonationAccountRequest request) {
        DonationAccount account = toEntity(request);
        DonationAccount created = donationAccountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(DonationAccountResponse.from(created));
    }

    /**
     * 계좌 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<DonationAccountResponse> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody DonationAccountRequest request) {
        DonationAccount account = toEntity(request);
        DonationAccount updated = donationAccountService.updateAccount(id, account);
        return ResponseEntity.ok(DonationAccountResponse.from(updated));
    }

    /**
     * 계좌 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        donationAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private DonationAccount toEntity(DonationAccountRequest request) {
        return DonationAccount.builder()
            .bankName(request.getBankName())
            .accountNumber(request.getAccountNumber())
            .accountHolder(request.getAccountHolder())
            .donationType(request.getDonationType())
            .description(request.getDescription())
            .displayOrder(request.getDisplayOrder())
            .isActive(request.getIsActive())
            .build();
    }
}
