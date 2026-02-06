# YouTube API 연동 설계

## 문서 정보

- **Feature**: YouTube API 연동
- **Phase**: Design
- **작성일**: 2026-02-03
- **상태**: In Progress

## 개요

YouTube Data API v3를 활용하여 성복교회 YouTube 채널의 영상을 자동으로 가져오고, 실시간 라이브 스트리밍을 지원하는 시스템을 구축합니다.

## 요구사항

### 1. 실시간 라이브 스트리밍
- 현재 진행 중인 라이브 방송 감지
- 라이브 상태 자동 업데이트 (1분마다)
- 시청자 수 실시간 표시
- 예배 시간에 자동으로 라이브 표시

### 2. 최신 영상 자동 동기화
- 매시간 최신 영상 가져오기
- 영상 메타데이터 자동 저장 (제목, 설명, 썸네일)
- 설교 날짜 자동 파싱
- 중복 방지

### 3. 재생목록 관리
- 재생목록별 영상 목록 가져오기
- 카테고리별 분류 (주일설교, 수요설교 등)
- 재생목록 임베드 지원

### 4. 영상 아카이브
- 과거 영상 검색
- 페이징 지원
- 필터링 (날짜, 설교자, 본문)

---

## YouTube Data API v3 개요

### API 문서
- **공식 문서**: https://developers.google.com/youtube/v3
- **할당량**: 하루 10,000 units (무료)
- **인증**: API Key 또는 OAuth 2.0

### 주요 리소스

| 리소스 | 용도 | 비용 (units) |
|--------|------|-------------|
| `channels.list` | 채널 정보 조회 | 1 |
| `search.list` | 영상 검색 | 100 |
| `videos.list` | 영상 상세 정보 | 1 |
| `playlistItems.list` | 재생목록 영상 목록 | 1 |
| `playlists.list` | 재생목록 목록 | 1 |

### 할당량 관리 전략
- 최신 영상: 매시간 동기화 (24 units/day)
- 라이브 상태: 1분마다 확인 (1,440 units/day)
- 재생목록: 6시간마다 동기화 (4 units/day)
- **총 예상 사용량**: ~1,500 units/day (여유 있음)

---

## 시스템 아키텍처

```
┌─────────────────────────────────────────────────────┐
│                   YouTube API                        │
└─────────────────────────────────────────────────────┘
                        ▲
                        │ HTTP Request
                        │
┌───────────────────────┴─────────────────────────────┐
│              Spring Boot Backend                     │
│                                                       │
│  ┌──────────────────────────────────────────────┐  │
│  │   YouTube Service (비즈니스 로직)            │  │
│  │  - fetchLatestVideos()                       │  │
│  │  - checkLiveStatus()                         │  │
│  │  - syncPlaylist()                            │  │
│  └──────────────────────────────────────────────┘  │
│                        │                             │
│  ┌──────────────────────────────────────────────┐  │
│  │   Scheduled Tasks (자동화)                   │  │
│  │  - @Scheduled(fixedRate = 60000) liveScan   │  │
│  │  - @Scheduled(cron = "0 0 * * * *") videoSync│ │
│  └──────────────────────────────────────────────┘  │
│                        │                             │
│  ┌──────────────────────────────────────────────┐  │
│  │   Repository Layer                            │  │
│  │  - SermonRepository                           │  │
│  │  - YouTubeLiveRepository                      │  │
│  │  - YouTubePlaylistRepository                  │  │
│  └──────────────────────────────────────────────┘  │
│                        │                             │
└────────────────────────┼─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                  PostgreSQL                          │
│  - sermon                                            │
│  - youtube_live                                      │
│  - youtube_playlist                                  │
└─────────────────────────────────────────────────────┘
```

---

## Backend 구현 설계

### 1. YouTube API Client

#### YouTubeApiClient.java
```java
@Component
public class YouTubeApiClient {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate;

    private static final String API_BASE_URL = "https://www.googleapis.com/youtube/v3";

    public YouTubeApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * 채널의 최신 영상 목록 가져오기
     */
    public List<VideoDto> getLatestVideos(int maxResults) {
        String url = String.format(
            "%s/search?part=snippet&channelId=%s&maxResults=%d&order=date&type=video&key=%s",
            API_BASE_URL, channelId, maxResults, apiKey
        );

        SearchResponse response = restTemplate.getForObject(url, SearchResponse.class);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        // Video IDs 추출
        List<String> videoIds = response.getItems().stream()
            .map(item -> item.getId().getVideoId())
            .collect(Collectors.toList());

        // 상세 정보 가져오기
        return getVideoDetails(videoIds);
    }

    /**
     * 영상 상세 정보 가져오기
     */
    public List<VideoDto> getVideoDetails(List<String> videoIds) {
        String ids = String.join(",", videoIds);
        String url = String.format(
            "%s/videos?part=snippet,contentDetails,statistics,liveStreamingDetails&id=%s&key=%s",
            API_BASE_URL, ids, apiKey
        );

        VideoResponse response = restTemplate.getForObject(url, VideoResponse.class);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        return response.getItems().stream()
            .map(this::mapToVideoDto)
            .collect(Collectors.toList());
    }

    /**
     * 라이브 방송 상태 확인
     */
    public List<LiveVideoDto> getLiveVideos() {
        String url = String.format(
            "%s/search?part=snippet&channelId=%s&eventType=live&type=video&key=%s",
            API_BASE_URL, channelId, apiKey
        );

        SearchResponse response = restTemplate.getForObject(url, SearchResponse.class);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        List<String> videoIds = response.getItems().stream()
            .map(item -> item.getId().getVideoId())
            .collect(Collectors.toList());

        return getLiveVideoDetails(videoIds);
    }

    /**
     * 라이브 영상 상세 정보
     */
    private List<LiveVideoDto> getLiveVideoDetails(List<String> videoIds) {
        if (videoIds.isEmpty()) {
            return Collections.emptyList();
        }

        String ids = String.join(",", videoIds);
        String url = String.format(
            "%s/videos?part=snippet,liveStreamingDetails&id=%s&key=%s",
            API_BASE_URL, ids, apiKey
        );

        VideoResponse response = restTemplate.getForObject(url, VideoResponse.class);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        return response.getItems().stream()
            .map(this::mapToLiveVideoDto)
            .collect(Collectors.toList());
    }

    /**
     * 재생목록 영상 목록 가져오기
     */
    public List<VideoDto> getPlaylistVideos(String playlistId, int maxResults) {
        String url = String.format(
            "%s/playlistItems?part=snippet&playlistId=%s&maxResults=%d&key=%s",
            API_BASE_URL, playlistId, maxResults, apiKey
        );

        PlaylistItemsResponse response = restTemplate.getForObject(url, PlaylistItemsResponse.class);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        List<String> videoIds = response.getItems().stream()
            .map(item -> item.getSnippet().getResourceId().getVideoId())
            .collect(Collectors.toList());

        return getVideoDetails(videoIds);
    }

    // DTO 매핑 메서드들...
}
```

---

### 2. YouTube Service

#### YouTubeService.java
```java
@Service
@Slf4j
public class YouTubeService {

    private final YouTubeApiClient youtubeApiClient;
    private final SermonRepository sermonRepository;
    private final YouTubeLiveRepository youtubeLiveRepository;
    private final YouTubePlaylistRepository playlistRepository;

    /**
     * 최신 영상 동기화
     */
    @Transactional
    public void syncLatestVideos() {
        log.info("Starting latest videos sync...");

        List<VideoDto> videos = youtubeApiClient.getLatestVideos(10);

        for (VideoDto video : videos) {
            // 중복 확인
            Optional<Sermon> existing = sermonRepository
                .findByYoutubeVideoId(video.getVideoId());

            if (existing.isPresent()) {
                log.debug("Video already exists: {}", video.getTitle());
                continue;
            }

            // 설교 데이터 생성
            Sermon sermon = Sermon.builder()
                .youtubeVideoId(video.getVideoId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl("https://www.youtube.com/watch?v=" + video.getVideoId())
                .thumbnailUrl(video.getThumbnailUrl())
                .duration(parseDuration(video.getDuration()))
                .sermonDate(parseSermonDate(video.getTitle(), video.getPublishedAt()))
                .preacher(parsePreacher(video.getTitle(), video.getDescription()))
                .isPublished(true)
                .viewCount(0)
                .build();

            sermonRepository.save(sermon);
            log.info("Saved new sermon: {}", sermon.getTitle());
        }
    }

    /**
     * 라이브 방송 상태 확인
     */
    @Transactional
    public void checkLiveStatus() {
        log.debug("Checking live status...");

        List<LiveVideoDto> liveVideos = youtubeApiClient.getLiveVideos();

        // 기존 라이브 종료 처리
        List<YouTubeLive> activeStreams = youtubeLiveRepository
            .findByStatus(LiveStatus.LIVE);

        for (YouTubeLive stream : activeStreams) {
            boolean stillLive = liveVideos.stream()
                .anyMatch(v -> v.getVideoId().equals(stream.getYoutubeVideoId()));

            if (!stillLive) {
                stream.setStatus(LiveStatus.ENDED);
                stream.setEndTime(LocalDateTime.now());
                log.info("Live stream ended: {}", stream.getTitle());
            }
        }

        // 새 라이브 추가
        for (LiveVideoDto liveVideo : liveVideos) {
            Optional<YouTubeLive> existing = youtubeLiveRepository
                .findByYoutubeVideoId(liveVideo.getVideoId());

            if (existing.isEmpty()) {
                YouTubeLive newLive = YouTubeLive.builder()
                    .youtubeVideoId(liveVideo.getVideoId())
                    .title(liveVideo.getTitle())
                    .scheduledStartTime(liveVideo.getScheduledStartTime())
                    .actualStartTime(liveVideo.getActualStartTime())
                    .status(LiveStatus.LIVE)
                    .viewerCount(liveVideo.getConcurrentViewers())
                    .build();

                youtubeLiveRepository.save(newLive);
                log.info("New live stream detected: {}", newLive.getTitle());
            } else {
                // 시청자 수 업데이트
                YouTubeLive live = existing.get();
                live.setViewerCount(liveVideo.getConcurrentViewers());
            }
        }

        // Worship 테이블 isLiveNow 업데이트
        updateWorshipLiveStatus(!liveVideos.isEmpty());
    }

    /**
     * 재생목록 동기화
     */
    @Transactional
    public void syncPlaylist(String playlistId) {
        log.info("Syncing playlist: {}", playlistId);

        List<VideoDto> videos = youtubeApiClient.getPlaylistVideos(playlistId, 50);

        YouTubePlaylist playlist = playlistRepository
            .findByPlaylistId(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found"));

        playlist.setVideoCount(videos.size());
        playlist.setLastSyncedAt(LocalDateTime.now());

        // 각 영상을 Sermon으로 저장
        for (VideoDto video : videos) {
            Optional<Sermon> existing = sermonRepository
                .findByYoutubeVideoId(video.getVideoId());

            if (existing.isEmpty()) {
                Sermon sermon = createSermonFromVideo(video);
                sermonRepository.save(sermon);
            }
        }

        log.info("Playlist sync completed: {} videos", videos.size());
    }

    // 유틸리티 메서드들...

    /**
     * ISO 8601 duration을 초 단위로 변환
     * 예: "PT1H30M" -> 5400 (초)
     */
    private Integer parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return null;
        }

        try {
            Duration d = Duration.parse(duration);
            return (int) d.getSeconds();
        } catch (Exception e) {
            log.error("Failed to parse duration: {}", duration, e);
            return null;
        }
    }

    /**
     * 제목에서 설교 날짜 추출
     * 예: "2026.02.03 주일 설교" -> 2026-02-03
     */
    private LocalDate parseSermonDate(String title, LocalDateTime publishedAt) {
        // 정규식으로 날짜 추출 (YYYY.MM.DD, YYYY-MM-DD, YYYY/MM/DD)
        Pattern pattern = Pattern.compile("(\\d{4})[./-](\\d{1,2})[./-](\\d{1,2})");
        Matcher matcher = pattern.matcher(title);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            return LocalDate.of(year, month, day);
        }

        // 날짜를 찾지 못하면 업로드 날짜 사용
        return publishedAt.toLocalDate();
    }

    /**
     * 제목/설명에서 설교자 추출
     */
    private String parsePreacher(String title, String description) {
        // 규칙 1: "담임목사 OOO" 패턴
        Pattern pattern1 = Pattern.compile("담임목사\\s*([가-힣]{2,4})");
        Matcher matcher1 = pattern1.matcher(title + " " + description);
        if (matcher1.find()) {
            return matcher1.group(1);
        }

        // 규칙 2: "목사 OOO" 패턴
        Pattern pattern2 = Pattern.compile("목사\\s*([가-힣]{2,4})");
        Matcher matcher2 = pattern2.matcher(title + " " + description);
        if (matcher2.find()) {
            return matcher2.group(1);
        }

        // 기본값
        return "담임목사";
    }
}
```

---

### 3. Scheduled Tasks

#### YouTubeScheduler.java
```java
@Component
@Slf4j
public class YouTubeScheduler {

    private final YouTubeService youtubeService;

    /**
     * 매시간 최신 영상 동기화
     */
    @Scheduled(cron = "0 0 * * * *")
    public void syncLatestVideos() {
        log.info("Scheduled: Syncing latest videos");
        try {
            youtubeService.syncLatestVideos();
        } catch (Exception e) {
            log.error("Failed to sync latest videos", e);
        }
    }

    /**
     * 1분마다 라이브 상태 확인
     */
    @Scheduled(fixedRate = 60000)
    public void checkLiveStatus() {
        log.debug("Scheduled: Checking live status");
        try {
            youtubeService.checkLiveStatus();
        } catch (Exception e) {
            log.error("Failed to check live status", e);
        }
    }

    /**
     * 6시간마다 재생목록 동기화
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void syncAllPlaylists() {
        log.info("Scheduled: Syncing all playlists");
        try {
            List<YouTubePlaylist> playlists = youtubeService.getAllActivePlaylists();
            for (YouTubePlaylist playlist : playlists) {
                youtubeService.syncPlaylist(playlist.getPlaylistId());
            }
        } catch (Exception e) {
            log.error("Failed to sync playlists", e);
        }
    }

    /**
     * 매일 자정 오래된 라이브 레코드 정리
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldLiveRecords() {
        log.info("Scheduled: Cleaning up old live records");
        try {
            youtubeService.cleanupOldLiveRecords(30); // 30일 이전 데이터 삭제
        } catch (Exception e) {
            log.error("Failed to cleanup old live records", e);
        }
    }
}
```

---

### 4. REST API Controllers

#### YouTubeController.java
```java
@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YouTubeController {

    private final YouTubeService youtubeService;
    private final YouTubeLiveRepository youtubeLiveRepository;

    /**
     * 현재 라이브 방송 정보
     */
    @GetMapping("/live/status")
    public ResponseEntity<LiveStatusResponse> getLiveStatus() {
        List<YouTubeLive> liveStreams = youtubeLiveRepository
            .findByStatus(LiveStatus.LIVE);

        if (liveStreams.isEmpty()) {
            return ResponseEntity.ok(LiveStatusResponse.noLive());
        }

        YouTubeLive live = liveStreams.get(0); // 첫 번째 라이브
        return ResponseEntity.ok(LiveStatusResponse.from(live));
    }

    /**
     * 수동 영상 동기화 (관리자용)
     */
    @PostMapping("/sync/videos")
    public ResponseEntity<SyncResponse> syncVideos() {
        youtubeService.syncLatestVideos();
        return ResponseEntity.ok(SyncResponse.success("영상 동기화 완료"));
    }

    /**
     * 재생목록 목록 조회
     */
    @GetMapping("/playlists")
    public ResponseEntity<List<YouTubePlaylistDto>> getPlaylists() {
        List<YouTubePlaylist> playlists = youtubeService.getAllActivePlaylists();
        return ResponseEntity.ok(
            playlists.stream()
                .map(YouTubePlaylistDto::from)
                .collect(Collectors.toList())
        );
    }

    /**
     * 재생목록 영상 목록 조회
     */
    @GetMapping("/playlists/{playlistId}/videos")
    public ResponseEntity<List<SermonDto>> getPlaylistVideos(
        @PathVariable String playlistId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        List<Sermon> sermons = youtubeService.getSermonsByPlaylist(playlistId, limit);
        return ResponseEntity.ok(
            sermons.stream()
                .map(SermonDto::from)
                .collect(Collectors.toList())
        );
    }
}
```

---

## Frontend 구현 설계

### 1. API Client (TypeScript)

#### lib/youtube-api.ts
```typescript
import { api } from './api';

export interface LiveStatus {
  isLive: boolean;
  youtubeVideoId?: string;
  title?: string;
  viewerCount?: number;
  startTime?: string;
}

export interface YouTubePlaylist {
  id: number;
  playlistId: string;
  title: string;
  description?: string;
  thumbnailUrl?: string;
  videoCount: number;
  category: string;
  lastSyncedAt: string;
}

export const youtubeApi = {
  // 현재 라이브 상태
  getLiveStatus: () =>
    api.get<LiveStatus>('/youtube/live/status'),

  // 재생목록 목록
  getPlaylists: () =>
    api.get<YouTubePlaylist[]>('/youtube/playlists'),

  // 재생목록 영상 목록
  getPlaylistVideos: (playlistId: string) =>
    api.get<Sermon[]>(`/youtube/playlists/${playlistId}/videos`),
};
```

---

### 2. 실시간 라이브 컴포넌트

#### components/LiveWorship.tsx
```typescript
'use client';

import { useQuery } from '@tanstack/react-query';
import { youtubeApi } from '@/lib/youtube-api';

export function LiveWorship() {
  const { data: liveStatus, isLoading } = useQuery({
    queryKey: ['youtube-live'],
    queryFn: youtubeApi.getLiveStatus,
    refetchInterval: 60000, // 1분마다 갱신
  });

  if (isLoading) {
    return <div className="animate-pulse">로딩 중...</div>;
  }

  if (!liveStatus?.isLive) {
    return (
      <div className="rounded-lg bg-gray-100 p-8 text-center">
        <p className="text-gray-600">현재 라이브 방송이 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <span className="flex h-3 w-3">
          <span className="absolute inline-flex h-3 w-3 animate-ping rounded-full bg-red-400 opacity-75"></span>
          <span className="relative inline-flex h-3 w-3 rounded-full bg-red-500"></span>
        </span>
        <span className="font-bold text-red-600">LIVE</span>
        {liveStatus.viewerCount && (
          <span className="text-sm text-gray-600">
            시청자 {liveStatus.viewerCount.toLocaleString()}명
          </span>
        )}
      </div>

      <div className="aspect-video w-full overflow-hidden rounded-lg">
        <iframe
          src={`https://www.youtube.com/embed/${liveStatus.youtubeVideoId}?autoplay=1`}
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
          className="h-full w-full"
        />
      </div>

      <h2 className="text-xl font-bold">{liveStatus.title}</h2>
    </div>
  );
}
```

---

### 3. 재생목록 컴포넌트

#### components/YouTubePlaylist.tsx
```typescript
'use client';

import { useQuery } from '@tanstack/react-query';
import { youtubeApi } from '@/lib/youtube-api';

interface Props {
  playlistId: string;
}

export function YouTubePlaylist({ playlistId }: Props) {
  const { data: videos, isLoading } = useQuery({
    queryKey: ['youtube-playlist', playlistId],
    queryFn: () => youtubeApi.getPlaylistVideos(playlistId),
  });

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="aspect-video w-full overflow-hidden rounded-lg">
      <iframe
        src={`https://www.youtube.com/embed/videoseries?list=${playlistId}`}
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowFullScreen
        className="h-full w-full"
      />
    </div>
  );
}
```

---

## 환경 설정

### application.yml
```yaml
youtube:
  api:
    key: ${YOUTUBE_API_KEY}
  channel:
    id: ${YOUTUBE_CHANNEL_ID}
  playlists:
    sunday-sermon: PLxxxxxxxxx
    wednesday-sermon: PLyyyyyyyyy

spring:
  task:
    scheduling:
      pool:
        size: 5
```

### .env (Frontend)
```bash
NEXT_PUBLIC_YOUTUBE_CHANNEL_ID=UCxxxxxxxxxxxxxxx
```

---

## API Key 발급 방법

### 1. Google Cloud Console 접속
https://console.cloud.google.com

### 2. 프로젝트 생성
- "새 프로젝트" 클릭
- 프로젝트 이름: "sungbok-church"

### 3. YouTube Data API v3 활성화
- "API 및 서비스" → "라이브러리"
- "YouTube Data API v3" 검색
- "사용 설정" 클릭

### 4. API Key 생성
- "API 및 서비스" → "사용자 인증 정보"
- "사용자 인증 정보 만들기" → "API 키"
- API 키 복사

### 5. API Key 제한 설정 (선택사항)
- "애플리케이션 제한사항": IP 주소
- "API 제한사항": YouTube Data API v3만 허용

---

## 데이터 파싱 규칙

### 1. 설교 날짜 파싱

**패턴**:
- `2026.02.03 주일 설교` → `2026-02-03`
- `2026-02-03 수요예배` → `2026-02-03`
- `20260203 특별집회` → `2026-02-03`

**정규식**:
```java
Pattern.compile("(\\d{4})[./-](\\d{1,2})[./-](\\d{1,2})");
```

### 2. 설교자 파싱

**패턴**:
- `담임목사 홍길동` → `홍길동`
- `설교: 김철수 목사` → `김철수`

**정규식**:
```java
Pattern.compile("담임?목사\\s*([가-힣]{2,4})");
```

### 3. 본문 파싱

**패턴**:
- `요한복음 3:16` → `요한복음 3:16`
- `마태복음 5장 1-10절` → `마태복음 5:1-10`

---

## 에러 핸들링

### 1. API 할당량 초과
```java
@ControllerAdvice
public class YouTubeApiExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleApiError(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
            // 할당량 초과
            log.error("YouTube API quota exceeded");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of("YouTube API 할당량 초과"));
        }
        return ResponseEntity.status(e.getStatusCode())
            .body(ErrorResponse.of(e.getMessage()));
    }
}
```

### 2. 네트워크 오류
```java
@Retryable(
    value = {RestClientException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000)
)
public List<VideoDto> getLatestVideos(int maxResults) {
    // API 호출
}
```

---

## 테스트 전략

### 1. Unit Test

```java
@ExtendWith(MockitoExtension.class)
class YouTubeServiceTest {

    @Mock
    private YouTubeApiClient youtubeApiClient;

    @Mock
    private SermonRepository sermonRepository;

    @InjectMocks
    private YouTubeService youtubeService;

    @Test
    void syncLatestVideos_shouldSaveNewVideos() {
        // Given
        List<VideoDto> videos = Arrays.asList(
            createVideoDto("video1", "2026.02.03 주일설교")
        );
        when(youtubeApiClient.getLatestVideos(10)).thenReturn(videos);
        when(sermonRepository.findByYoutubeVideoId("video1"))
            .thenReturn(Optional.empty());

        // When
        youtubeService.syncLatestVideos();

        // Then
        verify(sermonRepository, times(1)).save(any(Sermon.class));
    }
}
```

### 2. Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class YouTubeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getLiveStatus_shouldReturnLiveInfo() throws Exception {
        mockMvc.perform(get("/api/youtube/live/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isLive").exists());
    }
}
```

---

## 성능 최적화

### 1. 캐싱 (Redis)

```java
@Cacheable(value = "youtube:live", unless = "#result == null")
public LiveStatus getLiveStatus() {
    // ...
}

@CacheEvict(value = "youtube:live", allEntries = true)
@Scheduled(fixedRate = 60000)
public void evictLiveCache() {
    // 1분마다 캐시 무효화
}
```

### 2. 배치 처리

```java
// 한 번에 최대 50개 영상 ID 조회
private List<VideoDto> getVideoDetails(List<String> videoIds) {
    List<VideoDto> results = new ArrayList<>();

    // 50개씩 나눠서 조회
    for (int i = 0; i < videoIds.size(); i += 50) {
        List<String> batch = videoIds.subList(
            i,
            Math.min(i + 50, videoIds.size())
        );
        results.addAll(fetchVideoDetailsBatch(batch));
    }

    return results;
}
```

---

## 모니터링

### 1. 할당량 사용량 추적

```java
@Component
public class YouTubeQuotaMonitor {

    private final AtomicInteger dailyUsage = new AtomicInteger(0);

    public void recordUsage(int units) {
        int current = dailyUsage.addAndGet(units);

        if (current > 8000) { // 80% 경고
            log.warn("YouTube API quota usage: {}/10000", current);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyUsage() {
        dailyUsage.set(0);
        log.info("YouTube API quota usage reset");
    }
}
```

### 2. 로깅

```yaml
logging:
  level:
    com.sungbok.church.youtube: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

---

## 마이그레이션 가이드

### 1. 초기 데이터 동기화

```java
@Component
public class InitialDataLoader implements CommandLineRunner {

    private final YouTubeService youtubeService;

    @Override
    public void run(String... args) throws Exception {
        if (shouldRunInitialSync()) {
            log.info("Running initial YouTube data sync...");

            // 과거 100개 영상 동기화
            youtubeService.syncLatestVideos(100);

            log.info("Initial sync completed");
        }
    }
}
```

---

## 다음 단계

1. ✅ YouTube API 연동 설계 완료
2. ⬜ **Backend 구현** (YouTubeService, Scheduler)
3. ⬜ **Frontend 구현** (LiveWorship, Playlist 컴포넌트)
4. ⬜ **API Key 발급 및 설정**
5. ⬜ **초기 데이터 동기화**
6. ⬜ **테스트 및 배포**

---

**작성일**: 2026-02-03
**작성자**: Claude Code
**문서 버전**: 1.0
**상태**: Design Complete
