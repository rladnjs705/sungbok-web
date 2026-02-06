package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Page;
import com.sungbok.church.dto.request.PageRequest;
import com.sungbok.church.dto.response.PageResponse;
import com.sungbok.church.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Controller
 * 정적 페이지 관리 REST API
 */
@Tag(name = "페이지", description = "커스텀 페이지 관리 API")
@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    /**
     * 공개된 페이지 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<PageResponse>> getPublishedPages() {
        List<PageResponse> pages = pageService.getPublishedPages().stream()
            .map(PageResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pages);
    }

    /**
     * Slug로 페이지 조회
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PageResponse> getPageBySlug(@PathVariable String slug) {
        Page page = pageService.getPageBySlug(slug);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    /**
     * 페이지 생성
     */
    @PostMapping
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageRequest request) {
        Page page = toEntity(request);
        Page created = pageService.createPage(page);
        return ResponseEntity.status(HttpStatus.CREATED).body(PageResponse.from(created));
    }

    /**
     * 페이지 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<PageResponse> updatePage(
            @PathVariable Long id,
            @Valid @RequestBody PageRequest request) {
        Page page = toEntity(request);
        Page updated = pageService.updatePage(id, page);
        return ResponseEntity.ok(PageResponse.from(updated));
    }

    /**
     * 페이지 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        pageService.deletePage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Page toEntity(PageRequest request) {
        return Page.builder()
            .slug(request.getSlug())
            .title(request.getTitle())
            .content(request.getContent())
            .metaDescription(request.getMetaDescription())
            .isPublished(request.getIsPublished())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
