# ISR (Incremental Static Regeneration) 전략

> **프로젝트**: 성복교회 홈페이지
> **채택일**: 2026-02-05
> **Next.js**: 16.1 (App Router)

---

## 📋 ISR 채택 이유

### 교회 웹사이트 특성
- **콘텐츠 업데이트 빈도**: 주 1~2회 (설교), 주 2~3회 (공지)
- **트래픽 패턴**: 주일/수요일 예배 전후 집중
- **실시간성 요구**: 낮음 (몇 분~수십 분 지연 허용)
- **SEO 중요도**: 높음 (검색 유입 중요)

### ISR 장점
1. ⚡ **빠른 응답 속도**: 0.1초 (CDN에서 정적 파일)
2. 💰 **비용 효율**: 서버 부하 최소화
3. 📈 **무한 확장**: 트래픽 폭증 대응
4. 🔍 **완벽한 SEO**: 정적 HTML
5. 🎯 **자동 관리**: Next.js가 알아서 재생성

---

## 🎯 페이지별 Revalidate 전략

### 1. Static Pages (거의 변경 없음)

```typescript
// app/page.tsx (메인 페이지)
export const revalidate = 3600; // 1시간

// app/about/page.tsx (교회소개)
export const revalidate = 86400; // 24시간

// app/ministries/page.tsx (다음세대)
export const revalidate = 7200; // 2시간
```

### 2. Dynamic Content Pages (주기적 업데이트)

```typescript
// app/sermons/page.tsx (설교 목록)
export const revalidate = 1800; // 30분

// app/sermons/[id]/page.tsx (설교 상세)
export const revalidate = 300; // 5분

// app/news/page.tsx (공지사항)
export const revalidate = 600; // 10분

// app/news/[id]/page.tsx (공지 상세)
export const revalidate = 300; // 5분
```

### 3. Real-time Pages (실시간 필요)

```typescript
// app/admin/dashboard/page.tsx (관리자)
export const dynamic = 'force-dynamic'; // SSR

// app/live/page.tsx (실시간 예배)
export const dynamic = 'force-dynamic'; // SSR
```

---

## 💻 구현 예시

### 기본 ISR 설정

```typescript
// app/sermons/page.tsx
export const revalidate = 1800; // 30분마다 자동 재생성

export default async function SermonsPage() {
  const res = await fetch('http://localhost:8080/api/sermons', {
    next: { revalidate: 1800 }
  });

  const sermons = await res.json();

  return <SermonList sermons={sermons} />;
}
```

### On-Demand Revalidation (수동 재생성)

```typescript
// app/api/revalidate/route.ts
import { revalidatePath, revalidateTag } from 'next/cache';
import { NextRequest } from 'next/server';

export async function POST(request: NextRequest) {
  const secret = request.nextUrl.searchParams.get('secret');

  // 보안: secret 토큰 검증
  if (secret !== process.env.REVALIDATE_SECRET) {
    return Response.json({ message: 'Invalid token' }, { status: 401 });
  }

  const { path, tag } = await request.json();

  try {
    if (path) {
      revalidatePath(path); // 특정 경로 재생성
    }

    if (tag) {
      revalidateTag(tag); // 특정 태그 재생성
    }

    return Response.json({
      revalidated: true,
      now: Date.now()
    });
  } catch (err) {
    return Response.json({
      message: 'Error revalidating',
      error: err
    }, { status: 500 });
  }
}
```

### Cache Tags 활용

```typescript
// app/sermons/page.tsx
export default async function SermonsPage() {
  const res = await fetch('http://localhost:8080/api/sermons', {
    next: {
      revalidate: 1800,
      tags: ['sermons'] // 태그로 그룹화
    }
  });

  const sermons = await res.json();
  return <SermonList sermons={sermons} />;
}

// 설교 업로드 후 백엔드에서 호출:
// POST /api/revalidate?secret=xxx
// Body: { "tag": "sermons" }
```

---

## 🔧 Backend 연동

### Spring Boot에서 On-Demand Revalidation 트리거

```java
// SermonController.java
@PostMapping("/api/sermons")
public ResponseEntity<Sermon> createSermon(@RequestBody SermonDto dto) {
    Sermon sermon = sermonService.save(dto);

    // Next.js ISR 재생성 요청
    revalidationService.triggerRevalidation("/sermons");
    revalidationService.triggerRevalidation("/");

    return ResponseEntity.ok(sermon);
}
```

```java
// RevalidationService.java
@Service
public class RevalidationService {

    @Value("${nextjs.revalidate.url}")
    private String revalidateUrl;

    @Value("${nextjs.revalidate.secret}")
    private String secret;

    private final RestTemplate restTemplate;

    public void triggerRevalidation(String path) {
        String url = String.format("%s?secret=%s", revalidateUrl, secret);

        Map<String, String> body = Map.of("path", path);

        try {
            restTemplate.postForEntity(url, body, String.class);
            log.info("Revalidated: {}", path);
        } catch (Exception e) {
            log.error("Revalidation failed: {}", path, e);
        }
    }

    public void triggerRevalidationByTag(String tag) {
        String url = String.format("%s?secret=%s", revalidateUrl, secret);

        Map<String, String> body = Map.of("tag", tag);

        try {
            restTemplate.postForEntity(url, body, String.class);
            log.info("Revalidated tag: {}", tag);
        } catch (Exception e) {
            log.error("Revalidation failed for tag: {}", tag, e);
        }
    }
}
```

### application.yml 설정

```yaml
nextjs:
  revalidate:
    url: ${NEXTJS_URL:http://localhost:3000}/api/revalidate
    secret: ${REVALIDATE_SECRET:your-secret-token-here}
```

---

## 📊 Revalidate 시간 결정 가이드

| 콘텐츠 유형 | 업데이트 빈도 | Revalidate 시간 | 이유 |
|------------|--------------|----------------|------|
| 메인 페이지 | 주 1~2회 | 3600s (1시간) | 최신 설교 노출 |
| 교회 소개 | 월 1회 | 86400s (24시간) | 거의 변경 없음 |
| 설교 목록 | 주 1~2회 | 1800s (30분) | 새 설교 빠른 반영 |
| 설교 상세 | 조회수 업데이트 | 300s (5분) | 실시간성 필요 |
| 공지사항 | 주 2~3회 | 600s (10분) | 빠른 공지 필요 |
| 주보 | 주 1회 | 3600s (1시간) | 주일 전 업데이트 |
| 행사 일정 | 월 2~3회 | 1800s (30분) | 중간 빈도 |

---

## 🎯 성능 비교

### 메트릭 비교 (1000명 동시 접속 기준)

| 항목 | ISR | SSR |
|------|-----|-----|
| 응답 속도 | 0.1초 | 1~2초 |
| 서버 요청 | 0회 (CDN) | 1000회 |
| 서버 부하 | 거의 없음 | 높음 |
| 월 비용 | $20 | $100+ |
| 확장성 | 무한 | 서버 사양 제한 |

### 트래픽 시나리오

**주일 예배 전후 (1000명 접속):**
- ISR: CDN에서 즉시 제공 → 문제 없음
- SSR: 서버 과부하 위험

**평일 (50명 접속):**
- ISR: CDN에서 제공 → 빠름
- SSR: 서버 렌더링 → 느림

---

## 🔒 보안 고려사항

### Revalidation Secret 관리

```bash
# .env.local
REVALIDATE_SECRET=your-super-secret-token-here-change-this

# .env.production
REVALIDATE_SECRET=production-secret-token
```

### Secret 생성 예시

```bash
# 랜덤 secret 생성
openssl rand -base64 32
```

---

## 📈 모니터링

### Next.js 빌드 시 확인

```bash
npm run build

# 출력 예시:
# ○ (Static)   /about
# ƒ (Dynamic)  /sermons/[id] (ISR: 300s)
# λ (Server)   /admin/dashboard
```

### Vercel Analytics (프로덕션)

- Cache Hit Rate 확인
- ISR 재생성 빈도 모니터링
- 페이지 로딩 속도 추적

---

## ✅ 체크리스트

### 구현 완료 체크

- [ ] 페이지별 revalidate 시간 설정
- [ ] On-demand revalidation API 구현
- [ ] Backend에서 revalidation 트리거 연동
- [ ] Cache tags 적용
- [ ] Secret 환경변수 설정
- [ ] 빌드 후 ISR 작동 확인
- [ ] 프로덕션 배포 후 모니터링

---

## 📚 참고 문서

- [Next.js ISR 공식 문서](https://nextjs.org/docs/app/building-your-application/data-fetching/fetching-caching-and-revalidating)
- [Revalidation 가이드](https://nextjs.org/docs/app/building-your-application/data-fetching/revalidating)
- [Cache Tags](https://nextjs.org/docs/app/api-reference/functions/revalidateTag)

---

**작성일**: 2026-02-05
**작성자**: AI Assistant
**다음 단계**: Phase 1 CRITICAL 최적화 적용
