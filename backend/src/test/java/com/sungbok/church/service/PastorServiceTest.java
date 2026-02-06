package com.sungbok.church.service;

import com.sungbok.church.domain.repository.PastorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PastorService 단위 테스트")
class PastorServiceTest {

    @Mock
    private PastorRepository pastorRepository;

    @InjectMocks
    private PastorService pastorService;

    @Test
    @DisplayName("목회자 삭제 - 존재 확인 검증")
    void deletePastor_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(pastorRepository.existsById(id)).willReturn(true);

        // when
        pastorService.deletePastor(id);

        // then
        verify(pastorRepository, times(1)).existsById(id);
        verify(pastorRepository, times(1)).deleteById(id);
    }
}
