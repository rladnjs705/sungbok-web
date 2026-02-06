package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.dto.request.WorshipRequest;
import com.sungbok.church.dto.response.WorshipResponse;
import com.sungbok.church.service.WorshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Worship Controller
 * 예배 정보 관리 REST API
 */
@Tag(name = "예배", description = "예배 일정 관리 API")
@RestController
@RequestMapping("/api/worships")
@RequiredArgsConstructor
public class WorshipController {

    private final WorshipService worshipService;

    /**
     * 활성화된 예배 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<WorshipResponse>> getActiveWorships() {
        List<WorshipResponse> worships = worshipService.getActiveWorships().stream()
            .map(WorshipResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(worships);
    }

    /**
     * 예배 유형별 조회
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<WorshipResponse>> getWorshipsByType(@PathVariable WorshipType type) {
        List<WorshipResponse> worships = worshipService.getWorshipsByType(type).stream()
            .map(WorshipResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(worships);
    }

    /**
     * 요일별 예배 조회
     */
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<WorshipResponse>> getWorshipsByDayOfWeek(@PathVariable DayOfWeek dayOfWeek) {
        List<WorshipResponse> worships = worshipService.getWorshipsByDayOfWeek(dayOfWeek).stream()
            .map(WorshipResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(worships);
    }

    /**
     * 현재 라이브 중인 예배 조회
     */
    @GetMapping("/live")
    public ResponseEntity<WorshipResponse> getLiveWorship() {
        return worshipService.getLiveWorship()
            .map(worship -> ResponseEntity.ok(WorshipResponse.from(worship)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 라이브 스트리밍 예배 목록
     */
    @GetMapping("/live-stream")
    public ResponseEntity<List<WorshipResponse>> getLiveStreamWorships() {
        List<WorshipResponse> worships = worshipService.getLiveStreamWorships().stream()
            .map(WorshipResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(worships);
    }

    /**
     * 예배 생성
     */
    @PostMapping
    public ResponseEntity<WorshipResponse> createWorship(@Valid @RequestBody WorshipRequest request) {
        Worship worship = toEntity(request);
        Worship created = worshipService.createWorship(worship);
        return ResponseEntity.status(HttpStatus.CREATED).body(WorshipResponse.from(created));
    }

    /**
     * 예배 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorshipResponse> updateWorship(
            @PathVariable Long id,
            @Valid @RequestBody WorshipRequest request) {
        Worship worship = toEntity(request);
        Worship updated = worshipService.updateWorship(id, worship);
        return ResponseEntity.ok(WorshipResponse.from(updated));
    }

    /**
     * 라이브 상태 토글
     */
    @PatchMapping("/{id}/live")
    public ResponseEntity<Void> toggleLiveStatus(
            @PathVariable Long id,
            @RequestParam boolean isLive) {
        worshipService.toggleLiveStatus(id, isLive);
        return ResponseEntity.ok().build();
    }

    /**
     * 예배 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorship(@PathVariable Long id) {
        worshipService.deleteWorship(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Worship toEntity(WorshipRequest request) {
        return Worship.builder()
            .title(request.getTitle())
            .type(request.getType())
            .dayOfWeek(request.getDayOfWeek())
            .startTime(request.getStartTime())
            .location(request.getLocation())
            .description(request.getDescription())
            .liveStreamUrl(request.getLiveStreamUrl())
            .isActive(request.getIsActive())
            .build();
    }
}
