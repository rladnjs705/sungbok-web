package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.domain.repository.GalleryImageRepository;
import com.sungbok.church.domain.repository.GalleryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GalleryService 단위 테스트")
class GalleryServiceTest {

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private GalleryImageRepository galleryImageRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GalleryService galleryService;

    private Gallery testGallery;

    @BeforeEach
    void setUp() {
        testGallery = Gallery.builder()
                .title("2024년 부활절 사진")
                .description("부활절 행사 사진 모음")
                .eventDate(LocalDate.of(2024, 3, 31))
                .coverImageUrl("https://example.com/cover.jpg")
                .viewCount(0)
                .isPublished(true)
                .build();
    }

    @Test
    @DisplayName("갤러리 ID로 조회 성공 - 조회수 증가 및 EntityManager refresh 검증")
    void getGalleryById_Success_WithViewCountAndRefresh() {
        // given
        Long galleryId = 1L;
        given(galleryRepository.findById(galleryId))
                .willReturn(Optional.of(testGallery));
        doNothing().when(galleryRepository).incrementViewCount(galleryId);
        doNothing().when(entityManager).refresh(testGallery);

        // when
        Gallery result = galleryService.getGalleryById(galleryId);

        // then
        assertThat(result).isNotNull();
        verify(galleryRepository, times(1)).incrementViewCount(galleryId);
        verify(entityManager, times(1)).refresh(testGallery);
    }

    @Test
    @DisplayName("갤러리 삭제 성공 - 존재 확인 후 삭제")
    void deleteGallery_Success_WithExistenceCheck() {
        // given
        Long galleryId = 1L;
        given(galleryRepository.existsById(galleryId))
                .willReturn(true);
        doNothing().when(galleryRepository).deleteById(galleryId);

        // when
        galleryService.deleteGallery(galleryId);

        // then
        verify(galleryRepository, times(1)).existsById(galleryId);
        verify(galleryRepository, times(1)).deleteById(galleryId);
    }

    @Test
    @DisplayName("갤러리 삭제 실패 - 존재하지 않는 ID")
    void deleteGallery_NotFound_ThrowsException() {
        // given
        Long galleryId = 999L;
        given(galleryRepository.existsById(galleryId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> galleryService.deleteGallery(galleryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("갤러리를 찾을 수 없습니다");

        verify(galleryRepository, never()).deleteById(anyLong());
    }
}
