package com.sungbok.church.service;

import com.sungbok.church.domain.entity.VideoGallery;
import com.sungbok.church.domain.repository.VideoGalleryRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoGalleryService 단위 테스트")
class VideoGalleryServiceTest {

    @Mock
    private VideoGalleryRepository videoGalleryRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private VideoGalleryService videoGalleryService;

    @Test
    @DisplayName("비디오 조회 - 조회수 증가 및 refresh 검증")
    void getVideoById_Success_WithViewCountAndRefresh() {
        // given
        Long id = 1L;
        VideoGallery video = VideoGallery.builder()
                .title("영상 제목")
                .youtubeVideoId("abc123")
                .viewCount(0)
                .isPublished(true)
                .build();
        
        given(videoGalleryRepository.findById(id)).willReturn(Optional.of(video));
        doNothing().when(videoGalleryRepository).incrementViewCount(id);
        doNothing().when(entityManager).refresh(video);

        // when
        VideoGallery result = videoGalleryService.getVideoById(id);

        // then
        assertThat(result).isNotNull();
        verify(videoGalleryRepository, times(1)).incrementViewCount(id);
        verify(entityManager, times(1)).refresh(video);
    }

    @Test
    @DisplayName("비디오 삭제 - 존재 확인 검증")
    void deleteVideo_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(videoGalleryRepository.existsById(id)).willReturn(true);

        // when
        videoGalleryService.deleteVideo(id);

        // then
        verify(videoGalleryRepository, times(1)).existsById(id);
        verify(videoGalleryRepository, times(1)).deleteById(id);
    }
}
