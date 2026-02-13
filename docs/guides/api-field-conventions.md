# API Field Naming Conventions

> Backend API와 Frontend 간의 필드명 일치 및 변환 규칙

## 📋 개요

본 문서는 성복교회 홈페이지 프로젝트의 Backend API 응답 필드와 Frontend TypeScript 타입 간의 매핑 규칙을 정의합니다.

---

## ✅ 표준 필드명 규칙

### 카운터 필드명

| 도메인 | 필드명 | 의미 | 일관성 |
|--------|--------|------|--------|
| Notice, Sermon, Event, Gallery, VideoGallery, Testimony, Ministry | `viewCount` | 조회수 (누적) | ✅ 표준 |
| Bulletin | `downloadCount` | 다운로드 횟수 | ⚠️ 도메인 특화 |
| YouTubeLive | `viewerCount` | 실시간 시청자 수 | ⚠️ 도메인 특화 |
| PrayerRequest | `prayerCount` | 기도 횟수 | ⚠️ 도메인 특화 |

**설명**:
- `viewCount`: 대부분의 콘텐츠에서 사용하는 표준 조회수 필드
- `downloadCount`: 주보(PDF) 다운로드에 특화
- `viewerCount`: 실시간 스트리밍의 현재 시청자 수 (누적 아님)
- `prayerCount`: 기도요청에 대한 기도 횟수 (조회와 다른 개념)

### 날짜/시간 필드명

| Backend | Frontend DTO | Frontend Model | 설명 |
|---------|--------------|----------------|------|
| `publishedAt` | `publishedAt` | `date` | ISO 8601 -> "YYYY.MM.DD" |
| `sermonDate` | `sermonDate` | `date` | ISO 8601 -> "YYYY.MM.DD" |
| `eventDate` | `eventDate` | `eventDate` | ISO 8601 -> "YYYY.MM.DD" |
| `bulletinDate` | `bulletinDate` | `date` | ISO 8601 -> "YYYY.MM.DD" |
| `createdAt` | `createdAt` | `createdAt` | ISO 8601 (변환 없음) |
| `updatedAt` | `updatedAt` | `updatedAt` | ISO 8601 (변환 없음) |

### 이미지/썸네일 URL 필드명

| Backend | Frontend DTO | Frontend Model | 설명 |
|---------|--------------|----------------|------|
| `thumbnailUrl` | `thumbnailUrl` | `thumbnailUrl` | 표준 |
| `coverImageUrl` | `coverImageUrl` | `coverImage` | 모델에서 단축 |
| `photoUrl` | `photoUrl` | `photo` | 모델에서 단축 |
| `posterImageUrl` | `posterImageUrl` | `posterImage` | 모델에서 단축 |
| `imageUrl` | `imageUrl` | `image` | 모델에서 단축 |

---

## 🔄 Frontend 모델 변환 규칙

### 필드명 단축 규칙

Backend DTO -> Frontend Model 변환 시 다음 규칙을 적용합니다:

```typescript
// 예시: Sermon
Backend: youtubeVideoId -> Frontend Model: videoId
Backend: bibleVerse -> Frontend Model: verse
Backend: preacher -> Frontend Model: pastor
Backend: publishedAt -> Frontend Model: date
```

### 날짜 형식 변환

```typescript
// ISO 8601 -> 한국어 표시 형식
"2024-03-17T10:00:00" -> "2024.03.17"
"2024-03-17T10:00:00" -> "2024년 3월 17일"
```

### 시간 형식 변환

```typescript
// 24시간제 -> 12시간제 (한국어)
"11:00" -> "오전 11:00"
"14:00" -> "오후 2:00"
```

---

## 📊 도메인별 필드 매핑

### Notice (공지사항)

| Backend | Frontend DTO | Frontend Model | 비고 |
|---------|--------------|----------------|------|
| `id` | `id` | `id` | - |
| `category` | `category` | `category` | Enum -> string |
| `title` | `title` | `title` | - |
| `content` | `content` | `content` | - |
| `author` | `author` | `author` | - |
| `isPinned` | `isPinned` | `isPinned` | - |
| `viewCount` | `viewCount` | `viewCount` | - |
| `publishedAt` | `publishedAt` | `date` | 형식 변환 |
| `createdAt` | `createdAt` | `createdAt` | - |
| `updatedAt` | `updatedAt` | `updatedAt` | - |
| - | `excerpt` | `excerpt` | content에서 생성 |
| - | `imageUrl` | `imageUrl` | content에서 추출 |

### Sermon (설교)

| Backend | Frontend DTO | Frontend Model | 비고 |
|---------|--------------|----------------|------|
| `id` | `id` | `id` | - |
| `title` | `title` | `title` | - |
| `preacher` | `preacher` | `pastor` | 이름 변환 |
| `bibleVerse` | `bibleVerse` | `verse` | 이름 변환 |
| `sermonDate` | `sermonDate` | `date` | 형식 변환 |
| `youtubeVideoId` | `youtubeVideoId` | `videoId` | 이름 변환 |
| `duration` | `duration` | `duration` | 초 -> "MM:SS" |
| `viewCount` | `viewCount` | `viewCount` | - |
| `worshipName` | `worshipName` | `category` | 이름 변환 |
| `isFeatured` | `isFeatured` | `isFeatured` | - |

### Ministry (사역/부서)

| Backend | Frontend DTO | Frontend Model | 비고 |
|---------|--------------|----------------|------|
| `id` | `id` | `id` | - |
| `name` | `name` | `name` | - |
| `category` | `category` | `category` | Enum -> string |
| `photoUrl` | `photoUrl` | `photo` | 이름 변환 |
| `isActive` | `isActive` | `isActive` | - |
| `displayOrder` | `displayOrder` | `displayOrder` | - |

### Event (행사)

| Backend | Frontend DTO | Frontend Model | 비고 |
|---------|--------------|----------------|------|
| `id` | `id` | `id` | - |
| `title` | `title` | `title` | - |
| `posterImageUrl` | `posterImageUrl` | `posterImage` | 이름 변환 |
| `startDate` | `startDate` | `startDate` | 형식 변환 |
| `endDate` | `endDate` | `endDate` | 형식 변환 |
| `isFull` | `isFull` | `isFull` | 계산 필드 |
| `isOngoing` | `isOngoing` | `isOngoing` | 계산 필드 |

---

## 🎯 Enum 매핑

### NoticeCategory

```typescript
Backend (Java Enum) -> Frontend (string)
NEWS        -> "새소식"
EVENT       -> "행사"
ANNOUNCEMENT -> "공지사항"
BULLETIN    -> "주보"
```

### WorshipType

```typescript
Backend (Java Enum) -> Frontend (string)
SUNDAY      -> "주일예배"
WEDNESDAY   -> "수요예배"
DAWN        -> "새벽예배"
FRIDAY      -> "금요기도회"
SPECIAL     -> "특별예배"
```

### LiveStatus

```typescript
Backend (Java Enum) -> Frontend (string)
SCHEDULED   -> "UPCOMING"
LIVE        -> "LIVE"
COMPLETED   -> "COMPLETED"
```

### PrayerStatus

```typescript
Backend (Java Enum) -> Frontend (string)
PENDING     -> "PENDING"
ANSWERED    -> "ANSWERED"
```

---

## ⚠️ 주의사항

### 1. Boolean 필드명

Backend의 Boolean 필드는 `is` 접두사를 사용합니다:
- `isPinned`, `isPublished`, `isActive`, `isFeatured`, `isAnonymous`, `isApproved`

Frontend에서도 동일하게 유지하며, 조건문에서 직접 사용합니다.

### 2. Null 처리

Backend에서 `null`을 반환하는 필드는 Frontend에서 다음과 같이 처리합니다:

```typescript
// 문자열 필드
field: string | null;

// 숫자 필드
field: number | null;

// Template에서 표시
{field ?? "-"}
```

### 3. Page Response 구조

모든 목록 조회 API는 Spring Data Page 구조를 따릅니다:

```typescript
interface PageResponse<T> {
  content: T[];        // 실제 데이터
  totalElements: number; // 전체 개수
  totalPages: number;    // 전체 페이지 수
  number: number;        // 현재 페이지 (0-based)
  size: number;          // 페이지 크기
  first: boolean;        // 첫 페이지 여부
  last: boolean;         // 마지막 페이지 여부
}
```

---

## 📝 변경 이력

| 날짜 | 변경 내용 | 작성자 |
|------|----------|--------|
| 2026-02-11 | 초기 작성 | Kimi Code CLI |
| 2026-02-11 | NoticeResponse에 excerpt, imageUrl 추가 | Kimi Code CLI |
| 2026-02-11 | Frontend 타입 정의 생성 | Kimi Code CLI |

---

## 🔍 참고 문서

- [API Documentation](../../backend/API_DOCUMENTATION.md)
- [Frontend Design Strategy](../02-design/FRONTEND-DESIGN-STRATEGY.md)
- [TypeScript Types](../../../frontend/src/types/)
