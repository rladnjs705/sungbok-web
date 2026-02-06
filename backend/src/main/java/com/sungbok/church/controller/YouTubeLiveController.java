package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.dto.request.YouTubeLiveRequest;
import com.sungbok.church.dto.response.YouTubeLiveResponse;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.service.YouTubeLiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * YouTubeLive Controller
 * 유튜브 라이브 방송 관리 REST API
 */
@Tag(name = "유튜브 라이브", description = "유튜브 라이브 방송 관리 API")
@RestController
@RequestMapping("/api/youtube-lives")
@RequiredArgsConstructor
public class YouTubeLiveController {

    private final YouTubeLiveService youtubeLiveService;

    /**
     * 라이브 상태별 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<YouTubeLiveResponse>> getLivesByStatus(@PathVariable LiveStatus status) {
        List<YouTubeLiveResponse> lives = youtubeLiveService.getLivesByStatus(status).stream()
            .map(YouTubeLiveResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lives);
    }

    /**
     * 현재 라이브 중인 방송 조회
     */
    @GetMapping("/current")
    public ResponseEntity<List<YouTubeLiveResponse>> getCurrentLiveStreams() {
        List<YouTubeLiveResponse> lives = youtubeLiveService.getCurrentLiveStreams().stream()
            .map(YouTubeLiveResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lives);
    }

    /**
     * 예정된 라이브 조회
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<YouTubeLiveResponse>> getUpcomingLiveStreams() {
        List<YouTubeLiveResponse> lives = youtubeLiveService.getUpcomingLiveStreams().stream()
            .map(YouTubeLiveResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lives);
    }

    /**
     * YouTube Video ID로 조회
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<YouTubeLiveResponse> getLiveByVideoId(@PathVariable String videoId) {
        YouTubeLive live = youtubeLiveService.getLiveByVideoId(videoId)
            .orElseThrow(() -> new ResourceNotFoundException("라이브를 찾을 수 없습니다: " + videoId));
        return ResponseEntity.ok(YouTubeLiveResponse.from(live));
    }

    /**
     * 종료된 라이브 조회
     */
    @GetMapping("/completed")
    public ResponseEntity<List<YouTubeLiveResponse>> getCompletedLivesSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        List<YouTubeLiveResponse> lives = youtubeLiveService.getCompletedLivesSince(since).stream()
            .map(YouTubeLiveResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lives);
    }

    /**
     * 최신 라이브 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<YouTubeLiveResponse>> getLatestLives() {
        List<YouTubeLiveResponse> lives = youtubeLiveService.getLatestLives().stream()
            .map(YouTubeLiveResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lives);
    }

    /**
     * 라이브 생성
     */
    @PostMapping
    public ResponseEntity<YouTubeLiveResponse> createLive(@Valid @RequestBody YouTubeLiveRequest request) {
        YouTubeLive live = toEntity(request);
        YouTubeLive created = youtubeLiveService.createLive(live);
        return ResponseEntity.status(HttpStatus.CREATED).body(YouTubeLiveResponse.from(created));
    }

    /**
     * 라이브 상태 업데이트
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<YouTubeLiveResponse> updateLiveStatus(
            @PathVariable Long id,
            @RequestParam LiveStatus status) {
        YouTubeLive updated = youtubeLiveService.updateLiveStatus(id, status);
        return ResponseEntity.ok(YouTubeLiveResponse.from(updated));
    }

    /**
     * 라이브 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<YouTubeLiveResponse> updateLive(
            @PathVariable Long id,
            @Valid @RequestBody YouTubeLiveRequest request) {
        YouTubeLive live = toEntity(request);
        YouTubeLive updated = youtubeLiveService.updateLive(id, live);
        return ResponseEntity.ok(YouTubeLiveResponse.from(updated));
    }

    /**
     * 라이브 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLive(@PathVariable Long id) {
        youtubeLiveService.deleteLive(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 오래된 라이브 데이터 정리
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldLives(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        youtubeLiveService.cleanupOldLives(before);
        return ResponseEntity.ok().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private YouTubeLive toEntity(YouTubeLiveRequest request) {
        YouTubeLive.YouTubeLiveBuilder builder = YouTubeLive.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .youtubeVideoId(request.getYoutubeVideoId())
            .scheduledStartTime(request.getScheduledStartTime())
            .thumbnailUrl(request.getThumbnailUrl())
            .status(request.getStatus());

        // Worship 관계 설정은 Service Layer에서 처리하거나
        // 필요시 Repository를 통해 조회
        // 여기서는 worshipId를 포함하지 않음 (Service에서 처리)

        return builder.build();
    }
}
