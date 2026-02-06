package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.YouTubePlaylist;
import com.sungbok.church.dto.request.YouTubePlaylistRequest;
import com.sungbok.church.dto.response.YouTubePlaylistResponse;
import com.sungbok.church.service.YouTubePlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * YouTubePlaylist Controller
 * 유튜브 재생목록 관리 REST API
 */
@Tag(name = "유튜브 재생목록", description = "유튜브 재생목록 관리 API")
@RestController
@RequestMapping("/api/youtube-playlists")
@RequiredArgsConstructor
public class YouTubePlaylistController {

    private final YouTubePlaylistService youtubePlaylistService;

    /**
     * 활성화된 재생목록 조회
     */
    @GetMapping
    public ResponseEntity<List<YouTubePlaylistResponse>> getActivePlaylists() {
        List<YouTubePlaylistResponse> playlists = youtubePlaylistService.getActivePlaylists().stream()
            .map(YouTubePlaylistResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(playlists);
    }

    /**
     * 자동 동기화 설정된 재생목록 조회
     */
    @GetMapping("/auto-sync")
    public ResponseEntity<List<YouTubePlaylistResponse>> getAutoSyncPlaylists() {
        List<YouTubePlaylistResponse> playlists = youtubePlaylistService.getAutoSyncPlaylists().stream()
            .map(YouTubePlaylistResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(playlists);
    }

    /**
     * YouTube Playlist ID로 조회
     */
    @GetMapping("/youtube/{youtubePlaylistId}")
    public ResponseEntity<YouTubePlaylistResponse> getPlaylistByYoutubeId(
            @PathVariable String youtubePlaylistId) {
        YouTubePlaylist playlist = youtubePlaylistService.getPlaylistByYoutubeId(youtubePlaylistId);
        return ResponseEntity.ok(YouTubePlaylistResponse.from(playlist));
    }

    /**
     * 재생목록 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<YouTubePlaylistResponse> getPlaylistById(@PathVariable Long id) {
        YouTubePlaylist playlist = youtubePlaylistService.getPlaylistById(id);
        return ResponseEntity.ok(YouTubePlaylistResponse.from(playlist));
    }

    /**
     * 재생목록 생성
     */
    @PostMapping
    public ResponseEntity<YouTubePlaylistResponse> createPlaylist(
            @Valid @RequestBody YouTubePlaylistRequest request) {
        YouTubePlaylist playlist = toEntity(request);
        YouTubePlaylist created = youtubePlaylistService.createPlaylist(playlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(YouTubePlaylistResponse.from(created));
    }

    /**
     * 재생목록 동기화
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<YouTubePlaylistResponse> syncPlaylist(@PathVariable Long id) {
        YouTubePlaylist synced = youtubePlaylistService.syncPlaylist(id);
        return ResponseEntity.ok(YouTubePlaylistResponse.from(synced));
    }

    /**
     * 재생목록 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<YouTubePlaylistResponse> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody YouTubePlaylistRequest request) {
        YouTubePlaylist playlist = toEntity(request);
        YouTubePlaylist updated = youtubePlaylistService.updatePlaylist(id, playlist);
        return ResponseEntity.ok(YouTubePlaylistResponse.from(updated));
    }

    /**
     * 재생목록 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        youtubePlaylistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private YouTubePlaylist toEntity(YouTubePlaylistRequest request) {
        return YouTubePlaylist.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .playlistId(request.getPlaylistId())
            .thumbnailUrl(request.getThumbnailUrl())
            .autoSync(request.getAutoSync())
            .isActive(request.getIsActive())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
