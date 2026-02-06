package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.domain.repository.NoticeRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * NoticeService Unit Test
 *
 * Testing Strategy:
 * 1. Repository 메서드 호출 검증
 * 2. 비즈니스 로직 검증
 * 3. 예외 처리 검증
 * 4. 조회수 증가 로직 검증 (Race Condition 방지)
 * 5. 삭제 전 존재 확인 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeService 단위 테스트")
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private NoticeService noticeService;

    private Notice testNotice;

    @BeforeEach
    void setUp() {
        testNotice = Notice.builder()
                .title("테스트 공지사항")
                .content("테스트 내용")
                .category(NoticeCategory.ANNOUNCEMENT)
                .author("관리자")
                .viewCount(0)
                .publishedAt(LocalDateTime.now())
                .isPinned(false)
                .build();
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공")
    void getNotices_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> expectedPage = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findAllByOrderByPublishedAtDesc(pageable))
                .willReturn(expectedPage);

        // when
        Page<Notice> result = noticeService.getNotices(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 공지사항");
        verify(noticeRepository, times(1)).findAllByOrderByPublishedAtDesc(pageable);
    }

    @Test
    @DisplayName("공지사항 ID로 조회 성공 - 조회수 증가 및 EntityManager refresh 검증")
    void getNoticeById_Success_WithViewCountAndRefresh() {
        // given
        Long noticeId = 1L;
        given(noticeRepository.findById(noticeId))
                .willReturn(Optional.of(testNotice));
        doNothing().when(noticeRepository).incrementViewCount(noticeId);
        doNothing().when(entityManager).refresh(testNotice);

        // when
        Notice result = noticeService.getNoticeById(noticeId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 공지사항");

        // Race Condition 방지를 위한 검증
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeRepository, times(1)).incrementViewCount(noticeId);
        verify(entityManager, times(1)).refresh(testNotice);
    }

    @Test
    @DisplayName("공지사항 ID로 조회 실패 - 존재하지 않는 ID")
    void getNoticeById_NotFound_ThrowsException() {
        // given
        Long noticeId = 999L;
        given(noticeRepository.findById(noticeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.getNoticeById(noticeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("공지사항을 찾을 수 없습니다");

        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeRepository, never()).incrementViewCount(anyLong());
        verify(entityManager, never()).refresh(any());
    }

    @Test
    @DisplayName("공지사항 생성 성공")
    void createNotice_Success() {
        // given
        given(noticeRepository.save(any(Notice.class)))
                .willReturn(testNotice);

        // when
        Notice result = noticeService.createNotice(testNotice);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 공지사항");
        verify(noticeRepository, times(1)).save(testNotice);
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void updateNotice_Success() {
        // given
        Long noticeId = 1L;
        Notice updatedNotice = Notice.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .category(NoticeCategory.EVENT)
                .author("관리자")
                .build();

        given(noticeRepository.findById(noticeId))
                .willReturn(Optional.of(testNotice));
        given(noticeRepository.save(any(Notice.class)))
                .willReturn(testNotice);

        // when
        Notice result = noticeService.updateNotice(noticeId, updatedNotice);

        // then
        assertThat(result).isNotNull();
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeRepository, times(1)).save(testNotice);
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 존재하지 않는 ID")
    void updateNotice_NotFound_ThrowsException() {
        // given
        Long noticeId = 999L;
        given(noticeRepository.findById(noticeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.updateNotice(noticeId, testNotice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("공지사항을 찾을 수 없습니다");

        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeRepository, never()).save(any());
    }

    @Test
    @DisplayName("공지사항 삭제 성공 - 존재 확인 후 삭제")
    void deleteNotice_Success_WithExistenceCheck() {
        // given
        Long noticeId = 1L;
        given(noticeRepository.existsById(noticeId))
                .willReturn(true);
        doNothing().when(noticeRepository).deleteById(noticeId);

        // when
        noticeService.deleteNotice(noticeId);

        // then
        verify(noticeRepository, times(1)).existsById(noticeId);
        verify(noticeRepository, times(1)).deleteById(noticeId);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 존재하지 않는 ID (Phase 2/3 개선사항 검증)")
    void deleteNotice_NotFound_ThrowsException() {
        // given
        Long noticeId = 999L;
        given(noticeRepository.existsById(noticeId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> noticeService.deleteNotice(noticeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("공지사항을 찾을 수 없습니다");

        verify(noticeRepository, times(1)).existsById(noticeId);
        verify(noticeRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("카테고리별 공지사항 조회")
    void getNoticesByCategory_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> expectedPage = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByCategoryOrderByPublishedAtDesc(
                NoticeCategory.ANNOUNCEMENT, pageable))
                .willReturn(expectedPage);

        // when
        Page<Notice> result = noticeService.getNoticesByCategory(NoticeCategory.ANNOUNCEMENT, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(noticeRepository, times(1))
                .findByCategoryOrderByPublishedAtDesc(NoticeCategory.ANNOUNCEMENT, pageable);
    }

    @Test
    @DisplayName("상단 고정 공지사항 조회")
    void getPinnedNotices_Success() {
        // given
        testNotice.setIsPinned(true);
        given(noticeRepository.findByIsPinnedTrueOrderByPublishedAtDesc())
                .willReturn(List.of(testNotice));

        // when
        List<Notice> result = noticeService.getPinnedNotices();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsPinned()).isTrue();
        verify(noticeRepository, times(1)).findByIsPinnedTrueOrderByPublishedAtDesc();
    }

    @Test
    @DisplayName("제목으로 공지사항 검색")
    void searchByTitle_Success() {
        // given
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> expectedPage = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByTitleContainingOrderByPublishedAtDesc(keyword, pageable))
                .willReturn(expectedPage);

        // when
        Page<Notice> result = noticeService.searchByTitle(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(noticeRepository, times(1))
                .findByTitleContainingOrderByPublishedAtDesc(keyword, pageable);
    }
}
