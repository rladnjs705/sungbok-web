# 성복교회 홈페이지 Frontend Design Strategy

> **Design Philosophy**: "활동적인 역동성 + 부서별 고유성 + 진중한 신뢰감"
>
> **Project**: 성복교회 홈페이지
> **Level**: Dynamic (Fullstack with BaaS)
> **Framework**: Next.js 16.1 + Tailwind CSS + shadcn/ui
> **Date**: 2026-02-04

---

## 1. Design Thinking Process

### 1.1 Purpose (목적)
- **문제**: 기존 교회 웹사이트는 정적이고 단조로움
- **해결**: 각 섹션의 목적에 맞는 독특한 분위기로 차별화
- **사용자**: 교인, 새가족, 온라인 예배 참여자

### 1.2 Tone (미적 방향)

성복교회 홈페이지는 **3가지 Tone**을 가집니다:

| 섹션 | Tone | 키워드 | 느낌 |
|------|------|--------|------|
| **메인 페이지** | 활동적/역동적 | Energetic, Vibrant, Modern | 생동감 넘치는 교회 공동체 |
| **부서별 페이지** | 고유성/개성 | Unique, Playful, Diverse | 각 부서의 정체성과 특색 |
| **교회/목사님 소개** | 진중한/신뢰감 | Trustworthy, Elegant, Refined | 전통과 권위, 신뢰와 안정감 |

### 1.3 Differentiation (차별화 포인트)

**"하나의 웹사이트, 세 가지 감정"**

- 일반적인 교회 웹사이트는 전체가 동일한 디자인 톤
- 성복교회는 **섹션별로 다른 디자인 언어** 사용
- 사용자가 페이지를 이동할 때마다 **새로운 경험**

---

## 2. Design System Foundation

### 2.1 Typography Strategy

**❌ 피해야 할 폰트:**
- Inter, Roboto, Arial (일반적이고 무난함)
- Gothic 계열 남발 (교회 사이트의 클리셰)

**✅ 추천 폰트 조합:**

#### 메인 페이지 (활동적)
```css
/* Display Font (헤더/타이틀) */
--font-display: 'Unbounded', 'Pretendard Variable', sans-serif;
/* 기하학적이고 모던한 느낌, 활동성 표현 */

/* Body Font (본문) */
--font-body: 'Pretendard Variable', 'Inter', sans-serif;
/* 가독성 좋은 본문용, Variable Font로 다양한 굵기 */
```

#### 부서별 페이지 (고유성)
```css
/* 주일학교 - 부드럽고 둥근 느낌 */
--font-sunday-school: 'Fredoka', 'Noto Sans KR', sans-serif;

/* 청년부 - 모던하고 스타일리시 */
--font-youth: 'Space Grotesk', 'Noto Sans KR', sans-serif;

/* 장년부 - 안정적이고 전통적 */
--font-adult: 'Crimson Pro', 'Noto Serif KR', serif;
```

#### 교회/목사님 소개 (진중함)
```css
/* Display Font (헤더) */
--font-display-serious: 'Cormorant Garamond', 'Noto Serif KR', serif;
/* 세리프체로 권위와 전통 표현 */

/* Body Font (본문) */
--font-body-serious: 'Lora', 'Noto Serif KR', serif;
/* 읽기 편한 세리프체 */
```

### 2.2 Color Palette

#### 메인 페이지 (활동적)
```css
:root {
  /* Primary - 생동감 있는 그라디언트 */
  --color-primary: #3b82f6; /* Blue 500 */
  --color-primary-dark: #1e40af; /* Blue 800 */
  --color-gradient-start: #3b82f6;
  --color-gradient-end: #8b5cf6; /* Violet 500 */

  /* Accent - 강렬한 오렌지 악센트 */
  --color-accent: #f97316; /* Orange 500 */

  /* Background */
  --color-bg: #ffffff;
  --color-bg-secondary: #f8fafc;
}
```

#### 부서별 페이지 (고유성)
```css
/* 각 부서마다 고유한 Primary Color */
--color-sunday-school: #f472b6; /* Pink 400 - 밝고 귀여움 */
--color-youth: #06b6d4; /* Cyan 500 - 신선하고 활기참 */
--color-adult: #059669; /* Emerald 600 - 안정적이고 성숙함 */
--color-mission: #dc2626; /* Red 600 - 열정적이고 강렬함 */
```

#### 교회/목사님 소개 (진중함)
```css
:root {
  /* Primary - 고급스러운 네이비/골드 */
  --color-primary-serious: #1e293b; /* Slate 800 */
  --color-accent-gold: #d97706; /* Amber 600 */

  /* Background - 고급스러운 크림/베이지 */
  --color-bg-serious: #fafaf9; /* Stone 50 */
  --color-bg-secondary-serious: #f5f5f4; /* Stone 100 */
}
```

### 2.3 Spatial Composition

#### 메인 페이지 - 역동적 레이아웃
- **Hero Section**: Full-screen video background with diagonal overlay
- **비대칭 그리드**: Cards with varying heights (Masonry layout)
- **Parallax Scrolling**: 스크롤 시 배경 이미지 움직임
- **Overlap Elements**: 섹션 간 겹침으로 역동성 표현

#### 부서별 페이지 - 각 부서별 고유 레이아웃
- **주일학교**: Curved borders, rounded corners, playful shapes
- **청년부**: Bold asymmetry, diagonal dividers, modern grid
- **장년부**: Traditional centered layout, generous whitespace

#### 교회/목사님 소개 - 전통적이고 균형 잡힌 레이아웃
- **중앙 정렬**: Centered content with max-width container
- **Generous Whitespace**: 여유로운 여백으로 고급스러움
- **Classic Grid**: 2-column layout (image + text)

---

## 3. Animation & Motion Strategy

### 3.1 메인 페이지 - 활동적인 애니메이션

**Page Load Animation (Staggered Reveal):**
```css
/* 순차적 등장 애니메이션 */
.hero-title {
  animation: fadeInUp 0.8s ease-out 0s;
}

.hero-subtitle {
  animation: fadeInUp 0.8s ease-out 0.2s;
}

.hero-cta {
  animation: fadeInUp 0.8s ease-out 0.4s;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
```

**Scroll-triggered Animations:**
- Cards slide in from sides
- Numbers count-up animation (통계 섹션)
- Background gradient shifts on scroll

**Hover Effects:**
```css
.sermon-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.sermon-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}
```

### 3.2 부서별 페이지 - 고유한 인터랙션

#### 주일학교 - 부드럽고 귀여운 애니메이션
```css
.ministry-card {
  transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55); /* Bounce */
}

.ministry-card:hover {
  transform: scale(1.05) rotate(2deg);
}
```

#### 청년부 - 빠르고 스타일리시한 애니메이션
```css
.youth-card {
  transition: all 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94); /* Ease-out-quad */
}

.youth-card:hover {
  transform: skewY(-2deg) translateX(10px);
}
```

#### 장년부 - 부드럽고 점진적인 애니메이션
```css
.adult-card {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.adult-card:hover {
  opacity: 0.9;
  transform: scale(1.02);
}
```

### 3.3 교회/목사님 소개 - 절제된 애니메이션

**Minimal Motion:**
```css
/* 부드러운 페이드만 사용 */
.intro-text {
  animation: fadeIn 1.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Hover 시 미묘한 밝기 변화 */
.pastor-image {
  transition: filter 0.4s ease;
}

.pastor-image:hover {
  filter: brightness(1.05);
}
```

---

## 4. Background & Visual Details

### 4.1 메인 페이지

**Hero Section:**
```css
/* 역동적인 그라디언트 메시 */
background: linear-gradient(
  135deg,
  rgba(59, 130, 246, 0.8),
  rgba(139, 92, 246, 0.6)
);

/* Animated grain overlay */
.grain-overlay {
  background-image: url('/textures/grain.png');
  opacity: 0.03;
  animation: grain 8s steps(10) infinite;
}

@keyframes grain {
  0%, 100% { transform: translate(0, 0); }
  10% { transform: translate(-5%, -10%); }
  20% { transform: translate(-15%, 5%); }
  30% { transform: translate(7%, -25%); }
  40% { transform: translate(-5%, 25%); }
  50% { transform: translate(-15%, 10%); }
  60% { transform: translate(15%, 0%); }
  70% { transform: translate(0%, 15%); }
  80% { transform: translate(3%, 35%); }
  90% { transform: translate(-10%, 10%); }
}
```

**Section Dividers:**
```jsx
<svg className="section-divider" viewBox="0 0 1440 120">
  <path
    fill="currentColor"
    d="M0,64L80,58.7C160,53,320,43,480,48C640,53,800,75,960,80C1120,85,1280,75,1360,69.3L1440,64L1440,120L1360,120C1280,120,1120,120,960,120C800,120,640,120,480,120C320,120,160,120,80,120L0,120Z"
  />
</svg>
```

### 4.2 부서별 페이지

**주일학교 - 귀여운 일러스트 배경:**
```css
.sunday-school-bg {
  background-image: url('/patterns/cute-clouds.svg');
  background-size: 200px;
  opacity: 0.1;
}
```

**청년부 - 기하학적 패턴:**
```css
.youth-bg {
  background-image:
    repeating-linear-gradient(45deg, transparent, transparent 35px, rgba(6, 182, 212, 0.05) 35px, rgba(6, 182, 212, 0.05) 70px);
}
```

**장년부 - 미묘한 텍스처:**
```css
.adult-bg {
  background: linear-gradient(
    to bottom,
    #fafaf9,
    #f5f5f4
  );
  background-image: url('/textures/paper.png');
}
```

### 4.3 교회/목사님 소개

**고급스러운 배경:**
```css
.about-section {
  background: linear-gradient(
    to bottom,
    #fafaf9 0%,
    #f5f5f4 100%
  );

  /* Subtle vignette effect */
  box-shadow: inset 0 0 100px rgba(0, 0, 0, 0.03);
}

/* Golden accent border */
.pastor-card {
  border: 2px solid #d97706;
  box-shadow: 0 4px 20px rgba(217, 119, 6, 0.1);
}
```

---

## 5. Component Design Guidelines

### 5.1 메인 페이지 Components

#### Hero Section (활동적)
```jsx
<section className="hero min-h-screen relative overflow-hidden">
  {/* Video Background */}
  <video autoPlay muted loop className="absolute inset-0 w-full h-full object-cover">
    <source src="/videos/church-intro.mp4" type="video/mp4" />
  </video>

  {/* Gradient Overlay */}
  <div className="absolute inset-0 bg-gradient-to-br from-blue-500/80 to-violet-500/60" />

  {/* Content */}
  <div className="relative z-10 container mx-auto px-4 flex items-center justify-center min-h-screen">
    <div className="text-center text-white space-y-6">
      <h1 className="text-6xl md:text-8xl font-display font-bold animate-fadeInUp">
        성복교회에<br />오신 것을 환영합니다
      </h1>
      <p className="text-xl md:text-2xl font-light animate-fadeInUp [animation-delay:0.2s]">
        함께 예배하고, 배우고, 성장하는 공동체
      </p>
      <div className="flex gap-4 justify-center animate-fadeInUp [animation-delay:0.4s]">
        <button className="btn-primary">온라인 예배 보기</button>
        <button className="btn-secondary">교회 소개</button>
      </div>
    </div>
  </div>

  {/* Scroll Indicator */}
  <div className="absolute bottom-8 left-1/2 -translate-x-1/2 animate-bounce">
    <ChevronDown className="w-8 h-8 text-white" />
  </div>
</section>
```

#### Latest Sermon Card (역동적)
```jsx
<div className="sermon-card group relative overflow-hidden rounded-2xl shadow-lg transition-all duration-300 hover:-translate-y-2 hover:shadow-2xl">
  {/* Thumbnail with Overlay */}
  <div className="relative aspect-video overflow-hidden">
    <Image
      src={sermon.thumbnail}
      alt={sermon.title}
      fill
      className="object-cover transition-transform duration-500 group-hover:scale-110"
    />
    {/* Play Button Overlay */}
    <div className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity">
      <PlayCircle className="w-16 h-16 text-white" />
    </div>
  </div>

  {/* Content */}
  <div className="p-6 bg-white">
    <div className="flex items-center gap-2 text-sm text-gray-500 mb-2">
      <Calendar className="w-4 h-4" />
      <span>{sermon.date}</span>
    </div>
    <h3 className="text-xl font-bold text-gray-900 mb-2 line-clamp-2">
      {sermon.title}
    </h3>
    <p className="text-gray-600 line-clamp-2">
      {sermon.description}
    </p>

    {/* Stats */}
    <div className="flex gap-4 mt-4 text-sm text-gray-500">
      <span className="flex items-center gap-1">
        <Eye className="w-4 h-4" />
        {sermon.viewCount}
      </span>
      <span className="flex items-center gap-1">
        <ThumbsUp className="w-4 h-4" />
        {sermon.likeCount}
      </span>
    </div>
  </div>
</div>
```

### 5.2 부서별 페이지 Components

#### 주일학교 Card (귀여운 느낌)
```jsx
<div className="sunday-school-card bg-pink-50 rounded-[2rem] p-8 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 hover:rotate-2">
  <div className="flex items-center gap-4 mb-4">
    <div className="w-16 h-16 bg-pink-400 rounded-full flex items-center justify-center">
      <Heart className="w-8 h-8 text-white" />
    </div>
    <div>
      <h3 className="text-2xl font-sunday-school font-bold text-pink-700">
        유치부
      </h3>
      <p className="text-pink-600">4-7세</p>
    </div>
  </div>

  <p className="text-gray-700 leading-relaxed">
    사랑이 가득한 유치부에서 친구들과 함께 하나님을 배워요!
  </p>

  <button className="mt-6 w-full bg-pink-400 text-white py-3 rounded-full font-bold hover:bg-pink-500 transition-colors">
    자세히 보기
  </button>
</div>
```

#### 청년부 Card (스타일리시)
```jsx
<div className="youth-card relative overflow-hidden bg-gradient-to-br from-cyan-500 to-blue-600 rounded-lg shadow-lg hover:shadow-2xl transition-all duration-200 hover:skew-y-[-2deg]">
  {/* Diagonal Background Pattern */}
  <div className="absolute inset-0 opacity-10">
    <div className="absolute inset-0 bg-[repeating-linear-gradient(45deg,transparent,transparent_35px,rgba(255,255,255,0.3)_35px,rgba(255,255,255,0.3)_70px)]" />
  </div>

  <div className="relative p-8 text-white">
    <h3 className="text-3xl font-youth font-bold mb-2">청년부</h3>
    <p className="text-cyan-100 mb-4">20-30대</p>
    <p className="text-white/90 leading-relaxed">
      신앙과 삶이 하나 되는 청년 공동체
    </p>

    <button className="mt-6 bg-white text-cyan-600 px-6 py-2 rounded-md font-bold hover:bg-cyan-50 transition-colors">
      더 알아보기 →
    </button>
  </div>
</div>
```

### 5.3 교회/목사님 소개 Components

#### Pastor Profile (진중하고 고급스러움)
```jsx
<section className="pastor-section py-20 bg-gradient-to-b from-stone-50 to-stone-100">
  <div className="container mx-auto px-4 max-w-5xl">
    {/* Title */}
    <div className="text-center mb-16">
      <h2 className="text-5xl font-display-serious font-bold text-slate-800 mb-4">
        담임목사 소개
      </h2>
      <div className="w-24 h-1 bg-amber-600 mx-auto" />
    </div>

    {/* Content Grid */}
    <div className="grid md:grid-cols-2 gap-12 items-center">
      {/* Image */}
      <div className="relative">
        <div className="aspect-[3/4] relative overflow-hidden rounded-lg shadow-2xl border-2 border-amber-600">
          <Image
            src="/images/pastor.jpg"
            alt="담임목사"
            fill
            className="object-cover transition-all duration-500 hover:brightness-105"
          />
        </div>

        {/* Decorative Frame */}
        <div className="absolute -inset-4 border-2 border-amber-600/20 rounded-lg -z-10" />
      </div>

      {/* Bio */}
      <div className="space-y-6">
        <div>
          <h3 className="text-4xl font-body-serious font-bold text-slate-800 mb-2">
            홍길동 목사
          </h3>
          <p className="text-xl text-amber-700 font-medium">
            성복교회 담임목사
          </p>
        </div>

        <div className="border-l-4 border-amber-600 pl-6">
          <p className="text-lg text-gray-700 leading-relaxed font-body-serious">
            "하나님의 말씀으로 세워지고, 사랑으로 하나 되며,
            세상을 향해 빛과 소금의 역할을 감당하는 교회"
          </p>
        </div>

        <div className="space-y-3 text-gray-600">
          <p className="flex items-start gap-2">
            <span className="font-semibold text-slate-700">학력:</span>
            <span>○○신학대학원 목회학 석사 (M.Div)</span>
          </p>
          <p className="flex items-start gap-2">
            <span className="font-semibold text-slate-700">경력:</span>
            <span>성복교회 담임목사 (2015 ~ 현재)</span>
          </p>
          <p className="flex items-start gap-2">
            <span className="font-semibold text-slate-700">저서:</span>
            <span>"믿음의 여정" 외 다수</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</section>
```

---

## 6. Responsive Design Strategy

### 6.1 Breakpoints
```css
/* Mobile First Approach */
--breakpoint-sm: 640px;   /* Small devices */
--breakpoint-md: 768px;   /* Tablets */
--breakpoint-lg: 1024px;  /* Laptops */
--breakpoint-xl: 1280px;  /* Desktops */
--breakpoint-2xl: 1536px; /* Large screens */
```

### 6.2 Mobile Considerations

#### 메인 페이지
- Hero video → Static image on mobile (성능)
- Grid 3 columns → 1 column on mobile
- Font sizes scale down (text-6xl → text-4xl)

#### 부서별 페이지
- Horizontal scroll for ministry cards (carousel)
- Simplified animations on mobile

#### 교회 소개
- 2-column layout → 1-column stack on mobile
- Image aspect ratio changes (4:3 → 1:1)

---

## 7. Performance Optimization

### 7.1 Image Optimization
```jsx
// Next.js Image with blur placeholder
<Image
  src={sermon.thumbnail}
  alt={sermon.title}
  width={800}
  height={450}
  placeholder="blur"
  blurDataURL={sermon.blurHash}
  priority={index < 3} // Only first 3 images
/>
```

### 7.2 Code Splitting
```jsx
// Lazy load heavy components
const VideoPlayer = dynamic(() => import('@/components/VideoPlayer'), {
  loading: () => <Skeleton className="aspect-video" />,
  ssr: false,
});
```

### 7.3 Animation Performance
```css
/* Use transform and opacity (GPU-accelerated) */
.card {
  transform: translateY(0);
  opacity: 1;
  will-change: transform, opacity;
}

/* Avoid animating width, height, margin */
```

---

## 8. Accessibility (a11y)

### 8.1 Color Contrast
- WCAG AA 기준: 4.5:1 이상 (본문), 3:1 이상 (큰 텍스트)
- 배경/텍스트 대비 검증 필수

### 8.2 Keyboard Navigation
```jsx
// Focus styles
<button className="focus:outline-none focus:ring-4 focus:ring-blue-500/50">
  예배 보기
</button>
```

### 8.3 Screen Reader Support
```jsx
<button aria-label="설교 영상 재생">
  <PlayCircle />
</button>

<nav aria-label="주 내비게이션">
  <ul>...</ul>
</nav>
```

---

## 9. Implementation Checklist

### Phase 1: 디자인 시스템 구축
- [ ] Tailwind config 설정 (colors, fonts, animations)
- [ ] Global CSS variables 정의
- [ ] Typography styles 정의
- [ ] 공통 컴포넌트 제작 (Button, Card, Input 등)

### Phase 2: 메인 페이지
- [ ] Hero Section (비디오 배경)
- [ ] Latest Sermon Section
- [ ] Upcoming Events Section
- [ ] Quick Links Section
- [ ] Footer

### Phase 3: 부서별 페이지
- [ ] Ministry List Page
- [ ] Sunday School Page (귀여운 디자인)
- [ ] Youth Page (스타일리시한 디자인)
- [ ] Adult Page (전통적인 디자인)

### Phase 4: 교회/목사님 소개
- [ ] Church Introduction Page
- [ ] Pastor Profile Page
- [ ] Church History Page
- [ ] Location Page (지도 API)

### Phase 5: 미디어 섹션
- [ ] Sermon Archive (필터링)
- [ ] Video Gallery
- [ ] Live Streaming Page

### Phase 6: 커뮤니티
- [ ] Notice Board
- [ ] Bulletin Download
- [ ] Photo Gallery (Lightbox)
- [ ] Testimony Board
- [ ] Prayer Request Form

---

## 10. Next Steps

### 즉시 시작 가능한 작업:
1. **Tailwind Config 설정** - 색상, 폰트, 애니메이션 정의
2. **디자인 토큰 생성** - CSS variables로 테마 시스템 구축
3. **Hero Section 목업** - 메인 페이지 첫인상 제작
4. **Button/Card 컴포넌트** - shadcn/ui 기반 커스터마이징

### 디자인 검증:
- [ ] Figma/Sketch에서 시안 제작 (선택사항)
- [ ] 프로토타입으로 사용자 피드백
- [ ] 반응형 디자인 테스트 (Mobile, Tablet, Desktop)

---

**작성일**: 2026-02-04
**작성자**: Claude Code with frontend-design skill
**참고 문서**: MENU-STRUCTURE.md, church-phase4.analysis.md
**다음 단계**: Phase 3 Mockup 제작 → UI 컴포넌트 구현
