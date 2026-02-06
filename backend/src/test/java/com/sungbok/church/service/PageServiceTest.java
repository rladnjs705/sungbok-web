package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Page;
import com.sungbok.church.domain.repository.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * PageService Unit Test
 *
 * Testing Strategy:
 * 1. Repository 메서드 호출 검증
 * 2. 비즈니스 로직 검증 (Slug 중복 체크)
 * 3. 예외 처리 검증
 * 4. 삭제 전 존재 확인 검증 (Phase 2/3 개선사항)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PageService 단위 테스트")
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;

    @InjectMocks
    private PageService pageService;

    private Page testPage;

    @BeforeEach
    void setUp() {
        testPage = Page.builder()
                .slug("about")
                .title("교회 소개")
                .content("<h1>우리 교회를 소개합니다</h1>")
                .metaDescription("교회 소개 페이지")
                .isPublished(true)
                .displayOrder(1)
                .build();
    }

    @Test
    @DisplayName("Slug로 페이지 조회 성공")
    void getPageBySlug_Success() {
        // given
        String slug = "about";
        given(pageRepository.findBySlug(slug))
                .willReturn(Optional.of(testPage));

        // when
        Page result = pageService.getPageBySlug(slug);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo(slug);
        assertThat(result.getTitle()).isEqualTo("교회 소개");
        verify(pageRepository, times(1)).findBySlug(slug);
    }

    @Test
    @DisplayName("Slug로 페이지 조회 실패 - 존재하지 않는 Slug")
    void getPageBySlug_NotFound_ThrowsException() {
        // given
        String slug = "nonexistent";
        given(pageRepository.findBySlug(slug))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pageService.getPageBySlug(slug))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("페이지를 찾을 수 없습니다");

        verify(pageRepository, times(1)).findBySlug(slug);
    }

    @Test
    @DisplayName("공개된 페이지 목록 조회")
    void getPublishedPages_Success() {
        // given
        given(pageRepository.findByIsPublishedTrueOrderByDisplayOrderAsc())
                .willReturn(List.of(testPage));

        // when
        List<Page> result = pageService.getPublishedPages();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSlug()).isEqualTo("about");
        verify(pageRepository, times(1)).findByIsPublishedTrueOrderByDisplayOrderAsc();
    }

    @Test
    @DisplayName("페이지 생성 성공")
    void createPage_Success() {
        // given
        given(pageRepository.existsBySlug(testPage.getSlug()))
                .willReturn(false);
        given(pageRepository.save(testPage))
                .willReturn(testPage);

        // when
        Page result = pageService.createPage(testPage);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("about");
        verify(pageRepository, times(1)).existsBySlug("about");
        verify(pageRepository, times(1)).save(testPage);
    }

    @Test
    @DisplayName("페이지 생성 실패 - Slug 중복")
    void createPage_Failure_DuplicateSlug() {
        // given
        given(pageRepository.existsBySlug(testPage.getSlug()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> pageService.createPage(testPage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 Slug입니다");

        verify(pageRepository, times(1)).existsBySlug("about");
        verify(pageRepository, never()).save(any());
    }

    @Test
    @DisplayName("페이지 수정 성공")
    void updatePage_Success() {
        // given
        Long pageId = 1L;
        Page updatedPage = Page.builder()
                .slug("about")
                .title("수정된 제목")
                .content("<h1>수정된 내용</h1>")
                .metaDescription("수정된 설명")
                .isPublished(true)
                .displayOrder(2)
                .build();

        given(pageRepository.findById(pageId))
                .willReturn(Optional.of(testPage));
        given(pageRepository.save(testPage))
                .willReturn(testPage);

        // when
        Page result = pageService.updatePage(pageId, updatedPage);

        // then
        assertThat(result).isNotNull();
        verify(pageRepository, times(1)).findById(pageId);
        verify(pageRepository, times(1)).save(testPage);
    }

    @Test
    @DisplayName("페이지 수정 성공 - Slug 변경 시 중복 체크")
    void updatePage_Success_WithSlugChange() {
        // given
        Long pageId = 1L;
        Page updatedPage = Page.builder()
                .slug("new-about")
                .title("수정된 제목")
                .content("<h1>수정된 내용</h1>")
                .metaDescription("수정된 설명")
                .isPublished(true)
                .displayOrder(2)
                .build();

        given(pageRepository.findById(pageId))
                .willReturn(Optional.of(testPage));
        given(pageRepository.existsBySlug("new-about"))
                .willReturn(false);
        given(pageRepository.save(testPage))
                .willReturn(testPage);

        // when
        Page result = pageService.updatePage(pageId, updatedPage);

        // then
        assertThat(result).isNotNull();
        verify(pageRepository, times(1)).findById(pageId);
        verify(pageRepository, times(1)).existsBySlug("new-about");
        verify(pageRepository, times(1)).save(testPage);
    }

    @Test
    @DisplayName("페이지 수정 실패 - Slug 중복")
    void updatePage_Failure_DuplicateSlug() {
        // given
        Long pageId = 1L;
        Page updatedPage = Page.builder()
                .slug("existing-slug")
                .title("수정된 제목")
                .build();

        given(pageRepository.findById(pageId))
                .willReturn(Optional.of(testPage));
        given(pageRepository.existsBySlug("existing-slug"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> pageService.updatePage(pageId, updatedPage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 Slug입니다");

        verify(pageRepository, times(1)).findById(pageId);
        verify(pageRepository, times(1)).existsBySlug("existing-slug");
        verify(pageRepository, never()).save(any());
    }

    @Test
    @DisplayName("페이지 삭제 성공 - Phase 2/3 개선사항: 존재 확인 검증")
    void deletePage_Success_WithExistenceCheck() {
        // given
        Long pageId = 1L;
        given(pageRepository.existsById(pageId))
                .willReturn(true);
        doNothing().when(pageRepository).deleteById(pageId);

        // when
        pageService.deletePage(pageId);

        // then
        verify(pageRepository, times(1)).existsById(pageId);
        verify(pageRepository, times(1)).deleteById(pageId);
    }

    @Test
    @DisplayName("페이지 삭제 실패 - 존재하지 않는 ID (Phase 2/3 개선사항 검증)")
    void deletePage_NotFound_ThrowsException() {
        // given
        Long pageId = 999L;
        given(pageRepository.existsById(pageId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> pageService.deletePage(pageId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("페이지를 찾을 수 없습니다");

        verify(pageRepository, times(1)).existsById(pageId);
        verify(pageRepository, never()).deleteById(anyLong());
    }
}
