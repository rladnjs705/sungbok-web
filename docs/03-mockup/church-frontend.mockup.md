# Phase 3: Frontend Mockup - 성복교회 홈페이지

> **PDCA Phase**: Mockup (UI/UX 프로토타입)
>
> **Feature**: church (성복교회 홈페이지 프론트엔드)
> **Date**: 2026-02-04
> **Status**: Mockup 완료 → Design System 준비

---

## 1. Mockup Overview

### 1.1 목적
- 실제 구현 전 UI/UX 디자인 검증
- 사용자 피드백 수렴
- 디자인 시스템 구축 전 컴포넌트 프로토타이핑

### 1.2 접근 방식
**frontend-design 스킬 + Vercel React Best Practices 적용**

---

## 2. 제작된 Mockup 컴포넌트

### 2.1 Hero Section (메인 페이지)

**위치**: `frontend/src/components/home/HeroSection.tsx`

**디자인 특징:**
- 🎬 **Video background** with fallback image
- 🌈 **Gradient overlay** (Blue-Violet, animated)
- ✨ **Staggered animations** (fade-in-up with delays)
- 📊 **Quick stats** (교인/부서/선교지)
- 📱 **반응형** (Mobile/Tablet/Desktop)

**적용된 Best Practices:**
```tsx
// ✅ Bundle Size Optimization: Direct imports
import ChevronDown from 'lucide-react/dist/esm/icons/chevron-down';
import PlayCircle from 'lucide-react/dist/esm/icons/play-circle';

// ✅ Rendering Performance: Static hoisting
const ScrollIndicatorContent = () => (/* ... */);
const FALLBACK_STYLE = { backgroundImage: 'url(...)' } as const;

// ✅ Re-render Optimization: Extract static data
const STATS = [ /* ... */ ] as const;
```

**스크린샷 위치**: `mockup/screenshots/hero-section.png` (예정)

---

### 2.2 Sermon Card (설교 카드)

**위치**: `frontend/src/components/media/SermonCard.tsx`

**디자인 특징:**
- 🖼️ **Thumbnail with hover effect** (scale + overlay)
- ▶️ **Play button overlay** on hover
- 📅 **Meta information** (날짜, 설교자, 조회수)
- 🎨 **Card elevation** with shadow

**적용된 Best Practices:**
```tsx
// ✅ Direct icon imports
import Eye from 'lucide-react/dist/esm/icons/eye';

// ✅ Extract static icon component
function StatIcon({ icon: Icon, value }) { /* ... */ }
```

---

### 2.3 Sermon List (설교 목록)

**위치**: `frontend/src/components/media/SermonList.tsx`

**디자인 특징:**
- 📱 **Responsive grid** (1-2-3 columns)
- 💀 **Loading skeletons** for async loading
- 🔄 **Suspense boundaries** for streaming

**적용된 Best Practices:**
```tsx
// ✅ Server Component (default)
export async function SermonList() { /* ... */ }

// ✅ Parallel data fetching (if multiple sources)
const sermons = await getSermons();
```

---

### 2.4 Video Player (영상 플레이어)

**위치**:
- `frontend/src/components/media/VideoPlayer.tsx`
- `frontend/src/components/media/DynamicVideoPlayer.tsx`

**디자인 특징:**
- 🎥 **YouTube embed** with lazy loading
- 🔄 **Loading state** with spinner
- 🎛️ **Player controls** (YouTube native)

**적용된 Best Practices:**
```tsx
// ✅ Bundle Size Optimization: Dynamic import
const VideoPlayer = dynamic(() => import('./VideoPlayer'), {
  loading: () => <Skeleton />,
  ssr: false, // YouTube embed doesn't need SSR
});
```

---

## 3. Design Strategy 문서

### 3.1 Frontend Design Strategy
**위치**: `docs/02-design/FRONTEND-DESIGN-STRATEGY.md`

**핵심 내용:**
- **3가지 디자인 톤**: 활동적(메인), 고유성(부서), 진중함(소개)
- **Typography 전략**: Unbounded, Pretendard Variable
- **Color Palette**: Blue-Violet gradient, 부서별 고유 색상
- **Animation Strategy**: 섹션별 차별화된 인터랙션

### 3.2 Vercel React Best Practices
**위치**: `docs/02-design/VERCEL-REACT-BEST-PRACTICES.md`

**적용된 규칙:**
- ✅ **Eliminating Waterfalls** (CRITICAL)
- ✅ **Bundle Size Optimization** (CRITICAL)
- ✅ **Rendering Performance** (MEDIUM)
- ✅ **Re-render Optimization** (MEDIUM)

---

## 4. 파일 구조

```
frontend/src/
├── app/
│   ├── mockup/page.tsx              # Mockup 데모 페이지
│   └── media/
│       └── sermons/
│           └── [id]/page.tsx        # 설교 상세 페이지 (mockup)
│
├── components/
│   ├── home/
│   │   ├── HeroSection.tsx          # ✅ Mockup
│   │   └── HeroSection.backup.tsx   # 백업
│   └── media/
│       ├── SermonCard.tsx           # ✅ Mockup
│       ├── SermonList.tsx           # ✅ Mockup
│       ├── VideoPlayer.tsx          # ✅ Mockup
│       └── DynamicVideoPlayer.tsx   # ✅ Mockup
│
└── app/globals.css                  # Custom animations
```

---

## 5. Mockup 데모

### 5.1 로컬 실행
```bash
cd frontend
npm run dev
```

### 5.2 데모 URL
- **Hero Section**: http://localhost:3000/mockup
- **Sermon Detail**: http://localhost:3000/media/sermons/1

---

## 6. 사용자 피드백 (예정)

### 6.1 Hero Section
- [ ] Video background 로딩 속도
- [ ] Mobile 반응형 확인
- [ ] CTA 버튼 위치와 크기
- [ ] 통계 숫자 정확성

### 6.2 Sermon List
- [ ] 카드 레이아웃 선호도
- [ ] Thumbnail 비율 (16:9 vs 4:3)
- [ ] 로딩 skeleton 자연스러움

### 6.3 Video Player
- [ ] YouTube embed vs 자체 플레이어
- [ ] 재생 버튼 위치
- [ ] Related sermons 필요 여부

---

## 7. 다음 단계: Phase 5 (Design System)

### 7.1 Tailwind Config 설정
```typescript
// tailwind.config.ts
export default {
  theme: {
    extend: {
      colors: {
        'church-blue': { /* ... */ },
        'church-violet': { /* ... */ },
        // 부서별 색상
        'sunday-school': '#f472b6',
        'youth': '#06b6d4',
        'adult': '#059669',
      },
      fontFamily: {
        display: ['Unbounded', 'sans-serif'],
        body: ['Pretendard Variable', 'sans-serif'],
      },
    },
  },
};
```

### 7.2 shadcn/ui 커스터마이징
- Button variants (primary, secondary, outline)
- Card with different elevations
- Input with validation states

### 7.3 디자인 토큰 정리
- Spacing scale
- Border radius
- Shadow levels
- Animation durations

---

## 8. Mockup vs 실제 구현 차이

### 8.1 Mockup (현재)
- ✅ 정적 데이터 (하드코딩)
- ✅ 기본 컴포넌트 구조
- ✅ 디자인 검증용

### 8.2 Phase 6 (실제 구현)
- ⏭️ Backend API 연동
- ⏭️ SWR for data fetching
- ⏭️ Form validation
- ⏭️ Error handling
- ⏭️ Authentication

---

## 9. 참고 문서

- **Design Strategy**: `docs/02-design/FRONTEND-DESIGN-STRATEGY.md`
- **Best Practices**: `docs/02-design/VERCEL-REACT-BEST-PRACTICES.md`
- **Menu Structure**: `docs/02-design/MENU-STRUCTURE.md`

---

## 10. Mockup 체크리스트

### Design
- [x] ✅ Hero Section 디자인
- [x] ✅ Sermon Card 디자인
- [x] ✅ Sermon List 디자인
- [x] ✅ Video Player 디자인
- [ ] ⬜ Ministry Cards 디자인 (부서별)
- [ ] ⬜ Pastor Profile 디자인 (진중한 톤)
- [ ] ⬜ Notice Board 디자인

### Performance
- [x] ✅ Bundle size optimization (Direct imports)
- [x] ✅ Static JSX hoisting
- [x] ✅ Dynamic imports for heavy components
- [x] ✅ Loading skeletons

### Responsive
- [x] ✅ Mobile (< 768px)
- [x] ✅ Tablet (768px - 1024px)
- [x] ✅ Desktop (> 1024px)

### Accessibility
- [x] ✅ Semantic HTML
- [x] ✅ Alt text for images
- [ ] ⬜ Keyboard navigation testing
- [ ] ⬜ Screen reader testing

---

**Mockup 완료일**: 2026-02-04
**다음 Phase**: Phase 5 (Design System)
**예상 소요 시간**: 1-2일
