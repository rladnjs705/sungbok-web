package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Testimony;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.repository.TestimonyRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestimonyService 단위 테스트")
class TestimonyServiceTest {

    @Mock
    private TestimonyRepository testimonyRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TestimonyService testimonyService;

    private Testimony testTestimony;

    @BeforeEach
    void setUp() {
        testTestimony = Testimony.builder()
                .title("하나님의 은혜")
                .content("간증 내용")
                .author("홍길동")
                .category("은혜")
                .isApproved(true)
                .viewCount(0)
                .build();
    }

    @Test
    @DisplayName("간증 조회 - 조회수 증가 및 refresh 검증")
    void getTestimonyById_Success_WithViewCountAndRefresh() {
        // given
        Long id = 1L;
        given(testimonyRepository.findById(id)).willReturn(Optional.of(testTestimony));
        doNothing().when(testimonyRepository).incrementViewCount(id);
        doNothing().when(entityManager).refresh(testTestimony);

        // when
        Testimony result = testimonyService.getTestimonyById(id);

        // then
        assertThat(result).isNotNull();
        verify(testimonyRepository, times(1)).incrementViewCount(id);
        verify(entityManager, times(1)).refresh(testTestimony);
    }

    @Test
    @DisplayName("간증 삭제 - 존재 확인 검증")
    void deleteTestimony_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(testimonyRepository.existsById(id)).willReturn(true);

        // when
        testimonyService.deleteTestimony(id);

        // then
        verify(testimonyRepository, times(1)).existsById(id);
        verify(testimonyRepository, times(1)).deleteById(id);
    }
}
