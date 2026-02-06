package com.sungbok.church.service;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.repository.PrayerRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrayerRequestService 단위 테스트")
class PrayerRequestServiceTest {

    @Mock
    private PrayerRequestRepository prayerRequestRepository;

    @InjectMocks
    private PrayerRequestService prayerRequestService;

    @Test
    @DisplayName("기도 요청 삭제 - 존재 확인 검증")
    void deletePrayerRequest_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(prayerRequestRepository.existsById(id)).willReturn(true);

        // when
        prayerRequestService.deletePrayerRequest(id);

        // then
        verify(prayerRequestRepository, times(1)).existsById(id);
        verify(prayerRequestRepository, times(1)).deleteById(id);
    }
}
