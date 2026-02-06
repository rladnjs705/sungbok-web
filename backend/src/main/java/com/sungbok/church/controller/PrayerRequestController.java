package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.enums.PrayerStatus;
import com.sungbok.church.dto.request.PrayerRequestRequest;
import com.sungbok.church.dto.response.PrayerRequestResponse;
import com.sungbok.church.service.PrayerRequestService;
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
 * PrayerRequest Controller
 * 기도요청 관리 REST API
 */
@Tag(name = "기도제목", description = "기도제목 관리 API")
@RestController
@RequestMapping("/api/prayer-requests")
@RequiredArgsConstructor
public class PrayerRequestController {

    private final PrayerRequestService prayerRequestService;

    /**
     * 승인된 기도요청 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<PrayerRequestResponse>> getApprovedPrayerRequests(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PrayerRequestResponse> requests = prayerRequestService.getApprovedPrayerRequests(pageable)
            .map(PrayerRequestResponse::from);
        return ResponseEntity.ok(requests);
    }

    /**
     * 상태별 승인된 기도요청 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PrayerRequestResponse>> getPrayerRequestsByStatus(
            @PathVariable PrayerStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PrayerRequestResponse> requests = prayerRequestService.getPrayerRequestsByStatus(status, pageable)
            .map(PrayerRequestResponse::from);
        return ResponseEntity.ok(requests);
    }

    /**
     * 기도 중인 요청 목록
     */
    @GetMapping("/praying")
    public ResponseEntity<List<PrayerRequestResponse>> getPrayingRequests() {
        List<PrayerRequestResponse> requests = prayerRequestService.getPrayingRequests().stream()
            .map(PrayerRequestResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    /**
     * 응답받은 기도요청 목록
     */
    @GetMapping("/answered")
    public ResponseEntity<Page<PrayerRequestResponse>> getAnsweredPrayerRequests(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PrayerRequestResponse> requests = prayerRequestService.getAnsweredPrayerRequests(pageable)
            .map(PrayerRequestResponse::from);
        return ResponseEntity.ok(requests);
    }

    /**
     * 승인 대기 중인 기도요청 목록
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<PrayerRequestResponse>> getPendingPrayerRequests(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PrayerRequestResponse> requests = prayerRequestService.getPendingPrayerRequests(pageable)
            .map(PrayerRequestResponse::from);
        return ResponseEntity.ok(requests);
    }

    /**
     * 기도요청 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrayerRequestResponse> getPrayerRequestById(@PathVariable Long id) {
        PrayerRequest request = prayerRequestService.getPrayerRequestById(id);
        return ResponseEntity.ok(PrayerRequestResponse.from(request));
    }

    /**
     * 최신 기도요청 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<PrayerRequestResponse>> getLatestPrayerRequests() {
        List<PrayerRequestResponse> requests = prayerRequestService.getLatestPrayerRequests().stream()
            .map(PrayerRequestResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PrayerRequestResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PrayerRequestResponse> requests = prayerRequestService.searchByTitle(keyword, pageable)
            .map(PrayerRequestResponse::from);
        return ResponseEntity.ok(requests);
    }

    /**
     * 기도요청 생성
     */
    @PostMapping
    public ResponseEntity<PrayerRequestResponse> createPrayerRequest(
            @Valid @RequestBody PrayerRequestRequest request) {
        PrayerRequest prayerRequest = toEntity(request);
        PrayerRequest created = prayerRequestService.createPrayerRequest(prayerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(PrayerRequestResponse.from(created));
    }

    /**
     * 기도요청 승인
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<PrayerRequestResponse> approvePrayerRequest(@PathVariable Long id) {
        PrayerRequest approved = prayerRequestService.approvePrayerRequest(id);
        return ResponseEntity.ok(PrayerRequestResponse.from(approved));
    }

    /**
     * 기도수 증가
     */
    @PatchMapping("/{id}/pray")
    public ResponseEntity<Void> incrementPrayerCount(@PathVariable Long id) {
        prayerRequestService.incrementPrayerCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 기도 응답으로 상태 변경
     */
    @PatchMapping("/{id}/answer")
    public ResponseEntity<PrayerRequestResponse> markAsAnswered(@PathVariable Long id) {
        PrayerRequest answered = prayerRequestService.markAsAnswered(id);
        return ResponseEntity.ok(PrayerRequestResponse.from(answered));
    }

    /**
     * 기도요청 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<PrayerRequestResponse> updatePrayerRequest(
            @PathVariable Long id,
            @Valid @RequestBody PrayerRequestRequest request) {
        PrayerRequest prayerRequest = toEntity(request);
        PrayerRequest updated = prayerRequestService.updatePrayerRequest(id, prayerRequest);
        return ResponseEntity.ok(PrayerRequestResponse.from(updated));
    }

    /**
     * 기도요청 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrayerRequest(@PathVariable Long id) {
        prayerRequestService.deletePrayerRequest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 상태별 기도요청 개수
     */
    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> getPrayerRequestCountByStatus(@PathVariable PrayerStatus status) {
        return ResponseEntity.ok(prayerRequestService.getPrayerRequestCountByStatus(status));
    }

    /**
     * 통계: 승인 대기 중인 기도요청 개수
     */
    @GetMapping("/stats/pending")
    public ResponseEntity<Long> getPendingPrayerRequestCount() {
        return ResponseEntity.ok(prayerRequestService.getPendingPrayerRequestCount());
    }

    /**
     * Request DTO -> Entity 변환
     */
    private PrayerRequest toEntity(PrayerRequestRequest request) {
        return PrayerRequest.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .requester(request.getRequester())
            .isAnonymous(request.getIsAnonymous())
            .build();
    }
}
