# 성복교회 홈페이지 메뉴 구조

## 전체 메뉴 맵

```
🏠 홈
├── 📖 교회소개
│   ├── 인사말
│   ├── 담임목사 소개
│   ├── 교회 연혁
│   ├── 섬기는 이들
│   └── 오시는 길
│
├── 🙏 예배안내
│   ├── 예배 시간
│   └── 온라인 예배 (실시간 스트리밍)
│
├── 📺 말씀과 찬양
│   ├── 주일설교
│   ├── 수요설교
│   ├── 설교 아카이브
│   ├── 찬양 영상
│   └── YouTube 재생목록
│
├── 📚 교육/양육
│   ├── 주일학교
│   │   ├── 유치부
│   │   ├── 유년부
│   │   ├── 초등부
│   │   └── 중고등부
│   ├── 청년부
│   ├── 장년부
│   ├── 새가족반
│   └── 성경공부/제자훈련
│
├── 🌍 선교/봉사
│   ├── 국내선교
│   ├── 해외선교
│   └── 사회봉사
│
├── 💬 나눔터
│   ├── 공지사항
│   ├── 주보 (PDF 다운로드)
│   ├── 사진첩
│   ├── 영상갤러리
│   ├── 간증
│   └── 기도요청
│
└── 💰 헌금안내
    ├── 헌금 계좌
    └── 온라인 헌금 (선택사항)
```

---

## Frontend 라우트 구조 (Next.js)

```
src/app/
├── page.tsx                        # 홈페이지 (/)
│
├── about/                          # 교회소개 (/about)
│   ├── page.tsx                   # 교회소개 메인
│   ├── greeting/page.tsx          # 인사말
│   ├── pastor/page.tsx            # 담임목사 소개
│   ├── history/page.tsx           # 교회 연혁
│   ├── staff/page.tsx             # 섬기는 이들
│   └── location/page.tsx          # 오시는 길
│
├── worship/                        # 예배안내 (/worship)
│   ├── page.tsx                   # 예배 시간 안내
│   └── live/page.tsx              # 온라인 예배 (실시간)
│
├── media/                          # 말씀과 찬양 (/media)
│   ├── sermons/
│   │   ├── page.tsx               # 설교 목록
│   │   ├── [id]/page.tsx          # 설교 상세
│   │   └── latest/page.tsx        # 최신 설교
│   ├── hymns/page.tsx             # 찬양 영상
│   └── playlists/page.tsx         # YouTube 재생목록
│
├── ministries/                     # 교육/양육 (/ministries)
│   ├── page.tsx                   # 부서 목록
│   ├── sunday-school/page.tsx     # 주일학교
│   ├── youth/page.tsx             # 청년부
│   ├── adult/page.tsx             # 장년부
│   ├── newcomer/page.tsx          # 새가족반
│   └── [id]/page.tsx              # 부서 상세
│
├── missions/                       # 선교/봉사 (/missions)
│   ├── page.tsx                   # 선교 목록
│   ├── domestic/page.tsx          # 국내선교
│   ├── overseas/page.tsx          # 해외선교
│   └── social/page.tsx            # 사회봉사
│
├── community/                      # 나눔터 (/community)
│   ├── notices/
│   │   ├── page.tsx               # 공지사항 목록
│   │   └── [id]/page.tsx          # 공지사항 상세
│   ├── bulletins/
│   │   ├── page.tsx               # 주보 목록
│   │   └── [id]/page.tsx          # 주보 상세
│   ├── gallery/
│   │   ├── page.tsx               # 사진첩 목록
│   │   └── [id]/page.tsx          # 갤러리 상세
│   ├── videos/page.tsx            # 영상갤러리
│   ├── testimonies/
│   │   ├── page.tsx               # 간증 목록
│   │   └── [id]/page.tsx          # 간증 상세
│   └── prayers/
│       ├── page.tsx               # 기도요청 목록
│       └── new/page.tsx           # 기도요청 등록
│
└── donation/                       # 헌금안내 (/donation)
    └── page.tsx                   # 헌금 계좌 안내
```

---

## Backend API 엔드포인트 매핑

### 교회소개
```
GET  /api/pages/{slug}           # 정적 페이지 조회
GET  /api/pastors                # 교역자 목록
GET  /api/pastors/{id}           # 교역자 상세
GET  /api/staff                  # 섬기는 이들 목록
```

### 예배안내
```
GET  /api/worships               # 예배 시간 목록
GET  /api/worships/live          # 현재 라이브 방송 정보
GET  /api/youtube/live/status    # 라이브 상태 확인
```

### 말씀과 찬양
```
GET  /api/sermons                # 설교 목록 (페이징, 필터링)
GET  /api/sermons/{id}           # 설교 상세
GET  /api/sermons/latest         # 최신 설교
GET  /api/sermons/featured       # 추천 설교
POST /api/sermons/{id}/view      # 조회수 증가

GET  /api/hymns                  # 찬양 목록
GET  /api/hymns/{id}             # 찬양 상세

GET  /api/youtube/playlists      # 재생목록 목록
GET  /api/youtube/playlists/{id}/videos  # 재생목록 영상
```

### 교육/양육
```
GET  /api/ministries             # 부서 목록
GET  /api/ministries/{id}        # 부서 상세
GET  /api/ministries/{category}  # 카테고리별 부서
```

### 선교/봉사
```
GET  /api/missions               # 선교 목록
GET  /api/missions/{id}          # 선교 상세
GET  /api/missions/{type}        # 선교 타입별 조회
```

### 나눔터
```
# 공지사항
GET  /api/notices                # 공지사항 목록
GET  /api/notices/{id}           # 공지사항 상세
GET  /api/notices/pinned         # 상단 고정 공지
POST /api/notices/{id}/view      # 조회수 증가

# 주보
GET  /api/bulletins              # 주보 목록
GET  /api/bulletins/{id}         # 주보 상세
GET  /api/bulletins/latest       # 최신 주보
POST /api/bulletins/{id}/download # 다운로드 카운트

# 갤러리
GET  /api/galleries              # 사진첩 목록
GET  /api/galleries/{id}         # 갤러리 상세
GET  /api/galleries/{id}/images  # 갤러리 이미지 목록

# 영상갤러리
GET  /api/video-galleries        # 영상갤러리 목록
GET  /api/video-galleries/{id}   # 영상 상세

# 간증
GET  /api/testimonies            # 간증 목록
GET  /api/testimonies/{id}       # 간증 상세
POST /api/testimonies            # 간증 등록 (관리자)

# 기도요청
GET  /api/prayer-requests        # 기도요청 목록
GET  /api/prayer-requests/{id}   # 기도요청 상세
POST /api/prayer-requests        # 기도요청 등록
POST /api/prayer-requests/{id}/pray  # 기도하기 (카운트+1)
```

### 헌금안내
```
GET  /api/donation-accounts      # 헌금 계좌 목록
```

---

## 주요 기능 상세

### 1. 실시간 온라인 예배 (라이브 스트리밍)

**기능**:
- YouTube 라이브 스트리밍 자동 감지
- 예배 시간에 자동으로 라이브 표시
- 실시간 시청자 수 표시

**구현**:
```typescript
// Frontend Component
export default function LiveWorship() {
  const { data: liveInfo } = useQuery({
    queryKey: ['youtube-live'],
    queryFn: () => api.get('/worships/live'),
    refetchInterval: 60000, // 1분마다 갱신
  });

  if (!liveInfo?.isLiveNow) {
    return <div>현재 라이브 방송이 없습니다.</div>;
  }

  return (
    <div className="live-container">
      <span className="live-badge">🔴 LIVE</span>
      <iframe
        src={`https://www.youtube.com/embed/${liveInfo.youtubeVideoId}?autoplay=1`}
        allow="autoplay; encrypted-media"
      />
      <p>현재 시청자: {liveInfo.viewerCount}명</p>
    </div>
  );
}
```

### 2. 최신 설교 영상 자동 표시

**기능**:
- YouTube API로 채널의 최신 영상 자동 가져오기
- 매시간 자동 동기화
- 썸네일, 제목, 날짜 자동 업데이트

**Backend Scheduled Task**:
```java
@Scheduled(cron = "0 0 * * * *") // 매시간
public void syncLatestSermons() {
    String channelId = "YOUR_CHANNEL_ID";
    List<Video> videos = youtubeService.getLatestVideos(channelId, 10);

    for (Video video : videos) {
        sermonRepository.findByYoutubeVideoId(video.getId())
            .orElseGet(() -> sermonRepository.save(
                Sermon.from(video)
            ));
    }
}
```

### 3. 설교 아카이브 (검색 및 필터링)

**필터 옵션**:
- 날짜 범위 (기간 선택)
- 설교자 (담임목사, 부목사 등)
- 본문 (성경 책별)
- 태그 (주제별)

**Frontend**:
```typescript
const { data: sermons } = useQuery({
  queryKey: ['sermons', filters],
  queryFn: () => api.get('/sermons', {
    params: {
      startDate: filters.startDate,
      endDate: filters.endDate,
      preacher: filters.preacher,
      tags: filters.tags,
      page: filters.page,
      size: 20,
    },
  }),
});
```

### 4. 주보 PDF 다운로드

**기능**:
- 주간 주보 PDF 업로드
- 썸네일 자동 생성
- 다운로드 카운트

**Frontend**:
```typescript
<a
  href={bulletin.pdfUrl}
  download={bulletin.title}
  onClick={() => api.post(`/bulletins/${bulletin.id}/download`)}
>
  주보 다운로드 ({formatFileSize(bulletin.fileSize)})
</a>
```

### 5. 사진첩 갤러리 (Lightbox)

**기능**:
- 앨범별 사진 관리
- Lightbox 이미지 뷰어
- 썸네일 최적화

**Frontend Library**:
```bash
npm install yet-another-react-lightbox
```

### 6. 기도요청 게시판

**기능**:
- 익명/실명 선택 가능
- 승인 후 게시
- "기도했어요" 버튼 (카운트)

---

## 데이터 마이그레이션

### 기존 홈페이지 데이터 가져오기

1. **이미지 다운로드**
```bash
# 기존 사이트 이미지 크롤링
wget -r -l 1 -H -t 1 -nd -N -np -A jpg,jpeg,png,gif -erobots=off \
  http://www.sungbok.or.kr/images/
```

2. **주보 PDF 다운로드**
```bash
wget -r -l 1 -H -t 1 -nd -N -np -A pdf -erobots=off \
  http://www.sungbok.or.kr/bulletins/
```

3. **OCI Object Storage 업로드**
```bash
# OCI CLI로 일괄 업로드
oci os object bulk-upload \
  --bucket-name sungbok-church-storage \
  --src-dir ./downloaded-images \
  --prefix images/
```

---

## 다음 단계

1. ✅ 메뉴 구조 정의 완료
2. ⬜ Frontend 라우트 생성
3. ⬜ Backend API 엔드포인트 구현
4. ⬜ YouTube API 연동
5. ⬜ 이미지/파일 마이그레이션

---

**작성일**: 2026-02-03
**참고**: `data-model-v2.design.md`
