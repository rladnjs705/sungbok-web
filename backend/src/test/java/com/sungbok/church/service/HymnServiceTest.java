package com.sungbok.church.service;

import com.sungbok.church.domain.repository.HymnRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HymnService 단위 테스트")
class HymnServiceTest {

    @Mock
    private HymnRepository hymnRepository;

    @InjectMocks
    private HymnService hymnService;

    @Test
    @DisplayName("찬송가 삭제 - 존재 확인 검증")
    void deleteHymn_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(hymnRepository.existsById(id)).willReturn(true);

        // when
        hymnService.deleteHymn(id);

        // then
        verify(hymnRepository, times(1)).existsById(id);
        verify(hymnRepository, times(1)).deleteById(id);
    }
}
