package com.sungbok.church.service;

import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("YouTubeLiveService 단위 테스트")
class YouTubeLiveServiceTest {

    @Mock
    private YouTubeLiveRepository youtubeLiveRepository;

    @InjectMocks
    private YouTubeLiveService youtubeLiveService;

    @Test
    @DisplayName("현재 라이브 방송 조회 성공")
    void getCurrentLiveStreams_Success() {
        // given
        YouTubeLive live = YouTubeLive.builder()
                .youtubeVideoId("test123")
                .title("주일 예배 실황")
                .status(LiveStatus.LIVE)
                .actualStartTime(LocalDateTime.now())
                .build();

        given(youtubeLiveRepository.findByStatusOrderByActualStartTimeDesc(LiveStatus.LIVE))
                .willReturn(List.of(live));

        // when
        List<YouTubeLive> result = youtubeLiveService.getCurrentLiveStreams();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getYoutubeVideoId()).isEqualTo("test123");
        verify(youtubeLiveRepository, times(1)).findByStatusOrderByActualStartTimeDesc(LiveStatus.LIVE);
    }

    @Test
    @DisplayName("라이브 삭제 - 존재 확인 검증")
    void deleteLive_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(youtubeLiveRepository.existsById(id)).willReturn(true);

        // when
        youtubeLiveService.deleteLive(id);

        // then
        verify(youtubeLiveRepository, times(1)).existsById(id);
        verify(youtubeLiveRepository, times(1)).deleteById(id);
    }
}
