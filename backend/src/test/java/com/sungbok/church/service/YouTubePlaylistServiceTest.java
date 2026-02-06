package com.sungbok.church.service;

import com.sungbok.church.domain.entity.YouTubePlaylist;
import com.sungbok.church.domain.repository.YouTubePlaylistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("YouTubePlaylistService 단위 테스트")
class YouTubePlaylistServiceTest {

    @Mock
    private YouTubePlaylistRepository youtubePlaylistRepository;

    @InjectMocks
    private YouTubePlaylistService youtubePlaylistService;

    @Test
    @DisplayName("활성 재생목록 조회 성공")
    void getActivePlaylists_Success() {
        // given
        YouTubePlaylist playlist = YouTubePlaylist.builder()
                .playlistId("PLtest123")
                .title("설교 영상")
                .isActive(true)
                .build();
        
        given(youtubePlaylistRepository.findByIsActiveTrueOrderByDisplayOrderAsc())
                .willReturn(List.of(playlist));

        // when
        List<YouTubePlaylist> result = youtubePlaylistService.getActivePlaylists();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlaylistId()).isEqualTo("PLtest123");
        verify(youtubePlaylistRepository, times(1)).findByIsActiveTrueOrderByDisplayOrderAsc();
    }
}
