package com.sungbok.church.service;

import com.sungbok.church.domain.repository.MinistryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinistryService 단위 테스트")
class MinistryServiceTest {

    @Mock
    private MinistryRepository ministryRepository;

    @InjectMocks
    private MinistryService ministryService;

    @Test
    @DisplayName("사역 삭제 - 존재 확인 검증")
    void deleteMinistry_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(ministryRepository.existsById(id)).willReturn(true);

        // when
        ministryService.deleteMinistry(id);

        // then
        verify(ministryRepository, times(1)).existsById(id);
        verify(ministryRepository, times(1)).deleteById(id);
    }
}
