# Next.js 16.1 빠른 시작 가이드 (Spring Boot 개발자용)

Spring Boot 개발자를 위한 Next.js 핵심 개념 정리입니다.

## Spring Boot vs Next.js 비교

| 개념 | Spring Boot | Next.js |
|------|-------------|---------|
| 프로젝트 구조 | `src/main/java`, `src/main/resources` | `src/app`, `src/components` |
| 라우팅 | `@RequestMapping`, `@GetMapping` | 파일 시스템 기반 (`app/` 폴더) |
| 템플릿 | Thymeleaf, JSP | React 컴포넌트 (JSX/TSX) |
| 설정 파일 | `application.yml` | `next.config.ts` |
| 의존성 관리 | `pom.xml` (Maven) | `package.json` (npm) |
| 빌드 도구 | Maven/Gradle | npm/yarn/pnpm |

## 1. 프로젝트 구조

```
frontend/src/
├── app/                    # 라우팅 (Spring의 Controller 역할)
│   ├── layout.tsx         # 공통 레이아웃 (모든 페이지에 적용)
│   ├── page.tsx           # 홈페이지 (/)
│   ├── about/
│   │   └── page.tsx       # /about 페이지
│   └── api/               # API Routes (Backend API 역할 가능)
│       └── hello/route.ts
│
├── components/             # 재사용 컴포넌트 (Spring의 @Component 유사)
│   ├── ui/                # 기본 UI (버튼, 입력 등)
│   └── features/          # 기능별 컴포넌트
│
├── lib/                    # 유틸리티 (Spring의 @Utility 유사)
│   ├── api.ts             # API 클라이언트
│   └── utils.ts           # 공통 함수
│
└── types/                  # TypeScript 타입 정의 (Java의 DTO 유사)
    └── index.ts
```

## 2. 라우팅 시스템

### Spring Boot 방식
```java
@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    @GetMapping
    public List<Notice> getNotices() { ... }

    @GetMapping("/{id}")
    public Notice getNotice(@PathVariable Long id) { ... }
}
```

### Next.js 방식 (파일 시스템 기반)
```
app/
├── page.tsx              → /
├── about/
│   └── page.tsx          → /about
├── notices/
│   ├── page.tsx          → /notices
│   └── [id]/
│       └── page.tsx      → /notices/123
```

**동적 라우트 예시** (`app/notices/[id]/page.tsx`):
```tsx
export default async function NoticePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  // Spring의 @PathVariable과 동일
  // /notices/123 → id = "123"

  return <div>공지사항 ID: {id}</div>;
}
```

## 3. 컴포넌트 작성

### Server Component (기본, 추천)
```tsx
// app/notices/page.tsx
import { api } from '@/lib/api';

// Spring의 Service 호출과 유사
async function getNotices() {
  return await api.get('/notices');
}

export default async function NoticesPage() {
  const notices = await getNotices();

  return (
    <div>
      <h1>공지사항</h1>
      <ul>
        {notices.map((notice) => (
          <li key={notice.id}>{notice.title}</li>
        ))}
      </ul>
    </div>
  );
}
```

### Client Component (인터랙션 필요시)
```tsx
'use client'; // 이 선언이 있으면 클라이언트 컴포넌트

import { useState } from 'react';

export default function NoticeForm() {
  const [title, setTitle] = useState('');

  const handleSubmit = async () => {
    await api.post('/notices', { title });
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />
      <button type="submit">등록</button>
    </form>
  );
}
```

**언제 Client Component를 사용하나요?**
- `useState`, `useEffect` 등 React Hooks 사용
- 이벤트 핸들러 (`onClick`, `onChange` 등)
- 브라우저 전용 API (예: `window`, `localStorage`)

**기본은 Server Component를 사용하고, 필요할 때만 Client Component로 변경하세요.**

## 4. API 통신

### Frontend에서 Backend 호출

```tsx
// lib/api.ts (이미 생성됨)
export const api = {
  get: <T>(endpoint: string) => request<T>(endpoint, { method: 'GET' }),
  post: <T>(endpoint: string, data: unknown) => request<T>(endpoint, {
    method: 'POST',
    body: JSON.stringify(data),
  }),
};

// 사용 예시
const notices = await api.get<Notice[]>('/notices');
const newNotice = await api.post<Notice>('/notices', { title: '새 공지' });
```

### Spring Boot Controller 예시
```java
@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    @GetMapping
    public ResponseEntity<List<NoticeDto>> getNotices() {
        return ResponseEntity.ok(noticeService.findAll());
    }

    @PostMapping
    public ResponseEntity<NoticeDto> createNotice(@RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.create(request));
    }
}
```

## 5. 환경 변수

### Spring Boot
```yaml
# application.yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db
```

### Next.js
```bash
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

**주의**: `NEXT_PUBLIC_` 접두사가 있는 변수만 브라우저에서 접근 가능합니다.

```tsx
// 사용 예시
const apiUrl = process.env.NEXT_PUBLIC_API_URL;
```

## 6. TypeScript 타입 정의

### Backend DTO (Java)
```java
@Data
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
```

### Frontend Type (TypeScript)
```typescript
// types/index.ts
export interface Notice {
  id: number;
  title: string;
  content: string;
  createdAt: string; // ISO 8601 문자열
}
```

**Tip**: Backend의 DTO와 Frontend의 Type을 일치시키세요.

## 7. 스타일링 (Tailwind CSS)

### 기본 사용법
```tsx
<div className="flex flex-col gap-4 p-6 bg-white rounded-lg shadow">
  <h1 className="text-2xl font-bold">제목</h1>
  <p className="text-gray-600">내용</p>
</div>
```

### 주요 클래스
- `flex`: display: flex
- `p-6`: padding: 1.5rem
- `bg-white`: background-color: white
- `text-2xl`: font-size: 1.5rem
- `rounded-lg`: border-radius: 0.5rem

## 8. shadcn/ui 컴포넌트 사용

### 컴포넌트 설치
```bash
npx shadcn@latest add button
npx shadcn@latest add input
npx shadcn@latest add card
```

### 사용 예시
```tsx
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';

export default function Example() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>로그인</CardTitle>
      </CardHeader>
      <CardContent>
        <Input placeholder="이메일" />
        <Button>로그인</Button>
      </CardContent>
    </Card>
  );
}
```

## 9. 자주 사용하는 명령어

```bash
# 개발 서버 실행 (http://localhost:3000)
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 모드 실행
npm start

# 컴포넌트 설치
npx shadcn@latest add [component-name]
```

## 10. 디버깅

### Console 로그
```tsx
console.log('데이터:', data);
```

### React DevTools
Chrome Extension 설치: "React Developer Tools"

### Network 탭
브라우저 개발자 도구 → Network 탭에서 API 요청 확인

## 다음 학습 리소스

- **공식 문서**: https://nextjs.org/docs
- **shadcn/ui**: https://ui.shadcn.com
- **Tailwind CSS**: https://tailwindcss.com/docs

## 핵심 개념 요약

1. **파일 시스템 라우팅**: `app/` 폴더 구조가 URL 경로를 결정
2. **Server Component 우선**: 기본적으로 서버에서 렌더링, 필요시에만 Client Component 사용
3. **TypeScript**: 타입 안정성으로 런타임 에러 방지
4. **Tailwind CSS**: 유틸리티 클래스 기반 스타일링
5. **API 통신**: `fetch` 또는 커스텀 API 클라이언트 사용

---

궁금한 점이 있으면 언제든 질문하세요!
