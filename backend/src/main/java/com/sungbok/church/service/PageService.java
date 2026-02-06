package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Page;
import com.sungbok.church.domain.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Page Service
 * 정적 페이지 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageService {

    private final PageRepository pageRepository;

    /**
     * Slug로 페이지 조회
     */
    public Page getPageBySlug(String slug) {
        return pageRepository.findBySlug(slug)
            .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다: " + slug));
    }

    /**
     * 공개된 페이지 목록 조회
     */
    public List<Page> getPublishedPages() {
        return pageRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
    }

    /**
     * 페이지 생성
     */
    @Transactional
    public Page createPage(Page page) {
        validateSlugUniqueness(page.getSlug());
        return pageRepository.save(page);
    }

    /**
     * 페이지 수정
     */
    @Transactional
    public Page updatePage(Long id, Page updatedPage) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다: " + id));

        // Slug 변경 시 중복 체크
        if (!page.getSlug().equals(updatedPage.getSlug())) {
            validateSlugUniqueness(updatedPage.getSlug());
            page.setSlug(updatedPage.getSlug());
        }

        page.setTitle(updatedPage.getTitle());
        page.setContent(updatedPage.getContent());
        page.setMetaDescription(updatedPage.getMetaDescription());
        page.setIsPublished(updatedPage.getIsPublished());
        page.setDisplayOrder(updatedPage.getDisplayOrder());

        return pageRepository.save(page);
    }

    /**
     * 페이지 삭제
     */
    @Transactional
    public void deletePage(Long id) {
        if (!pageRepository.existsById(id)) {
            throw new IllegalArgumentException("페이지를 찾을 수 없습니다: " + id);
        }
        pageRepository.deleteById(id);
    }

    /**
     * Slug 중복 체크
     */
    private void validateSlugUniqueness(String slug) {
        if (pageRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("이미 존재하는 Slug입니다: " + slug);
        }
    }
}
