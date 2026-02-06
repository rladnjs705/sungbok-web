package com.sungbok.church.service;

import com.sungbok.church.domain.entity.DonationAccount;
import com.sungbok.church.domain.repository.DonationAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DonationAccount Service
 * 헌금 계좌 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonationAccountService {

    private final DonationAccountRepository donationAccountRepository;

    /**
     * 활성화된 계좌 목록 조회
     */
    public List<DonationAccount> getActiveAccounts() {
        return donationAccountRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * 헌금 유형별 계좌 조회
     */
    public List<DonationAccount> getAccountsByDonationType(String donationType) {
        return donationAccountRepository.findByDonationTypeAndIsActiveTrueOrderByDisplayOrderAsc(
            donationType
        );
    }

    /**
     * 계좌 ID로 조회
     */
    public DonationAccount getAccountById(Long id) {
        return donationAccountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + id));
    }

    /**
     * 계좌 생성
     */
    @Transactional
    public DonationAccount createAccount(DonationAccount account) {
        return donationAccountRepository.save(account);
    }

    /**
     * 계좌 수정
     */
    @Transactional
    public DonationAccount updateAccount(Long id, DonationAccount updatedAccount) {
        DonationAccount account = getAccountById(id);

        account.setBankName(updatedAccount.getBankName());
        account.setAccountNumber(updatedAccount.getAccountNumber());
        account.setAccountHolder(updatedAccount.getAccountHolder());
        account.setDonationType(updatedAccount.getDonationType());
        account.setDescription(updatedAccount.getDescription());
        account.setDisplayOrder(updatedAccount.getDisplayOrder());
        account.setIsActive(updatedAccount.getIsActive());

        return donationAccountRepository.save(account);
    }

    /**
     * 계좌 삭제
     */
    @Transactional
    public void deleteAccount(Long id) {
        if (!donationAccountRepository.existsById(id)) {
            throw new IllegalArgumentException("헌금 계좌를 찾을 수 없습니다: " + id);
        }
        donationAccountRepository.deleteById(id);
    }
}
