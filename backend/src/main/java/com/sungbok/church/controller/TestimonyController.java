package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Testimony;
import com.sungbok.church.dto.request.TestimonyRequest;
import com.sungbok.church.dto.response.TestimonyResponse;
import com.sungbok.church.service.TestimonyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Testimony Controller
 * 간증 관리 REST API
 */
@Tag(name = "간증", description = "간증 관리 API")
@RestController
@RequestMapping("/api/testimonies")
@RequiredArgsConstructor
public class TestimonyController {

    private final TestimonyService testimonyService;

    /**
     * 승인된 간증 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<TestimonyResponse>> getApprovedTestimonies(
            @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.getApprovedTestimonies(pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 카테고리별 승인된 간증 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<TestimonyResponse>> getTestimoniesByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.getTestimoniesByCategory(category, pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 간증 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestimonyResponse> getTestimonyById(@PathVariable Long id) {
        Testimony testimony = testimonyService.getTestimonyById(id);
        return ResponseEntity.ok(TestimonyResponse.from(testimony));
    }

    /**
     * 작성자별 간증 조회
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<Page<TestimonyResponse>> getTestimoniesByAuthor(
            @PathVariable String author,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.getTestimoniesByAuthor(author, pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 승인 대기 중인 간증 목록
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<TestimonyResponse>> getPendingTestimonies(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.getPendingTestimonies(pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 최신 간증 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<TestimonyResponse>> getLatestTestimonies() {
        List<TestimonyResponse> testimonies = testimonyService.getLatestTestimonies().stream()
            .map(TestimonyResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<TestimonyResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.searchByTitle(keyword, pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 제목 또는 내용으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<TestimonyResponse>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TestimonyResponse> testimonies = testimonyService.searchByKeyword(keyword, pageable)
            .map(TestimonyResponse::from);
        return ResponseEntity.ok(testimonies);
    }

    /**
     * 간증 생성
     */
    @PostMapping
    public ResponseEntity<TestimonyResponse> createTestimony(@Valid @RequestBody TestimonyRequest request) {
        Testimony testimony = toEntity(request);
        Testimony created = testimonyService.createTestimony(testimony);
        return ResponseEntity.status(HttpStatus.CREATED).body(TestimonyResponse.from(created));
    }

    /**
     * 간증 승인
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<TestimonyResponse> approveTestimony(@PathVariable Long id) {
        Testimony approved = testimonyService.approveTestimony(id);
        return ResponseEntity.ok(TestimonyResponse.from(approved));
    }

    /**
     * 간증 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestimonyResponse> updateTestimony(
            @PathVariable Long id,
            @Valid @RequestBody TestimonyRequest request) {
        Testimony testimony = toEntity(request);
        Testimony updated = testimonyService.updateTestimony(id, testimony);
        return ResponseEntity.ok(TestimonyResponse.from(updated));
    }

    /**
     * 간증 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestimony(@PathVariable Long id) {
        testimonyService.deleteTestimony(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 승인된 간증 개수
     */
    @GetMapping("/stats/approved")
    public ResponseEntity<Long> getApprovedTestimonyCount() {
        return ResponseEntity.ok(testimonyService.getApprovedTestimonyCount());
    }

    /**
     * 통계: 승인 대기 중인 간증 개수
     */
    @GetMapping("/stats/pending")
    public ResponseEntity<Long> getPendingTestimonyCount() {
        return ResponseEntity.ok(testimonyService.getPendingTestimonyCount());
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Testimony toEntity(TestimonyRequest request) {
        return Testimony.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .author(request.getAuthor())
            .category(request.getCategory())
            .build();
    }
}
