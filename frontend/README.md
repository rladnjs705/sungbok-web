# 성복교회 Frontend

> Next.js 16.1 + TypeScript + Tailwind CSS + shadcn/ui

## 🚀 시작하기

### 환경 요구사항
- Node.js 20+
- npm 10+

### 설치 및 실행

```bash
# 패키지 설치
npm install

# 개발 서버 실행
npm run dev
```

- 개발 서버: http://localhost:3000
- API 프록시: http://localhost:8080/api (백엔드)

---

## 📁 프로젝트 구조

```
frontend/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── page.tsx           # 홈페이지 (/)
│   │   ├── layout.tsx         # 루트 레이아웃
│   │   ├── globals.css        # 전역 스타일
│   │   ├── about/             # 교회 소개 (/about)
│   │   ├── worship/           # 예배 안내 (/worship)
│   │   ├── ministries/        # 사역 (/ministries)
│   │   ├── news/              # 교회 소식 (/news)
│   │   ├── mission/           # 선교 (/mission)
│   │   └── ...
│   │
│   ├── components/
│   │   ├── ui/                # shadcn/ui 컴포넌트
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   └── ...
│   │   └── features/          # 기능별 컴포넌트
│   │       ├── hero-section.tsx
│   │       ├── sermon-card.tsx
│   │       └── ...
│   │
│   ├── lib/
│   │   ├── api.ts             # API 클라이언트
│   │   ├── utils.ts           # 유틸리티 함수
│   │   └── design-tokens.ts   # 디자인 토큰
│   │
│   └── types/
│       └── index.ts           # TypeScript 타입 정의
│
├── public/                     # 정적 에셋
│   ├── images/
│   └── fonts/
│
├── components.json             # shadcn/ui 설정
├── next.config.ts              # Next.js 설정
├── tailwind.config.ts          # Tailwind 설정
└── tsconfig.json               # TypeScript 설정
```

---

## 🎨 디자인 시스템

### Cloud Harmony Design System

**3가지 톤(Tone) 전략:**

| 섹션 | 톤 | 특징 |
|------|-----|------|
| 메인 페이지 | 활동적/역동적 | 생동감, 에너지 |
| 부서별 페이지 | 고유성/개성 | 각 부서별 차별화 |
| 교회/목사 소개 | 진중한/신뢰감 | 전통, 권위 |

### 색상 팔레트

```css
/* Primary */
--color-primary: #6B84A3;        /* Cloud Blue */
--color-secondary: #94B3D4;       /* Soft Cloud Blue */
--color-accent: #4A6FA5;          /* Deep Sky */

/* Action Colors */
--color-warm: #D9B88F;            /* Golden Sand */
--color-fresh: #A8C9A8;           /* Soft Sage */
--color-energy: #D4896A;          /* Warm Terracotta */
```

### 폰트

```css
/* 메인 페이지 */
--font-display: 'Unbounded', 'Pretendard Variable', sans-serif;
--font-body: 'Pretendard Variable', sans-serif;

/* 교회 소개 */
--font-display-serious: 'Cormorant Garamond', 'Noto Serif KR', serif;
```

---

## ⚡ 성능 최적화

### ISR (Incremental Static Regeneration)

```typescript
// 페이지별 재검증 시간
export const revalidate = {
  home: 3600,        // 1시간
  about: 86400,      // 24시간
  sermons: 1800,     // 30분
  sermonDetail: 300, // 5분
  news: 600,         // 10분
};
```

### 적용된 최적화
- ✅ 동적 임포트 (Dynamic Imports)
- ✅ 폰트 최적화 (next/font)
- ✅ 이미지 최적화 (next/image)
- ✅ CSS 최적화

---

## 🔌 API 통신

### API 클라이언트 사용법

```typescript
import { api } from '@/lib/api';

// 데이터 조회 (Server Component)
export default async function Page() {
  const notices = await api.get('/notices');
  return <NoticeList notices={notices} />;
}

// 데이터 변경 (Client Component)
'use client';
import { api } from '@/lib/api';

async function handleSubmit(data: FormData) {
  await api.post('/notices', data);
}
```

### 환경 변수

```bash
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_SITE_NAME=성복교회
NEXT_PUBLIC_SITE_URL=https://sungbok-church.com
```

---

## 🛠️ 개발 가이드

### 컴포넌트 작성

**Server Component (기본):**
```tsx
// Server Component - 데이터 페칭 가능
export default async function SermonPage() {
  const sermons = await api.get('/sermons');
  return <SermonList sermons={sermons} />;
}
```

**Client Component (인터랙션 필요 시):**
```tsx
'use client';

import { useState } from 'react';

export default function LikeButton() {
  const [count, setCount] = useState(0);
  return <button onClick={() => setCount(c => c + 1)}>{count}</button>;
}
```

### shadcn/ui 컴포넌트 추가

```bash
npx shadcn@latest add button
npx shadcn@latest add card
npx shadcn@latest add dialog
```

---

## 🧪 테스트

```bash
# 린트 검사
npm run lint

# 타입 검사
npm run type-check

# 프로덕션 빌드 테스트
npm run build
```

---

## 📦 빌드 및 배포

```bash
# 프로덕션 빌드
npm run build

# 정적 분석
npm run analyze
```

---

## 📚 참고 문서

- [프론트엔드 디자인 전략](../docs/02-design/FRONTEND-DESIGN-STRATEGY.md)
- [ISR 전략](../docs/02-design/ISR-STRATEGY.md)
- [Next.js 가이드 (Spring 개발자용)](../docs/NEXTJS-GUIDE.md)
- [API 문서](../backend/API_DOCUMENTATION.md)

---

## ⚠️ 주의사항

- **Node.js 20+** 필수
- 환경 변수 파일 `.env.local` 필요
- 백엔드 서버 실행 상태 확인

---

Copyright © 2026 성복교회. All rights reserved.
