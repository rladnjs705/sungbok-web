package com.sungbok.church.service;

import com.sungbok.church.domain.repository.SermonRepository;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import com.sungbok.church.domain.repository.YouTubePlaylistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("YouTubeSyncService 단위 테스트")
class YouTubeSyncServiceTest {

    @Mock
    private SermonRepository sermonRepository;

    @Mock
    private YouTubeLiveRepository youtubeLiveRepository;

    @Mock
    private YouTubePlaylistRepository youtubePlaylistRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private YouTubeSyncService youtubeSyncService;

    @Test
    @DisplayName("YouTube Sync Service 초기화 확인")
    void youtubeSyncService_Initialization() {
        // then
        assertNotNull(youtubeSyncService);
    }
}
