# Vercel React Best Practices - 프로젝트 적용 가이드

> **출처**: [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills/tree/main/skills/react-best-practices)
>
> **버전**: 1.0.0
> **적용일**: 2026-02-04
> **프로젝트**: 성복교회 홈페이지

---

## 개요

Vercel Engineering팀이 작성한 **57개의 React/Next.js 성능 최적화 규칙**을 성복교회 프로젝트에 적용합니다.

---

## 우선순위별 규칙 (8 Categories)

| 순위 | 카테고리 | 영향도 | 적용 우선순위 |
|------|---------|--------|-------------|
| 1 | Eliminating Waterfalls | **CRITICAL** | 🔴 최우선 |
| 2 | Bundle Size Optimization | **CRITICAL** | 🔴 최우선 |
| 3 | Server-Side Performance | **HIGH** | 🟠 높음 |
| 4 | Client-Side Data Fetching | **MEDIUM-HIGH** | 🟡 중상 |
| 5 | Re-render Optimization | **MEDIUM** | 🟢 중간 |
| 6 | Rendering Performance | **MEDIUM** | 🟢 중간 |
| 7 | JavaScript Performance | **LOW-MEDIUM** | 🔵 낮음 |
| 8 | Advanced Patterns | **LOW** | 🔵 낮음 |

---

## 1. Eliminating Waterfalls (CRITICAL)

### ✅ 적용 규칙

#### 1.1 Promise.all() for Independent Operations

```tsx
// ❌ Bad: Sequential (3 round trips)
const user = await fetchUser();
const posts = await fetchPosts();
const comments = await fetchComments();

// ✅ Good: Parallel (1 round trip)
const [user, posts, comments] = await Promise.all([
  fetchUser(),
  fetchPosts(),
  fetchComments(),
]);
```

**프로젝트 적용 예시:**
```tsx
// 설교 목록 페이지
const [sermons, categories, tags] = await Promise.all([
  api.get('/sermons'),
  api.get('/sermons/categories'),
  api.get('/sermons/tags'),
]);
```

#### 1.2 Defer Await Until Needed

```tsx
// ❌ Bad: Always fetches permissions
async function updateResource(resourceId: string, userId: string) {
  const permissions = await fetchPermissions(userId);
  const resource = await getResource(resourceId);

  if (!resource) {
    return { error: 'Not found' };
  }
  // ...
}

// ✅ Good: Fetches only when needed
async function updateResource(resourceId: string, userId: string) {
  const resource = await getResource(resourceId);

  if (!resource) {
    return { error: 'Not found' }; // Early return
  }

  const permissions = await fetchPermissions(userId); // Defer
  // ...
}
```

---

## 2. Bundle Size Optimization (CRITICAL)

### ✅ 적용 규칙

#### 2.1 Avoid Barrel File Imports

```tsx
// ❌ Bad: Barrel import (imports entire module)
import { Button, Card, Input } from '@/components/ui';

// ✅ Good: Direct import (tree-shakable)
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
```

**lucide-react 아이콘:**
```tsx
// ❌ Bad: 전체 아이콘 라이브러리 로드
import { PlayCircle, ChevronDown } from 'lucide-react';

// ✅ Good: 개별 아이콘만 로드
import PlayCircle from 'lucide-react/dist/esm/icons/play-circle';
import ChevronDown from 'lucide-react/dist/esm/icons/chevron-down';
```

#### 2.2 Dynamic Imports for Heavy Components

```tsx
// ❌ Bad: Always loads heavy component
import VideoPlayer from '@/components/VideoPlayer';

// ✅ Good: Lazy load when needed
const VideoPlayer = dynamic(() => import('@/components/VideoPlayer'), {
  loading: () => <Skeleton />,
  ssr: false,
});
```

**프로젝트 적용 예시:**
```tsx
// 설교 영상 플레이어 (heavy component)
const SermonPlayer = dynamic(() => import('@/components/media/SermonPlayer'), {
  loading: () => <div>로딩 중...</div>,
  ssr: false,
});
```

---

## 3. Server-Side Performance (HIGH)

### ✅ 적용 규칙

#### 3.1 Use React.cache() for Per-Request Deduplication

```tsx
// ✅ Good: Automatic deduplication within request
import { cache } from 'react';

const getUser = cache(async (id: string) => {
  return await db.user.findUnique({ where: { id } });
});
```

**프로젝트 적용 예시:**
```tsx
// 여러 컴포넌트에서 같은 설교 데이터 요청 시 자동 중복 제거
import { cache } from 'react';

const getSermon = cache(async (id: string) => {
  return await api.get(`/sermons/${id}`);
});
```

#### 3.2 Parallel Data Fetching with Component Composition

```tsx
// ✅ Good: Components fetch in parallel
export default function Page() {
  return (
    <>
      <Suspense fallback={<Skeleton />}>
        <UserProfile />
      </Suspense>
      <Suspense fallback={<Skeleton />}>
        <UserPosts />
      </Suspense>
    </>
  );
}
```

---

## 4. Client-Side Data Fetching (MEDIUM-HIGH)

### ✅ 적용 규칙

#### 4.1 Use SWR for Automatic Deduplication

```tsx
// ✅ Good: SWR handles caching and deduplication
import useSWR from 'swr';

function SermonList() {
  const { data, error } = useSWR('/api/sermons', fetcher);

  if (error) return <div>Failed to load</div>;
  if (!data) return <div>Loading...</div>;

  return <ul>{data.map(sermon => <li>{sermon.title}</li>)}</ul>;
}
```

#### 4.2 Use Passive Event Listeners for Scroll

```tsx
// ❌ Bad: Blocking scroll
window.addEventListener('scroll', handleScroll);

// ✅ Good: Non-blocking scroll
window.addEventListener('scroll', handleScroll, { passive: true });
```

---

## 5. Re-render Optimization (MEDIUM)

### ✅ 적용 규칙

#### 5.1 Hoist Static JSX Elements

```tsx
// ❌ Bad: Re-creates on every render
function Component() {
  return (
    <div>
      <Icon className="icon" />
    </div>
  );
}

// ✅ Good: Extract static JSX
const StaticIcon = () => <Icon className="icon" />;

function Component() {
  return (
    <div>
      <StaticIcon />
    </div>
  );
}
```

**Hero Section 적용 예시:**
```tsx
// ✅ Good: Static components hoisted
const ScrollIndicator = () => (
  <div className="flex flex-col items-center gap-2">
    <span>스크롤</span>
    <ChevronDown />
  </div>
);

export function HeroSection() {
  return (
    <section>
      {/* ... */}
      <ScrollIndicator />
    </section>
  );
}
```

#### 5.2 Use Functional setState Updates

```tsx
// ❌ Bad: Depends on current state
const increment = () => setCount(count + 1);

// ✅ Good: Functional update (stable callback)
const increment = () => setCount(prev => prev + 1);
```

---

## 6. Rendering Performance (MEDIUM)

### ✅ 적용 규칙

#### 6.1 CSS content-visibility for Long Lists

```css
/* ✅ Good: Only renders visible items */
.list-item {
  content-visibility: auto;
  contain-intrinsic-size: 200px;
}
```

**프로젝트 적용 예시:**
```tsx
// 설교 아카이브 (긴 목록)
<div className="sermon-list">
  {sermons.map(sermon => (
    <div
      key={sermon.id}
      className="sermon-item"
      style={{ contentVisibility: 'auto', containIntrinsicSize: '200px' }}
    >
      {sermon.title}
    </div>
  ))}
</div>
```

---

## 7. JavaScript Performance (LOW-MEDIUM)

### ✅ 적용 규칙

#### 7.1 Use Set/Map for O(1) Lookups

```tsx
// ❌ Bad: O(n) lookup
const hasId = ids.includes(targetId);

// ✅ Good: O(1) lookup
const idSet = new Set(ids);
const hasId = idSet.has(targetId);
```

#### 7.2 Cache Property Access in Loops

```tsx
// ❌ Bad: Repeated property access
for (let i = 0; i < items.length; i++) {
  console.log(items[i].data.nested.value);
}

// ✅ Good: Cache property
for (let i = 0; i < items.length; i++) {
  const value = items[i].data.nested.value;
  console.log(value);
}
```

---

## 8. Advanced Patterns (LOW)

### ✅ 적용 규칙

#### 8.1 Store Event Handlers in Refs

```tsx
// ✅ Good: Stable event handler
function Component({ onChange }: { onChange: (value: string) => void }) {
  const onChangeRef = useRef(onChange);

  useEffect(() => {
    onChangeRef.current = onChange;
  }, [onChange]);

  const handleChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    onChangeRef.current(e.target.value);
  }, []);

  return <input onChange={handleChange} />;
}
```

---

## 프로젝트 적용 체크리스트

### Phase 1: CRITICAL (최우선)
- [x] ✅ Hero Section: Direct imports 적용
- [x] ✅ Hero Section: Static JSX hoisting
- [ ] ⬜ API 호출: Promise.all() 적용
- [ ] ⬜ 설교 영상: Dynamic imports 적용

### Phase 2: HIGH (높음)
- [ ] ⬜ Server Components: React.cache() 적용
- [ ] ⬜ Parallel fetching with Suspense

### Phase 3: MEDIUM (중간)
- [ ] ⬜ SWR for client-side data fetching
- [ ] ⬜ Re-render optimization with useMemo
- [ ] ⬜ Long lists: content-visibility 적용

### Phase 4: LOW (낮음)
- [ ] ⬜ Set/Map for lookups
- [ ] ⬜ Advanced patterns (refs, useLatest)

---

## 참고 문서

- **Full Guide**: `~/.claude/plugins/marketplaces/vercel-labs-agent-skills/skills/react-best-practices/AGENTS.md`
- **Individual Rules**: `~/.claude/plugins/marketplaces/vercel-labs-agent-skills/skills/react-best-practices/rules/`

---

**작성일**: 2026-02-04
**마지막 업데이트**: 2026-02-04
**다음 리뷰**: Hero Section 최적화 후
