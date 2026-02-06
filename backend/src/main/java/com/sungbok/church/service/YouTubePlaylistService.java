package com.sungbok.church.service;

import com.sungbok.church.domain.entity.YouTubePlaylist;
import com.sungbok.church.domain.repository.YouTubePlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTubePlaylist Service
 * 유튜브 재생목록 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YouTubePlaylistService {

    private final YouTubePlaylistRepository youtubePlaylistRepository;

    /**
     * 활성화된 재생목록 조회
     */
    public List<YouTubePlaylist> getActivePlaylists() {
        return youtubePlaylistRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * 자동 동기화 설정된 재생목록 조회
     */
    public List<YouTubePlaylist> getAutoSyncPlaylists() {
        return youtubePlaylistRepository.findByAutoSyncTrueAndIsActiveTrue();
    }

    /**
     * YouTube Playlist ID로 조회
     */
    public YouTubePlaylist getPlaylistByYoutubeId(String youtubePlaylistId) {
        return youtubePlaylistRepository.findByPlaylistId(youtubePlaylistId)
            .orElseThrow(() -> new IllegalArgumentException(
                "재생목록을 찾을 수 없습니다: " + youtubePlaylistId
            ));
    }

    /**
     * 재생목록 ID로 조회
     */
    public YouTubePlaylist getPlaylistById(Long id) {
        return youtubePlaylistRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("재생목록을 찾을 수 없습니다: " + id));
    }

    /**
     * 재생목록 생성
     */
    @Transactional
    public YouTubePlaylist createPlaylist(YouTubePlaylist playlist) {
        validatePlaylistIdUniqueness(playlist.getPlaylistId());
        return youtubePlaylistRepository.save(playlist);
    }

    /**
     * 재생목록 동기화
     */
    @Transactional
    public YouTubePlaylist syncPlaylist(Long id) {
        YouTubePlaylist playlist = getPlaylistById(id);

        // TODO: YouTube API를 통한 실제 동기화 로직 구현
        playlist.setLastSyncedAt(LocalDateTime.now());

        return youtubePlaylistRepository.save(playlist);
    }

    /**
     * 재생목록 수정
     */
    @Transactional
    public YouTubePlaylist updatePlaylist(Long id, YouTubePlaylist updatedPlaylist) {
        YouTubePlaylist playlist = getPlaylistById(id);

        playlist.setTitle(updatedPlaylist.getTitle());
        playlist.setDescription(updatedPlaylist.getDescription());
        playlist.setAutoSync(updatedPlaylist.getAutoSync());
        playlist.setIsActive(updatedPlaylist.getIsActive());
        playlist.setDisplayOrder(updatedPlaylist.getDisplayOrder());

        return youtubePlaylistRepository.save(playlist);
    }

    /**
     * 재생목록 삭제
     */
    @Transactional
    public void deletePlaylist(Long id) {
        if (!youtubePlaylistRepository.existsById(id)) {
            throw new IllegalArgumentException("재생목록을 찾을 수 없습니다: " + id);
        }
        youtubePlaylistRepository.deleteById(id);
    }

    /**
     * Playlist ID 중복 체크
     */
    private void validatePlaylistIdUniqueness(String youtubePlaylistId) {
        if (youtubePlaylistRepository.existsByPlaylistId(youtubePlaylistId)) {
            throw new IllegalArgumentException(
                "이미 존재하는 재생목록 ID입니다: " + youtubePlaylistId
            );
        }
    }
}
