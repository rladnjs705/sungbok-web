# YouTube API 연동 Plan

## 문서 정보

- **Feature**: youtube-api
- **Phase**: Plan
- **작성일**: 2026-02-03
- **우선순위**: High (Backend API 완료 후 필수 작업)
- **예상 기간**: 2-3일

---

## 1. 목표 (Goal)

YouTube Data API v3를 활용하여 성복교회 YouTube 채널의 영상을 자동으로 가져오고, 실시간 라이브 스트리밍을 지원하는 시스템을 구축합니다.

### 핵심 목표
1. **실시간 라이브 감지**: 예배 시간에 자동으로 라이브 방송 표시
2. **자동 영상 동기화**: 최신 설교 영상 자동 저장
3. **재생목록 관리**: 카테고리별 영상 분류
4. **할당량 관리**: YouTube API 일일 10,000 units 제한 내 운영

---

## 2. 배경 (Background)

### 2.1 현재 상황

**완료된 작업**:
- ✅ Entity Layer (YouTubeLive, Sermon, YouTubePlaylist, VideoGallery, Hymn)
- ✅ Repository Layer (YouTube 관련 5개 Repository)
- ✅ Service Layer (YouTubeLiveService, SermonService 등)
- ✅ Controller Layer (YouTubeLiveController 등 REST API)

**문제점**:
- ❌ YouTube API 실제 연동 없음 (데이터베이스 CRUD만 가능)
- ❌ 수동으로 영상 정보 입력 필요
- ❌ 라이브 상태 수동 업데이트 필요
- ❌ 최신 영상 자동 동기화 불가

### 2.2 요구사항 출처

1. **실시간 예배 중계**: 예배 시간에 자동으로 라이브 방송 표시 필요
2. **관리 부담 감소**: 설교 영상 수동 업로드 작업 자동화
3. **영상 아카이브**: 과거 설교 영상 검색 및 분류
4. **사용자 경험**: 최신 영상 자동 노출

---

## 3. 범위 (Scope)

### 3.1 포함 항목 (In Scope)

#### Phase 1: YouTube API Client 구현 (1일)
- [x] YouTube Data API v3 설정
  - API Key 발급 및 환경 변수 설정
  - Spring Boot 의존성 추가
- [ ] YouTubeApiClient 구현
  - `fetchLatestVideos()` - 최신 영상 조회
  - `fetchVideoDetails()` - 영상 상세 정보
  - `fetchLiveStreams()` - 라이브 방송 목록
  - `fetchPlaylistItems()` - 재생목록 영상
  - `fetchChannelInfo()` - 채널 정보
- [ ] API 응답 파싱
  - DTO 매핑 (YouTube API → Entity)
  - 에러 처리 (Rate Limit, Network Error)

#### Phase 2: 자동 동기화 Service (1일)
- [ ] YouTubeSyncService 구현
  - 최신 영상 동기화 로직
  - 라이브 상태 업데이트 로직
  - 재생목록 동기화 로직
  - 중복 체크 및 업데이트 로직
- [ ] 비즈니스 로직
  - 설교 날짜 파싱 (제목에서 추출)
  - 카테고리 자동 분류
  - 썸네일 다운로드 및 저장

#### Phase 3: Scheduled Tasks (0.5일)
- [ ] 스케줄러 설정
  - `@EnableScheduling` 활성화
  - 라이브 스캔: 1분마다 (`fixedRate = 60000`)
  - 영상 동기화: 매시간 (`cron = "0 0 * * * *"`)
  - 재생목록 동기화: 6시간마다 (`cron = "0 0 */6 * * *"`)
- [ ] 스케줄러 모니터링
  - 로그 기록
  - 실패 시 재시도 로직

#### Phase 4: 할당량 관리 (0.5일)
- [ ] 할당량 추적
  - 사용량 카운터 구현
  - 일일 제한 체크
- [ ] 최적화
  - 캐싱 전략 (Redis 또는 In-Memory)
  - 불필요한 API 호출 제거

### 3.2 제외 항목 (Out of Scope)

- ❌ YouTube 영상 업로드 (교회에서 별도 관리)
- ❌ 댓글 관리
- ❌ 구독자 통계
- ❌ OAuth 2.0 인증 (API Key만 사용)
- ❌ 영상 편집 기능

---

## 4. 이해관계자 (Stakeholders)

| 역할 | 이름 | 책임 | 필요 사항 |
|-----|------|------|----------|
| Product Owner | 교회 관리자 | YouTube 채널 관리 | API Key 제공, 채널 ID |
| Developer | AI Agent | 구현 및 테스트 | YouTube API 문서, 개발 환경 |
| End User | 교인 | 영상 시청 | 최신 영상 자동 노출 |

---

## 5. 전제 조건 (Prerequisites)

### 5.1 기술 요구사항

**필수**:
- ✅ Spring Boot 4.0.2 (완료)
- ✅ PostgreSQL 18.1 (완료)
- ✅ Backend API (Entity, Repository, Service, Controller) (완료)
- ⏳ YouTube Data API Key 발급
- ⏳ Google Cloud Console 프로젝트 생성

**선택**:
- Redis (캐싱용)
- Swagger (API 문서화)

### 5.2 외부 의존성

| 서비스 | 용도 | 비용 | 제한 |
|--------|------|------|------|
| YouTube Data API v3 | 영상 데이터 조회 | 무료 | 10,000 units/day |
| Google Cloud Storage | 썸네일 저장 (선택) | 종량제 | - |

### 5.3 환경 설정

```properties
# application.yml
youtube:
  api:
    key: ${YOUTUBE_API_KEY}
    channel-id: ${YOUTUBE_CHANNEL_ID}
    base-url: https://www.googleapis.com/youtube/v3
  sync:
    enabled: true
    live-check-interval: 60000  # 1분
    video-sync-cron: "0 0 * * * *"  # 매시간
    playlist-sync-cron: "0 0 */6 * * *"  # 6시간
```

---

## 6. 성공 기준 (Success Criteria)

### 6.1 기능 요구사항

| # | 기준 | 측정 방법 |
|---|------|----------|
| 1 | 라이브 방송 1분 이내 감지 | 스케줄러 로그 확인 |
| 2 | 최신 영상 1시간 이내 동기화 | 데이터베이스 확인 |
| 3 | 재생목록 6시간마다 업데이트 | 스케줄러 로그 확인 |
| 4 | 중복 영상 0건 | 데이터베이스 Unique 제약 |
| 5 | API 할당량 80% 이하 사용 | 모니터링 대시보드 |

### 6.2 비기능 요구사항

| # | 기준 | 목표 |
|---|------|------|
| 1 | API 응답 시간 | < 2초 |
| 2 | 스케줄러 안정성 | 99% 성공률 |
| 3 | 에러 복구 | 자동 재시도 3회 |
| 4 | 로그 상세도 | INFO 이상 |

---

## 7. 위험 요소 (Risks)

| 위험 | 확률 | 영향 | 완화 방안 |
|-----|:----:|:----:|----------|
| API 할당량 초과 | 중간 | 높음 | 캐싱, 호출 최적화, 할당량 모니터링 |
| YouTube API 장애 | 낮음 | 높음 | 재시도 로직, 에러 로깅, 수동 백업 |
| 네트워크 타임아웃 | 높음 | 중간 | Timeout 설정, Circuit Breaker |
| 영상 메타데이터 파싱 실패 | 중간 | 낮음 | 기본값 설정, 수동 수정 가능 |

---

## 8. 제약 사항 (Constraints)

### 8.1 기술적 제약
- YouTube API 일일 할당량: 10,000 units
- API Key 방식만 사용 (OAuth 불필요)
- 라이브 스트림 개수 제한 없음

### 8.2 시간적 제약
- 개발 기간: 2-3일
- 우선순위: High (Backend API 다음)

### 8.3 예산 제약
- YouTube API: 무료 (할당량 내)
- 추가 비용 없음

---

## 9. 타임라인 (Timeline)

### Phase별 예상 일정

| Phase | 작업 | 예상 시간 | 의존성 |
|-------|------|-----------|--------|
| **Phase 1** | YouTube API Client 구현 | 1일 | API Key 발급 |
| **Phase 2** | YouTubeSyncService 구현 | 1일 | Phase 1 |
| **Phase 3** | Scheduled Tasks 구현 | 0.5일 | Phase 2 |
| **Phase 4** | 할당량 관리 및 최적화 | 0.5일 | Phase 3 |
| **테스트** | 통합 테스트 및 검증 | 0.5일 | Phase 4 |

**총 예상 기간**: 2.5 - 3일

### 마일스톤

```
Day 1: YouTube API Client ✅
Day 2: YouTubeSyncService + Scheduler ✅
Day 3: 테스트 + 최적화 + 문서화 ✅
```

---

## 10. 측정 지표 (Metrics)

### 10.1 개발 지표

| 지표 | 목표 | 현재 |
|-----|:----:|:----:|
| API Client Methods | 5개 | 0개 |
| Service Methods | 4개 | 0개 |
| Scheduled Tasks | 3개 | 0개 |
| 단위 테스트 | 80% 커버리지 | 0% |

### 10.2 운영 지표

| 지표 | 목표 | 측정 방법 |
|-----|:----:|----------|
| API 성공률 | > 95% | 로그 분석 |
| 할당량 사용률 | < 80% | 모니터링 |
| 라이브 감지 지연 | < 1분 | 타임스탬프 비교 |
| 영상 동기화 정확도 | > 99% | 데이터 검증 |

---

## 11. 의존성 (Dependencies)

### 11.1 내부 의존성

| 의존 항목 | 상태 | 차단 여부 |
|----------|:----:|:--------:|
| Entity Layer (YouTubeLive, Sermon) | ✅ 완료 | - |
| Repository Layer | ✅ 완료 | - |
| Service Layer (YouTubeLiveService) | ✅ 완료 | - |
| Database Migration | ⏳ 대기 | ❌ Non-blocking |

### 11.2 외부 의존성

| 의존 항목 | 제공자 | 상태 |
|----------|--------|:----:|
| YouTube Data API v3 | Google | ✅ 사용 가능 |
| API Key | 교회 관리자 | ⏳ 발급 필요 |
| 채널 ID | 교회 YouTube | ✅ 확인 가능 |

---

## 12. 다음 단계 (Next Steps)

### 12.1 즉시 수행
1. ✅ Plan 문서 작성 완료
2. ⏳ YouTube API Key 발급 요청
3. ⏳ Google Cloud Console 프로젝트 생성

### 12.2 순차 수행
1. `/pdca design youtube-api` - Design 문서 검토/업데이트
2. `/pdca do youtube-api` - 구현 시작
3. Phase 1: YouTubeApiClient 구현
4. Phase 2: YouTubeSyncService 구현
5. Phase 3: Scheduled Tasks 구현
6. Phase 4: 테스트 및 최적화

---

## 13. 참고 자료 (References)

### 공식 문서
- [YouTube Data API v3 Documentation](https://developers.google.com/youtube/v3)
- [API Key 발급 가이드](https://developers.google.com/youtube/v3/getting-started)
- [할당량 정책](https://developers.google.com/youtube/v3/determine_quota_cost)

### 관련 문서
- Design Document: `/docs/02-design/features/youtube-api.design.md`
- Backend API Archive: `/docs/archive/2026-02/backend-api/`

### 예제 코드
- [Spring Boot YouTube API Integration](https://github.com/youtube/api-samples/tree/master/java)
- [Google API Client Library](https://github.com/googleapis/google-api-java-client)

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|-----|------|----------|--------|
| 2026-02-03 | 1.0 | 초기 Plan 문서 작성 | AI Agent |

---

## 승인

| 역할 | 이름 | 날짜 | 서명 |
|-----|------|------|------|
| Product Owner | - | - | - |
| Tech Lead | - | - | - |
| Developer | AI Agent | 2026-02-03 | ✅ |

---

**다음 단계**: `/pdca design youtube-api` (Design 문서 검토)
