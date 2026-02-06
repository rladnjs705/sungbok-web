package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Bulletin;
import com.sungbok.church.domain.repository.BulletinRepository;
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

/**
 * BulletinService Unit Test
 *
 * Testing Strategy:
 * 1. Repository 메서드 호출 검증
 * 2. 비즈니스 로직 검증 (날짜 중복 체크, 다운로드 수 증가)
 * 3. 예외 처리 검증
 * 4. 삭제 전 존재 확인 검증 (Phase 2/3 개선사항)
 * 5. 다운로드 수 증가 로직 검증 (Race Condition 방지)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BulletinService 단위 테스트")
class BulletinServiceTest {

    @Mock
    private BulletinRepository bulletinRepository;

    @InjectMocks
    private BulletinService bulletinService;

    private Bulletin testBulletin;

    @BeforeEach
    void setUp() {
        testBulletin = Bulletin.builder()
                .bulletinDate(LocalDate.of(2024, 1, 7))
                .title("2024년 1월 첫째주 주보")
                .pdfUrl("https://example.com/bulletin.pdf")
                .fileSize(1024000L)
                .thumbnailUrl("https://example.com/thumb.jpg")
                .downloadCount(0)
                .isPublished(true)
                .build();
    }

    @Test
    @DisplayName("공개된 주보 목록 조회")
    void getPublishedBulletins_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bulletin> expectedPage = new PageImpl<>(List.of(testBulletin));
        given(bulletinRepository.findByIsPublishedTrueOrderByBulletinDateDesc(pageable))
                .willReturn(expectedPage);

        // when
        Page<Bulletin> result = bulletinService.getPublishedBulletins(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("2024년 1월 첫째주 주보");
        verify(bulletinRepository, times(1)).findByIsPublishedTrueOrderByBulletinDateDesc(pageable);
    }

    @Test
    @DisplayName("주보 ID로 조회 성공")
    void getBulletinById_Success() {
        // given
        Long bulletinId = 1L;
        given(bulletinRepository.findById(bulletinId))
                .willReturn(Optional.of(testBulletin));

        // when
        Bulletin result = bulletinService.getBulletinById(bulletinId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("2024년 1월 첫째주 주보");
        verify(bulletinRepository, times(1)).findById(bulletinId);
    }

    @Test
    @DisplayName("주보 ID로 조회 실패 - 존재하지 않는 ID")
    void getBulletinById_NotFound_ThrowsException() {
        // given
        Long bulletinId = 999L;
        given(bulletinRepository.findById(bulletinId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bulletinService.getBulletinById(bulletinId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주보를 찾을 수 없습니다");

        verify(bulletinRepository, times(1)).findById(bulletinId);
    }

    @Test
    @DisplayName("날짜로 주보 조회 성공")
    void getBulletinByDate_Success() {
        // given
        LocalDate bulletinDate = LocalDate.of(2024, 1, 7);
        given(bulletinRepository.findByBulletinDate(bulletinDate))
                .willReturn(Optional.of(testBulletin));

        // when
        Bulletin result = bulletinService.getBulletinByDate(bulletinDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBulletinDate()).isEqualTo(bulletinDate);
        verify(bulletinRepository, times(1)).findByBulletinDate(bulletinDate);
    }

    @Test
    @DisplayName("날짜 범위로 주보 조회")
    void getBulletinsByDateRange_Success() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bulletin> expectedPage = new PageImpl<>(List.of(testBulletin));
        given(bulletinRepository.findByBulletinDateBetweenAndIsPublishedTrue(
                startDate, endDate, pageable))
                .willReturn(expectedPage);

        // when
        Page<Bulletin> result = bulletinService.getBulletinsByDateRange(startDate, endDate, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(bulletinRepository, times(1))
                .findByBulletinDateBetweenAndIsPublishedTrue(startDate, endDate, pageable);
    }

    @Test
    @DisplayName("최신 주보 조회")
    void getLatestBulletins_Success() {
        // given
        given(bulletinRepository.findTop10ByIsPublishedTrueOrderByBulletinDateDesc())
                .willReturn(List.of(testBulletin));

        // when
        List<Bulletin> result = bulletinService.getLatestBulletins();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(bulletinRepository, times(1)).findTop10ByIsPublishedTrueOrderByBulletinDateDesc();
    }

    @Test
    @DisplayName("제목으로 검색")
    void searchByTitle_Success() {
        // given
        String keyword = "주보";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bulletin> expectedPage = new PageImpl<>(List.of(testBulletin));
        given(bulletinRepository.findByTitleContainingAndIsPublishedTrue(keyword, pageable))
                .willReturn(expectedPage);

        // when
        Page<Bulletin> result = bulletinService.searchByTitle(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(bulletinRepository, times(1))
                .findByTitleContainingAndIsPublishedTrue(keyword, pageable);
    }

    @Test
    @DisplayName("주보 다운로드 - 다운로드 수 증가 검증 (Race Condition 방지)")
    void downloadBulletin_Success_WithDownloadCountIncrement() {
        // given
        Long bulletinId = 1L;
        given(bulletinRepository.findById(bulletinId))
                .willReturn(Optional.of(testBulletin));
        doNothing().when(bulletinRepository).incrementDownloadCount(bulletinId);

        // when
        Bulletin result = bulletinService.downloadBulletin(bulletinId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("2024년 1월 첫째주 주보");

        // Race Condition 방지를 위한 검증
        verify(bulletinRepository, times(1)).findById(bulletinId);
        verify(bulletinRepository, times(1)).incrementDownloadCount(bulletinId);
    }

    @Test
    @DisplayName("주보 생성 성공")
    void createBulletin_Success() {
        // given
        given(bulletinRepository.existsByBulletinDate(testBulletin.getBulletinDate()))
                .willReturn(false);
        given(bulletinRepository.save(testBulletin))
                .willReturn(testBulletin);

        // when
        Bulletin result = bulletinService.createBulletin(testBulletin);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("2024년 1월 첫째주 주보");
        verify(bulletinRepository, times(1)).existsByBulletinDate(testBulletin.getBulletinDate());
        verify(bulletinRepository, times(1)).save(testBulletin);
    }

    @Test
    @DisplayName("주보 생성 실패 - 날짜 중복")
    void createBulletin_Failure_DuplicateDate() {
        // given
        given(bulletinRepository.existsByBulletinDate(testBulletin.getBulletinDate()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> bulletinService.createBulletin(testBulletin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 날짜의 주보가 이미 존재합니다");

        verify(bulletinRepository, times(1)).existsByBulletinDate(testBulletin.getBulletinDate());
        verify(bulletinRepository, never()).save(any());
    }

    @Test
    @DisplayName("주보 수정 성공")
    void updateBulletin_Success() {
        // given
        Long bulletinId = 1L;
        Bulletin updatedBulletin = Bulletin.builder()
                .bulletinDate(LocalDate.of(2024, 1, 7))
                .title("수정된 주보 제목")
                .pdfUrl("https://example.com/updated.pdf")
                .fileSize(2048000L)
                .thumbnailUrl("https://example.com/updated-thumb.jpg")
                .isPublished(true)
                .build();

        given(bulletinRepository.findById(bulletinId))
                .willReturn(Optional.of(testBulletin));
        given(bulletinRepository.save(testBulletin))
                .willReturn(testBulletin);

        // when
        Bulletin result = bulletinService.updateBulletin(bulletinId, updatedBulletin);

        // then
        assertThat(result).isNotNull();
        verify(bulletinRepository, times(1)).findById(bulletinId);
        verify(bulletinRepository, times(1)).save(testBulletin);
    }

    @Test
    @DisplayName("주보 수정 성공 - 날짜 변경 시 중복 체크")
    void updateBulletin_Success_WithDateChange() {
        // given
        Long bulletinId = 1L;
        LocalDate newDate = LocalDate.of(2024, 1, 14);
        Bulletin updatedBulletin = Bulletin.builder()
                .bulletinDate(newDate)
                .title("수정된 주보 제목")
                .pdfUrl("https://example.com/updated.pdf")
                .fileSize(2048000L)
                .thumbnailUrl("https://example.com/updated-thumb.jpg")
                .isPublished(true)
                .build();

        given(bulletinRepository.findById(bulletinId))
                .willReturn(Optional.of(testBulletin));
        given(bulletinRepository.existsByBulletinDate(newDate))
                .willReturn(false);
        given(bulletinRepository.save(testBulletin))
                .willReturn(testBulletin);

        // when
        Bulletin result = bulletinService.updateBulletin(bulletinId, updatedBulletin);

        // then
        assertThat(result).isNotNull();
        verify(bulletinRepository, times(1)).findById(bulletinId);
        verify(bulletinRepository, times(1)).existsByBulletinDate(newDate);
        verify(bulletinRepository, times(1)).save(testBulletin);
    }

    @Test
    @DisplayName("주보 삭제 성공 - Phase 2/3 개선사항: 존재 확인 검증")
    void deleteBulletin_Success_WithExistenceCheck() {
        // given
        Long bulletinId = 1L;
        given(bulletinRepository.existsById(bulletinId))
                .willReturn(true);
        doNothing().when(bulletinRepository).deleteById(bulletinId);

        // when
        bulletinService.deleteBulletin(bulletinId);

        // then
        verify(bulletinRepository, times(1)).existsById(bulletinId);
        verify(bulletinRepository, times(1)).deleteById(bulletinId);
    }

    @Test
    @DisplayName("주보 삭제 실패 - 존재하지 않는 ID (Phase 2/3 개선사항 검증)")
    void deleteBulletin_NotFound_ThrowsException() {
        // given
        Long bulletinId = 999L;
        given(bulletinRepository.existsById(bulletinId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> bulletinService.deleteBulletin(bulletinId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주보를 찾을 수 없습니다");

        verify(bulletinRepository, times(1)).existsById(bulletinId);
        verify(bulletinRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("통계: 총 주보 개수")
    void getTotalBulletinCount_Success() {
        // given
        given(bulletinRepository.countByIsPublishedTrue())
                .willReturn(10L);

        // when
        long result = bulletinService.getTotalBulletinCount();

        // then
        assertThat(result).isEqualTo(10L);
        verify(bulletinRepository, times(1)).countByIsPublishedTrue();
    }
}
