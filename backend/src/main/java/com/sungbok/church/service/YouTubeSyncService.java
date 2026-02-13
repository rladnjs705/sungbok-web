package com.sungbok.church.service;

import com.sungbok.church.client.YouTubeApiClient;
import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.entity.VideoGallery;
import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.entity.YouTubePlaylist;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.repository.SermonRepository;
import com.sungbok.church.domain.repository.VideoGalleryRepository;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import com.sungbok.church.domain.repository.YouTubePlaylistRepository;
import com.sungbok.church.dto.youtube.YouTubePlaylistItemDto;
import com.sungbok.church.dto.youtube.YouTubeVideoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YouTube Sync Service
 * YouTube API와 데이터베이스 동기화 비즈니스 로직
 *
 * 주요 기능:
 * 1. 최신 영상 동기화 (Sermon 테이블에 저장)
 * 2. 라이브 상태 업데이트 (YouTubeLive 테이블)
 * 3. 재생목록 동기화
 * 4. 제목에서 날짜/설교자 파싱
 * 5. 중복 체크 (youtubeVideoId 기반)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeSyncService {

    private final YouTubeApiClient youTubeApiClient;
    private final SermonRepository sermonRepository;
    private final YouTubeLiveRepository youtubeLiveRepository;
    private final VideoGalleryRepository videoGalleryRepository;
    private final YouTubePlaylistRepository youtubePlaylistRepository;

    // 날짜 패턴: 2024.02.03, 2024-02-03, 2024/02/03, 24.2.3 등
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "(\\d{4}|\\d{2})[.\\-/](\\d{1,2})[.\\-/](\\d{1,2})"
    );

    // 설교자 패턴: "김철수 목사", "박영희목사님", "이목사 설교" 등
    private static final Pattern PREACHER_PATTERN = Pattern.compile(
        "([가-힣]{2,4})\\s*(목사|전도사|강도사|장로|권사|집사|선교사|사모)(님)?",
        Pattern.CASE_INSENSITIVE
    );

    // ISO 8601 Duration 패턴: PT1H30M15S
    private static final Pattern DURATION_PATTERN = Pattern.compile(
        "PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?"
    );

    /**
     * 1. 최신 영상 동기화
     * YouTube에서 최신 영상을 가져와 Sermon 테이블에 저장
     *
     * @param maxResults 가져올 최대 영상 수 (기본: 10)
     * @return 동기화된 설교 개수
     */
    @Transactional
    public int syncLatestVideos(Integer maxResults) {
        log.info("Starting video sync with maxResults: {}", maxResults);

        try {
            List<YouTubeVideoDto> videos = youTubeApiClient.fetchLatestVideos(maxResults != null ? maxResults : 10);
            int syncedCount = 0;

            for (YouTubeVideoDto video : videos) {
                try {
                    // 라이브 방송 중이거나 예정된 영상은 스킵
                    if (video.isLive() || video.isUpcoming()) {
                        log.debug("Skipping live/upcoming video: {}", video.getId());
                        continue;
                    }

                    // 중복 체크
                    if (sermonRepository.findByYoutubeVideoId(video.getId()).isPresent()) {
                        log.debug("Video already exists: {}", video.getId());
                        continue;
                    }

                    // Sermon 엔티티 생성 및 저장
                    Sermon sermon = createSermonFromVideo(video);
                    sermonRepository.save(sermon);
                    syncedCount++;

                    log.info("Synced sermon: {} ({})", sermon.getTitle(), sermon.getYoutubeVideoId());

                } catch (Exception e) {
                    log.error("Error syncing video: {}", video.getId(), e);
                    // 개별 영상 동기화 실패 시 계속 진행
                }
            }

            log.info("Video sync completed: {} sermons synced", syncedCount);
            return syncedCount;

        } catch (Exception e) {
            log.error("Fatal error during video sync: {}", e.getMessage());
            // 예외를 던지지 않고 0 반환 - 서비스 계속 진행
            return 0;
        }
    }

    /**
     * 2. 라이브 상태 업데이트
     * YouTube에서 현재 라이브 방송을 조회하여 YouTubeLive 테이블 업데이트
     *
     * @return 업데이트된 라이브 개수
     */
    @Transactional
    public int updateLiveStatus() {
        log.info("Starting live status update");

        try {
            List<YouTubeVideoDto> liveVideos = youTubeApiClient.fetchLiveStreams();
            int updatedCount = 0;

            // 1. YouTube에서 가져온 라이브 영상 처리
            for (YouTubeVideoDto video : liveVideos) {
                try {
                    Optional<YouTubeLive> existingLive = youtubeLiveRepository
                        .findByYoutubeVideoId(video.getId());

                    if (existingLive.isPresent()) {
                        // 기존 라이브 업데이트
                        YouTubeLive live = existingLive.get();
                        updateLiveFromVideo(live, video);
                        youtubeLiveRepository.save(live);
                        updatedCount++;
                        log.info("Updated live: {} (status: {})", live.getTitle(), live.getStatus());
                    } else {
                        // 새 라이브 생성
                        YouTubeLive newLive = createLiveFromVideo(video);
                        youtubeLiveRepository.save(newLive);
                        updatedCount++;
                        log.info("Created new live: {} (status: {})", newLive.getTitle(), newLive.getStatus());
                    }

                } catch (Exception e) {
                    log.error("Error updating live: {}", video.getId(), e);
                }
            }

            // 2. 종료된 라이브 처리 (DB에는 LIVE인데 YouTube에 없는 경우)
            List<YouTubeLive> activeLives = youtubeLiveRepository.findByStatus(LiveStatus.LIVE);
            List<String> currentLiveIds = liveVideos.stream()
                .map(YouTubeVideoDto::getId)
                .toList();

            for (YouTubeLive live : activeLives) {
                if (!currentLiveIds.contains(live.getYoutubeVideoId())) {
                    live.setStatus(LiveStatus.ENDED);
                    live.setEndTime(LocalDateTime.now());
                    youtubeLiveRepository.save(live);
                    updatedCount++;
                    log.info("Marked live as ended: {}", live.getTitle());
                }
            }

            log.info("Live status update completed: {} lives updated", updatedCount);
            return updatedCount;

        } catch (Exception e) {
            log.error("Fatal error during live status update: {}", e.getMessage());
            // 예외를 던지지 않고 0 반환 - 서비스 계속 진행
            return 0;
        }
    }

    /**
     * 3. 재생목록 동기화
     * YouTube 재생목록을 VideoGallery 테이블에 동기화
     *
     * @param playlistId YouTube Playlist ID
     * @return 동기화된 영상 개수
     */
    @Transactional
    public int syncPlaylist(String playlistId, String category) {
        log.info("Starting playlist sync: {} (category: {})", playlistId, category);

        try {
            List<YouTubePlaylistItemDto> items = youTubeApiClient.fetchPlaylistItems(playlistId, 50);
            int syncedCount = 0;

            for (YouTubePlaylistItemDto item : items) {
                try {
                    String videoId = item.getVideoId();
                    if (videoId == null) {
                        continue;
                    }

                    // 중복 체크
                    if (videoGalleryRepository.findByYoutubeVideoId(videoId).isPresent()) {
                        log.debug("Video already exists in gallery: {}", videoId);
                        continue;
                    }

                    // VideoGallery 엔티티 생성 및 저장
                    VideoGallery video = createVideoGalleryFromPlaylistItem(item, category);
                    videoGalleryRepository.save(video);
                    syncedCount++;

                    log.info("Synced video to gallery: {} ({})", video.getTitle(), video.getYoutubeVideoId());

                } catch (Exception e) {
                    log.error("Error syncing playlist item: {}", item.getId(), e);
                }
            }

            log.info("Playlist sync completed: {} videos synced", syncedCount);
            return syncedCount;

        } catch (Exception e) {
            log.error("Fatal error during playlist sync: {}", e.getMessage());
            // 예외를 던지지 않고 0 반환 - 서비스 계속 진행
            return 0;
        }
    }

    /**
     * 4. 제목에서 날짜 파싱
     * 예: "2024.02.03 주일예배 설교" → LocalDate.of(2024, 2, 3)
     *
     * @param title YouTube 영상 제목
     * @return 파싱된 날짜 (실패 시 현재 날짜)
     */
    public LocalDate parseDateFromTitle(String title) {
        if (title == null || title.isEmpty()) {
            return LocalDate.now();
        }

        try {
            Matcher matcher = DATE_PATTERN.matcher(title);
            if (matcher.find()) {
                String yearStr = matcher.group(1);
                int year = yearStr.length() == 2 ? 2000 + Integer.parseInt(yearStr) : Integer.parseInt(yearStr);
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));

                LocalDate date = LocalDate.of(year, month, day);
                log.debug("Parsed date from title '{}': {}", title, date);
                return date;
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.warn("Failed to parse date from title: {}", title, e);
        }

        return LocalDate.now();
    }

    /**
     * 5. 제목에서 설교자 파싱
     * 예: "김철수 목사님 설교" → "김철수 목사"
     *
     * @param title YouTube 영상 제목
     * @return 파싱된 설교자 이름 (실패 시 "담임목사")
     */
    public String parsePreacherFromTitle(String title) {
        if (title == null || title.isEmpty()) {
            return "담임목사";
        }

        try {
            Matcher matcher = PREACHER_PATTERN.matcher(title);
            if (matcher.find()) {
                String name = matcher.group(1);
                String position = matcher.group(2);
                String preacher = name + " " + position;
                log.debug("Parsed preacher from title '{}': {}", title, preacher);
                return preacher;
            }
        } catch (Exception e) {
            log.warn("Failed to parse preacher from title: {}", title, e);
        }

        return "담임목사";
    }

    // ===== Private Helper Methods =====

    /**
     * YouTube 영상 → Sermon 엔티티 변환
     */
    private Sermon createSermonFromVideo(YouTubeVideoDto video) {
        String title = video.getSnippet().getTitle();
        String description = video.getSnippet().getDescription();

        return Sermon.builder()
            .title(title)
            .preacher(parsePreacherFromTitle(title))
            .sermonDate(parseDateFromTitle(title))
            .youtubeVideoId(video.getId())
            .videoUrl("https://www.youtube.com/watch?v=" + video.getId())
            .thumbnailUrl(video.getThumbnailUrl())
            .duration(parseDuration(video.getContentDetails().getDuration()))
            .description(description)
            .viewCount(0)
            .isPublished(true)  // 기본 공개
            .isFeatured(false)
            .build();
    }

    /**
     * YouTube 영상 → YouTubeLive 엔티티 변환
     */
    private YouTubeLive createLiveFromVideo(YouTubeVideoDto video) {
        YouTubeLive live = new YouTubeLive();
        live.setYoutubeVideoId(video.getId());
        live.setTitle(video.getSnippet().getTitle());

        // 라이브 상태 설정
        if (video.isLive()) {
            live.setStatus(LiveStatus.LIVE);
            if (video.getLiveStreamingDetails() != null) {
                live.setActualStartTime(parseDateTime(video.getLiveStreamingDetails().getActualStartTime()));
                live.setScheduledStartTime(parseDateTime(video.getLiveStreamingDetails().getScheduledStartTime()));

                // 동시 시청자 수
                String viewers = video.getLiveStreamingDetails().getConcurrentViewers();
                if (viewers != null) {
                    try {
                        live.setViewerCount(Integer.parseInt(viewers));
                    } catch (NumberFormatException e) {
                        live.setViewerCount(0);
                    }
                }
            } else {
                live.setActualStartTime(LocalDateTime.now());
            }
        } else if (video.isUpcoming()) {
            live.setStatus(LiveStatus.SCHEDULED);
            if (video.getLiveStreamingDetails() != null) {
                live.setScheduledStartTime(parseDateTime(video.getLiveStreamingDetails().getScheduledStartTime()));
            }
        }

        return live;
    }

    /**
     * 기존 YouTubeLive 엔티티 업데이트
     */
    private void updateLiveFromVideo(YouTubeLive live, YouTubeVideoDto video) {
        live.setTitle(video.getSnippet().getTitle());

        if (video.isLive()) {
            live.setStatus(LiveStatus.LIVE);
            if (video.getLiveStreamingDetails() != null) {
                if (live.getActualStartTime() == null) {
                    live.setActualStartTime(parseDateTime(video.getLiveStreamingDetails().getActualStartTime()));
                }

                // 동시 시청자 수 업데이트
                String viewers = video.getLiveStreamingDetails().getConcurrentViewers();
                if (viewers != null) {
                    try {
                        live.setViewerCount(Integer.parseInt(viewers));
                    } catch (NumberFormatException e) {
                        // Keep existing value
                    }
                }
            }
        } else if (video.isUpcoming()) {
            live.setStatus(LiveStatus.SCHEDULED);
            if (video.getLiveStreamingDetails() != null) {
                live.setScheduledStartTime(parseDateTime(video.getLiveStreamingDetails().getScheduledStartTime()));
            }
        } else {
            live.setStatus(LiveStatus.ENDED);
            if (video.getLiveStreamingDetails() != null) {
                live.setEndTime(parseDateTime(video.getLiveStreamingDetails().getActualEndTime()));
            }
            if (live.getEndTime() == null) {
                live.setEndTime(LocalDateTime.now());
            }
        }
    }

    /**
     * Playlist Item → VideoGallery 엔티티 변환
     */
    private VideoGallery createVideoGalleryFromPlaylistItem(YouTubePlaylistItemDto item, String category) {
        String title = item.getSnippet().getTitle();

        return VideoGallery.builder()
            .title(title)
            .description(item.getSnippet().getDescription())
            .youtubeVideoId(item.getVideoId())
            .videoUrl("https://www.youtube.com/watch?v=" + item.getVideoId())
            .thumbnailUrl(item.getThumbnailUrl())
            .eventDate(parseDateFromTitle(title))
            .category(category != null ? category : "예배영상")
            .isPublished(true)
            .build();
    }

    /**
     * ISO 8601 Duration 파싱 (PT1H30M15S → 5415 seconds)
     */
    private Integer parseDuration(String isoDuration) {
        if (isoDuration == null || isoDuration.isEmpty()) {
            return null;
        }

        try {
            Duration duration = Duration.parse(isoDuration);
            return (int) duration.getSeconds();
        } catch (Exception e) {
            log.warn("Failed to parse duration: {}", isoDuration, e);
            return null;
        }
    }

    /**
     * ISO 8601 DateTime 파싱
     */
    private LocalDateTime parseDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", isoDateTime, e);
            return null;
        }
    }

    /**
     * 카테고리 자동 분류
     * 제목 기반으로 카테고리 추론
     */
    private String classifyCategory(String title) {
        if (title == null) {
            return "기타";
        }

        String lowerTitle = title.toLowerCase();

        if (lowerTitle.contains("주일") || lowerTitle.contains("주일예배")) {
            return "주일예배";
        } else if (lowerTitle.contains("수요") || lowerTitle.contains("수요예배")) {
            return "수요예배";
        } else if (lowerTitle.contains("새벽") || lowerTitle.contains("새벽기도")) {
            return "새벽기도";
        } else if (lowerTitle.contains("특별") || lowerTitle.contains("부흥")) {
            return "특별집회";
        } else if (lowerTitle.contains("찬양") || lowerTitle.contains("워십")) {
            return "찬양";
        } else if (lowerTitle.contains("간증")) {
            return "간증";
        }

        return "기타";
    }
}
