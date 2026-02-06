package com.sungbok.church.service;

import com.sungbok.church.domain.repository.DonationAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DonationAccountService 단위 테스트")
class DonationAccountServiceTest {

    @Mock
    private DonationAccountRepository donationAccountRepository;

    @InjectMocks
    private DonationAccountService donationAccountService;

    @Test
    @DisplayName("헌금 계좌 삭제 - 존재 확인 검증")
    void deleteDonationAccount_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(donationAccountRepository.existsById(id)).willReturn(true);

        // when
        donationAccountService.deleteAccount(id);

        // then
        verify(donationAccountRepository, times(1)).existsById(id);
        verify(donationAccountRepository, times(1)).deleteById(id);
    }
}
