package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.DonationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DonationAccount Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface DonationAccountRepository extends JpaRepository<DonationAccount, Long> {

    /**
     * 활성화된 헌금 계좌 목록 조회 (표시 순서)
     */
    List<DonationAccount> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 헌금 유형별 계좌 조회
     */
    List<DonationAccount> findByDonationTypeAndIsActiveTrueOrderByDisplayOrderAsc(String donationType);

    /**
     * 은행명별 계좌 조회
     */
    List<DonationAccount> findByBankNameAndIsActiveTrue(String bankName);

    /**
     * 계좌번호로 조회
     */
    Optional<DonationAccount> findByAccountNumber(String accountNumber);

    /**
     * 계좌번호 존재 여부 확인
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * 활성화된 계좌 개수
     */
    long countByIsActive(Boolean isActive);
}
