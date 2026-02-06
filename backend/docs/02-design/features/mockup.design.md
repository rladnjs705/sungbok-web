# Phase 3: UI/UX Mockup & Prototype - 설계서

> **Feature**: 성북교회 홈페이지 UI/UX 목업 및 프로토타입
>
> **Phase**: Phase 3 (Mockup & Prototype)
> **Created**: 2026-02-04
> **Status**: Design
> **Version**: 1.0
> **Based On**: mockup.plan.md

---

## 1. 설계 개요 (Design Overview)

### 1.1 설계 목표

Phase 4 백엔드 API(150+ 엔드포인트)를 기반으로 **HTML/CSS/JavaScript 인터랙티브 프로토타입**을 제작하여:
1. ✅ 사용자 경험(UX) 검증
2. ✅ 디자인 시스템 정립
3. ✅ Phase 6 구현 가이드 제공
4. ✅ 이해관계자 피드백 수집

### 1.2 기술 스택

```yaml
Prototype Stack:
  - HTML5: Semantic markup
  - CSS3: Variables, Flexbox, Grid, Animations
  - JavaScript: Vanilla JS (ES6+)
  - Optional: Alpine.js (lightweight reactivity)

Design Tools:
  - Figma: Wireframe & Design System (Optional)
  - VS Code: Code editing
  - Live Server: Local development

Assets:
  - Icons: Heroicons, Lucide Icons
  - Fonts: Noto Sans KR (Google Fonts)
  - Images: Unsplash (mockup images)
```

### 1.3 디렉토리 구조

```
/prototypes/
├── index.html                    # 홈페이지
├── pages/
│   ├── notices/
│   │   ├── list.html             # 공지사항 목록
│   │   ├── detail.html           # 공지사항 상세
│   │   └── search.html           # 공지사항 검색
│   ├── sermons/
│   │   ├── list.html             # 설교 목록
│   │   ├── detail.html           # 설교 상세
│   │   └── search.html           # 설교 검색
│   ├── worship/
│   │   ├── schedule.html         # 예배 일정
│   │   └── live.html             # 라이브 예배
│   ├── galleries/
│   │   ├── image-list.html       # 이미지 갤러리 목록
│   │   ├── image-detail.html     # 이미지 갤러리 상세
│   │   ├── video-list.html       # 영상 갤러리 목록
│   │   └── youtube-playlists.html # YouTube 재생목록
│   ├── community/
│   │   ├── testimonies.html      # 간증 목록
│   │   ├── testimony-detail.html # 간증 상세
│   │   ├── prayer-requests.html  # 기도 제목 목록
│   │   └── prayer-form.html      # 기도 제목 등록
│   ├── about/
│   │   ├── church.html           # 교회 소개
│   │   ├── pastors.html          # 목회자 소개
│   │   ├── staff.html            # 교직원 소개
│   │   ├── ministries.html       # 사역 소개
│   │   ├── bulletins.html        # 주보 다운로드
│   │   ├── donations.html        # 헌금 안내
│   │   └── missions.html         # 선교 활동
│   └── admin/
│       ├── dashboard.html        # 관리자 대시보드
│       ├── content-form.html     # 콘텐츠 등록/수정
│       └── approval.html         # 승인 관리
├── css/
│   ├── reset.css                 # CSS 리셋
│   ├── variables.css             # CSS 변수 (디자인 시스템)
│   ├── components.css            # 재사용 컴포넌트
│   ├── layout.css                # 레이아웃 (Grid, Container)
│   ├── navigation.css            # 네비게이션 스타일
│   ├── pages.css                 # 페이지별 스타일
│   └── responsive.css            # 반응형 미디어 쿼리
├── js/
│   ├── navigation.js             # 네비게이션 (햄버거 메뉴, Mega Menu)
│   ├── modal.js                  # 모달 (Lightbox, 확인 다이얼로그)
│   ├── slider.js                 # 슬라이더 (이미지 캐러셀)
│   ├── tabs.js                   # 탭 (카테고리 필터)
│   ├── form-validation.js        # 폼 검증
│   └── utils.js                  # 유틸리티 함수
├── assets/
│   ├── images/
│   │   ├── hero/                 # Hero 이미지
│   │   ├── placeholders/         # 플레이스홀더
│   │   └── icons/                # 커스텀 아이콘
│   └── fonts/                    # 로컬 폰트 (Optional)
└── README.md                     # 프로토타입 가이드
```

---

## 2. 디자인 시스템 (Design System)

### 2.1 색상 팔레트 (Color Palette)

```css
/* CSS Variables (variables.css) */

:root {
  /* Primary Colors - 교회 브랜드 */
  --color-primary-50: #EFF6FF;
  --color-primary-100: #DBEAFE;
  --color-primary-200: #BFDBFE;
  --color-primary-300: #93C5FD;
  --color-primary-400: #60A5FA;
  --color-primary-500: #3B82F6;
  --color-primary-600: #2563EB;
  --color-primary-700: #1D4ED8;    /* Main Brand Color */
  --color-primary-800: #1E40AF;
  --color-primary-900: #1E3A8A;

  /* Secondary Colors - 강조 */
  --color-secondary-500: #F59E0B;  /* Gold */
  --color-secondary-600: #D97706;
  --color-secondary-700: #B45309;

  /* Neutral Colors - 그레이스케일 */
  --color-gray-50: #F9FAFB;
  --color-gray-100: #F3F4F6;
  --color-gray-200: #E5E7EB;
  --color-gray-300: #D1D5DB;
  --color-gray-400: #9CA3AF;
  --color-gray-500: #6B7280;
  --color-gray-600: #4B5563;
  --color-gray-700: #374151;
  --color-gray-800: #1F2937;
  --color-gray-900: #111827;

  /* Semantic Colors */
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error: #EF4444;
  --color-info: #3B82F6;

  /* Background & Surface */
  --color-bg-primary: #FFFFFF;
  --color-bg-secondary: var(--color-gray-50);
  --color-bg-tertiary: var(--color-gray-100);

  /* Text Colors */
  --color-text-primary: var(--color-gray-900);
  --color-text-secondary: var(--color-gray-700);
  --color-text-tertiary: var(--color-gray-500);
  --color-text-inverted: #FFFFFF;

  /* Border */
  --color-border: var(--color-gray-300);
  --color-border-light: var(--color-gray-200);
}
```

### 2.2 타이포그래피 (Typography)

```css
/* Font System */
:root {
  /* Font Family */
  --font-primary: 'Noto Sans KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-display: 'Noto Serif KR', Georgia, serif;  /* For headings */
  --font-mono: 'Courier New', monospace;

  /* Font Sizes - Fluid Typography */
  --text-xs: clamp(0.75rem, 0.7rem + 0.25vw, 0.875rem);      /* 12-14px */
  --text-sm: clamp(0.875rem, 0.825rem + 0.25vw, 1rem);       /* 14-16px */
  --text-base: clamp(1rem, 0.95rem + 0.25vw, 1.125rem);      /* 16-18px */
  --text-lg: clamp(1.125rem, 1.05rem + 0.375vw, 1.25rem);    /* 18-20px */
  --text-xl: clamp(1.25rem, 1.15rem + 0.5vw, 1.5rem);        /* 20-24px */
  --text-2xl: clamp(1.5rem, 1.35rem + 0.75vw, 1.875rem);     /* 24-30px */
  --text-3xl: clamp(1.875rem, 1.65rem + 1.125vw, 2.25rem);   /* 30-36px */
  --text-4xl: clamp(2.25rem, 1.95rem + 1.5vw, 3rem);         /* 36-48px */
  --text-5xl: clamp(3rem, 2.5rem + 2.5vw, 4rem);             /* 48-64px */

  /* Font Weights */
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;

  /* Line Heights */
  --leading-tight: 1.25;
  --leading-normal: 1.5;
  --leading-relaxed: 1.75;

  /* Letter Spacing */
  --tracking-tight: -0.025em;
  --tracking-normal: 0;
  --tracking-wide: 0.025em;
}

/* Typography Classes */
.heading-1 {
  font-family: var(--font-display);
  font-size: var(--text-5xl);
  font-weight: var(--font-bold);
  line-height: var(--leading-tight);
  letter-spacing: var(--tracking-tight);
  color: var(--color-text-primary);
}

.heading-2 {
  font-family: var(--font-display);
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  line-height: var(--leading-tight);
  color: var(--color-text-primary);
}

.heading-3 {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  line-height: var(--leading-tight);
  color: var(--color-text-primary);
}

.body-large {
  font-size: var(--text-lg);
  line-height: var(--leading-relaxed);
  color: var(--color-text-secondary);
}

.body {
  font-size: var(--text-base);
  line-height: var(--leading-normal);
  color: var(--color-text-secondary);
}

.body-small {
  font-size: var(--text-sm);
  line-height: var(--leading-normal);
  color: var(--color-text-tertiary);
}

.caption {
  font-size: var(--text-xs);
  line-height: var(--leading-normal);
  color: var(--color-text-tertiary);
}
```

### 2.3 스페이싱 시스템 (Spacing)

```css
:root {
  /* Spacing Scale (8px base) */
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-5: 1.25rem;   /* 20px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
  --space-20: 5rem;     /* 80px */
  --space-24: 6rem;     /* 96px */
}
```

### 2.4 Border Radius (Rounded Corners)

```css
:root {
  --radius-sm: 0.25rem;   /* 4px - Small elements */
  --radius-md: 0.5rem;    /* 8px - Buttons, inputs */
  --radius-lg: 0.75rem;   /* 12px - Cards */
  --radius-xl: 1rem;      /* 16px - Large cards */
  --radius-2xl: 1.5rem;   /* 24px - Hero sections */
  --radius-full: 9999px;  /* Pills, badges */
}
```

### 2.5 Shadows (Elevation)

```css
:root {
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
               0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1),
               0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
               0 10px 10px -5px rgba(0, 0, 0, 0.04);
  --shadow-2xl: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}
```

---

## 3. 컴포넌트 설계 (Component Design)

### 3.1 버튼 (Button)

```html
<!-- Primary Button -->
<button class="btn btn-primary">
  공지사항 보기
</button>

<!-- Secondary Button -->
<button class="btn btn-secondary">
  자세히 보기
</button>

<!-- Outline Button -->
<button class="btn btn-outline">
  취소
</button>

<!-- Icon Button -->
<button class="btn btn-icon">
  <svg><!-- Icon --></svg>
</button>

<!-- Loading State -->
<button class="btn btn-primary" disabled>
  <span class="spinner"></span>
  저장 중...
</button>
```

```css
/* Button Styles (components.css) */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  line-height: 1;
  border-radius: var(--radius-md);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn:active {
  transform: translateY(0);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.btn-primary {
  background: var(--color-primary-700);
  color: var(--color-text-inverted);
}

.btn-primary:hover {
  background: var(--color-primary-600);
}

.btn-secondary {
  background: var(--color-secondary-600);
  color: var(--color-text-inverted);
}

.btn-secondary:hover {
  background: var(--color-secondary-500);
}

.btn-outline {
  background: transparent;
  color: var(--color-primary-700);
  border: 2px solid var(--color-primary-700);
}

.btn-outline:hover {
  background: var(--color-primary-50);
}

.btn-icon {
  padding: var(--space-2);
  border-radius: var(--radius-full);
}

/* Button Sizes */
.btn-sm {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
}

.btn-lg {
  padding: var(--space-4) var(--space-8);
  font-size: var(--text-lg);
}
```

### 3.2 카드 (Card)

```html
<!-- Notice Card -->
<article class="card card-notice">
  <div class="card-header">
    <span class="badge badge-primary">일반</span>
    <time class="card-date">2026.02.04</time>
  </div>
  <div class="card-body">
    <h3 class="card-title">주일예배 시간 변경 안내</h3>
    <p class="card-description">
      2월 둘째 주부터 주일예배 시간이 오전 11시로 변경됩니다...
    </p>
  </div>
  <div class="card-footer">
    <span class="card-meta">조회 123</span>
    <a href="#" class="card-link">자세히 보기 →</a>
  </div>
</article>

<!-- Sermon Card -->
<article class="card card-sermon">
  <img src="/assets/images/sermons/thumb.jpg" alt="설교 썸네일" class="card-image">
  <div class="card-body">
    <h3 class="card-title">은혜로운 삶</h3>
    <p class="card-meta">
      <span>김목사 목사</span>
      <span>·</span>
      <span>2026.02.04</span>
    </p>
    <div class="card-actions">
      <button class="btn btn-icon" aria-label="재생">
        <svg><!-- Play icon --></svg>
      </button>
    </div>
  </div>
</article>
```

```css
/* Card Styles */
.card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border-bottom: 1px solid var(--color-border-light);
}

.card-body {
  padding: var(--space-6);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border-top: 1px solid var(--color-border-light);
  background: var(--color-bg-secondary);
}

.card-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.card-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--space-2);
}

.card-description {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: var(--leading-relaxed);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
}
```

### 3.3 배지 (Badge)

```html
<span class="badge badge-primary">일반</span>
<span class="badge badge-warning">중요</span>
<span class="badge badge-success">승인</span>
<span class="badge badge-error">거부</span>
<span class="badge badge-live">LIVE</span>
```

```css
.badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  line-height: 1;
  border-radius: var(--radius-full);
  text-transform: uppercase;
  letter-spacing: var(--tracking-wide);
}

.badge-primary {
  background: var(--color-primary-100);
  color: var(--color-primary-700);
}

.badge-warning {
  background: var(--color-secondary-100);
  color: var(--color-secondary-700);
}

.badge-success {
  background: #D1FAE5;
  color: var(--color-success);
}

.badge-error {
  background: #FEE2E2;
  color: var(--color-error);
}

.badge-live {
  background: var(--color-error);
  color: white;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}
```

### 3.4 입력 폼 (Form Controls)

```html
<!-- Text Input -->
<div class="form-group">
  <label for="title" class="form-label">제목</label>
  <input type="text" id="title" class="form-input" placeholder="제목을 입력하세요">
  <span class="form-hint">최소 5자 이상 입력하세요.</span>
</div>

<!-- Textarea -->
<div class="form-group">
  <label for="content" class="form-label">내용</label>
  <textarea id="content" class="form-textarea" rows="5" placeholder="내용을 입력하세요"></textarea>
</div>

<!-- Select -->
<div class="form-group">
  <label for="category" class="form-label">카테고리</label>
  <select id="category" class="form-select">
    <option value="">선택하세요</option>
    <option value="notice">일반</option>
    <option value="important">중요</option>
  </select>
</div>

<!-- Checkbox -->
<div class="form-group">
  <label class="form-checkbox">
    <input type="checkbox">
    <span>개인정보 수집 및 이용에 동의합니다.</span>
  </label>
</div>
```

```css
.form-group {
  margin-bottom: var(--space-6);
}

.form-label {
  display: block;
  margin-bottom: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-text-primary);
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-base);
  color: var(--color-text-primary);
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-hint {
  display: block;
  margin-top: var(--space-2);
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
}
```

### 3.5 네비게이션 (Navigation)

```html
<!-- Desktop Navigation -->
<nav class="navbar">
  <div class="container navbar-container">
    <!-- Logo -->
    <a href="/" class="navbar-logo">
      <img src="/assets/images/logo.svg" alt="성북교회">
    </a>

    <!-- Main Menu -->
    <ul class="navbar-menu">
      <li class="navbar-item">
        <a href="/pages/notices/list.html" class="navbar-link">교회 소식</a>
      </li>
      <li class="navbar-item dropdown">
        <button class="navbar-link">예배 안내</button>
        <div class="dropdown-menu">
          <a href="/pages/worship/schedule.html">예배 시간</a>
          <a href="/pages/worship/live.html">라이브 예배</a>
          <a href="/pages/sermons/list.html">설교 말씀</a>
        </div>
      </li>
      <li class="navbar-item">
        <a href="/pages/galleries/image-list.html" class="navbar-link">갤러리</a>
      </li>
      <li class="navbar-item">
        <a href="/pages/about/church.html" class="navbar-link">교회 소개</a>
      </li>
    </ul>

    <!-- Mobile Menu Toggle -->
    <button class="navbar-toggle" aria-label="메뉴 열기">
      <span></span>
      <span></span>
      <span></span>
    </button>
  </div>
</nav>

<!-- Mobile Navigation (Overlay) -->
<div class="mobile-menu">
  <div class="mobile-menu-header">
    <img src="/assets/images/logo.svg" alt="성북교회">
    <button class="mobile-menu-close" aria-label="메뉴 닫기">×</button>
  </div>
  <nav class="mobile-menu-nav">
    <!-- Mobile menu items -->
  </nav>
</div>
```

```css
/* Desktop Navigation */
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
}

.navbar-menu {
  display: flex;
  gap: var(--space-8);
  list-style: none;
}

.navbar-link {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-text-secondary);
  text-decoration: none;
  transition: color 0.2s;
}

.navbar-link:hover {
  color: var(--color-primary-700);
}

/* Mobile Menu */
.navbar-toggle {
  display: none;
}

@media (max-width: 767px) {
  .navbar-menu {
    display: none;
  }

  .navbar-toggle {
    display: flex;
    flex-direction: column;
    gap: 5px;
    background: none;
    border: none;
    cursor: pointer;
  }

  .navbar-toggle span {
    width: 24px;
    height: 3px;
    background: var(--color-text-primary);
    border-radius: var(--radius-sm);
    transition: all 0.3s;
  }

  .mobile-menu {
    position: fixed;
    top: 0;
    left: -100%;
    width: 85%;
    max-width: 400px;
    height: 100vh;
    background: var(--color-bg-primary);
    box-shadow: var(--shadow-2xl);
    transition: left 0.3s;
    z-index: 200;
  }

  .mobile-menu.active {
    left: 0;
  }
}
```

### 3.6 모달 (Modal)

```html
<!-- Modal Structure -->
<div class="modal" id="exampleModal">
  <div class="modal-backdrop" data-dismiss="modal"></div>
  <div class="modal-dialog">
    <div class="modal-header">
      <h3 class="modal-title">삭제 확인</h3>
      <button class="modal-close" data-dismiss="modal" aria-label="닫기">×</button>
    </div>
    <div class="modal-body">
      <p>정말로 이 항목을 삭제하시겠습니까?</p>
    </div>
    <div class="modal-footer">
      <button class="btn btn-outline" data-dismiss="modal">취소</button>
      <button class="btn btn-error">삭제</button>
    </div>
  </div>
</div>
```

```css
.modal {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: none;
  align-items: center;
  justify-content: center;
}

.modal.active {
  display: flex;
}

.modal-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.modal-dialog {
  position: relative;
  width: 90%;
  max-width: 500px;
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-2xl);
  animation: modalSlideUp 0.3s ease;
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--color-border-light);
}

.modal-body {
  padding: var(--space-6);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--color-border-light);
}
```

---

## 4. 페이지 설계 (Page Designs)

### 4.1 홈페이지 (index.html)

#### 레이아웃 구조

```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>성북교회 - 하나님의 사랑을 전하는 교회</title>
  <link rel="stylesheet" href="/css/reset.css">
  <link rel="stylesheet" href="/css/variables.css">
  <link rel="stylesheet" href="/css/components.css">
  <link rel="stylesheet" href="/css/layout.css">
  <link rel="stylesheet" href="/css/pages.css">
  <link rel="stylesheet" href="/css/responsive.css">
</head>
<body>
  <!-- Navigation (included via JS) -->
  <div id="navbar"></div>

  <!-- Hero Section -->
  <section class="hero">
    <div class="hero-background">
      <img src="/assets/images/hero/church.jpg" alt="교회 전경">
      <div class="hero-overlay"></div>
    </div>
    <div class="hero-content">
      <h1 class="hero-title">하나님의 사랑을 전하는 교회</h1>
      <p class="hero-subtitle">성북교회에 오신 것을 환영합니다</p>

      <!-- Live Status -->
      <div class="hero-live">
        <span class="badge badge-live">LIVE</span>
        <span>주일예배 실시간 중계 중</span>
        <a href="/pages/worship/live.html" class="btn btn-primary">
          예배 참여하기
        </a>
      </div>
    </div>
  </section>

  <!-- Quick Links -->
  <section class="quick-links">
    <div class="container">
      <div class="quick-links-grid">
        <a href="/pages/worship/schedule.html" class="quick-link-card">
          <svg class="quick-link-icon"><!-- Icon --></svg>
          <h3>예배 안내</h3>
        </a>
        <a href="/pages/notices/list.html" class="quick-link-card">
          <svg class="quick-link-icon"><!-- Icon --></svg>
          <h3>공지사항</h3>
        </a>
        <a href="/pages/sermons/list.html" class="quick-link-card">
          <svg class="quick-link-icon"><!-- Icon --></svg>
          <h3>설교 말씀</h3>
        </a>
        <a href="/pages/galleries/image-list.html" class="quick-link-card">
          <svg class="quick-link-icon"><!-- Icon --></svg>
          <h3>갤러리</h3>
        </a>
      </div>
    </div>
  </section>

  <!-- Latest Notices -->
  <section class="section section-notices">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">교회 소식</h2>
        <a href="/pages/notices/list.html" class="section-link">
          전체보기 →
        </a>
      </div>
      <div class="notices-grid">
        <!-- Notice Cards (3개) -->
        <article class="card card-notice"><!-- ... --></article>
        <article class="card card-notice"><!-- ... --></article>
        <article class="card card-notice"><!-- ... --></article>
      </div>
    </div>
  </section>

  <!-- Worship Schedule -->
  <section class="section section-worship">
    <div class="container">
      <h2 class="section-title">주간 예배 일정</h2>
      <div class="worship-schedule">
        <div class="worship-item">
          <div class="worship-day">주일</div>
          <div class="worship-time">오전 11:00</div>
          <div class="worship-type">주일예배</div>
        </div>
        <div class="worship-item">
          <div class="worship-day">수요일</div>
          <div class="worship-time">오후 7:30</div>
          <div class="worship-type">수요예배</div>
        </div>
        <div class="worship-item">
          <div class="worship-day">금요일</div>
          <div class="worship-time">오전 5:30</div>
          <div class="worship-type">새벽기도회</div>
        </div>
      </div>
    </div>
  </section>

  <!-- Latest Sermons -->
  <section class="section section-sermons">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">최근 설교</h2>
        <a href="/pages/sermons/list.html" class="section-link">
          전체보기 →
        </a>
      </div>
      <div class="sermons-grid">
        <!-- Sermon Cards (4개) -->
        <article class="card card-sermon"><!-- ... --></article>
        <article class="card card-sermon"><!-- ... --></article>
        <article class="card card-sermon"><!-- ... --></article>
        <article class="card card-sermon"><!-- ... --></article>
      </div>
    </div>
  </section>

  <!-- Footer -->
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>성북교회</h3>
          <p>주소: 서울시 성북구 ...</p>
          <p>전화: 02-1234-5678</p>
          <p>이메일: info@sungbok.church</p>
        </div>
        <div class="footer-section">
          <h4>바로가기</h4>
          <ul>
            <li><a href="/pages/about/church.html">교회 소개</a></li>
            <li><a href="/pages/about/pastors.html">목회자 소개</a></li>
            <li><a href="/pages/about/ministries.html">사역 안내</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>소셜 미디어</h4>
          <div class="footer-social">
            <a href="#" aria-label="YouTube"><!-- Icon --></a>
            <a href="#" aria-label="Facebook"><!-- Icon --></a>
            <a href="#" aria-label="Instagram"><!-- Icon --></a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 성북교회. All rights reserved.</p>
      </div>
    </div>
  </footer>

  <!-- Scripts -->
  <script src="/js/utils.js"></script>
  <script src="/js/navigation.js"></script>
</body>
</html>
```

#### CSS 스타일 (pages.css - Hero Section)

```css
/* Hero Section */
.hero {
  position: relative;
  height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
}

.hero-background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.3),
    rgba(0, 0, 0, 0.6)
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
}

.hero-title {
  font-size: var(--text-5xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.hero-subtitle {
  font-size: var(--text-xl);
  margin-bottom: var(--space-8);
  opacity: 0.9;
}

.hero-live {
  display: inline-flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-xl);
}

/* Responsive Hero */
@media (max-width: 767px) {
  .hero {
    height: 400px;
  }

  .hero-title {
    font-size: var(--text-3xl);
  }

  .hero-subtitle {
    font-size: var(--text-lg);
  }
}
```

### 4.2 공지사항 목록 (pages/notices/list.html)

#### 레이아웃 구조

```html
<div class="page-header">
  <div class="container">
    <h1 class="page-title">교회 소식</h1>
    <p class="page-description">성북교회의 다양한 소식을 전해드립니다</p>
  </div>
</div>

<div class="page-content">
  <div class="container">
    <!-- Category Tabs -->
    <div class="tabs">
      <button class="tab active" data-category="all">전체</button>
      <button class="tab" data-category="notice">일반</button>
      <button class="tab" data-category="important">중요</button>
      <button class="tab" data-category="event">행사</button>
    </div>

    <!-- Pinned Notices -->
    <div class="pinned-notices">
      <h3 class="section-subtitle">
        <svg><!-- Pin icon --></svg>
        고정 공지
      </h3>
      <div class="notices-list">
        <!-- Pinned notice cards -->
      </div>
    </div>

    <!-- Regular Notices -->
    <div class="notices-grid">
      <!-- Notice cards (12개 per page) -->
      <article class="card card-notice">
        <div class="card-header">
          <span class="badge badge-primary">일반</span>
          <time class="card-date">2026.02.04</time>
        </div>
        <div class="card-body">
          <h3 class="card-title">주일예배 시간 변경 안내</h3>
          <p class="card-description">
            2월 둘째 주부터 주일예배 시간이 오전 11시로 변경됩니다...
          </p>
        </div>
        <div class="card-footer">
          <span class="card-meta">
            <svg><!-- Eye icon --></svg>
            123
          </span>
          <a href="/pages/notices/detail.html?id=1" class="card-link">
            자세히 보기 →
          </a>
        </div>
      </article>
      <!-- More cards... -->
    </div>

    <!-- Pagination -->
    <nav class="pagination">
      <button class="pagination-btn" disabled>이전</button>
      <button class="pagination-page active">1</button>
      <button class="pagination-page">2</button>
      <button class="pagination-page">3</button>
      <button class="pagination-btn">다음</button>
    </nav>
  </div>
</div>
```

#### JavaScript 인터랙션 (tabs.js)

```javascript
// Tab switching
const tabs = document.querySelectorAll('.tab');
const noticesGrid = document.querySelector('.notices-grid');

tabs.forEach(tab => {
  tab.addEventListener('click', () => {
    // Remove active class from all tabs
    tabs.forEach(t => t.classList.remove('active'));

    // Add active class to clicked tab
    tab.classList.add('active');

    // Get selected category
    const category = tab.dataset.category;

    // Filter notices (in real app, this would be an API call)
    filterNotices(category);
  });
});

function filterNotices(category) {
  // Mock filtering logic
  console.log(`Filtering by category: ${category}`);

  // In real implementation:
  // fetch(`/api/notices?category=${category}`)
  //   .then(res => res.json())
  //   .then(data => renderNotices(data));
}
```

### 4.3 설교 상세 (pages/sermons/detail.html)

#### 레이아웃 구조

```html
<article class="sermon-detail">
  <div class="container">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
      <a href="/">홈</a>
      <span>/</span>
      <a href="/pages/sermons/list.html">설교</a>
      <span>/</span>
      <span>은혜로운 삶</span>
    </nav>

    <!-- Sermon Header -->
    <header class="sermon-header">
      <h1 class="sermon-title">은혜로운 삶</h1>
      <div class="sermon-meta">
        <div class="sermon-pastor">
          <img src="/assets/images/pastors/kim.jpg" alt="김목사" class="pastor-avatar">
          <div>
            <div class="pastor-name">김목사 목사</div>
            <div class="pastor-role">담임목사</div>
          </div>
        </div>
        <div class="sermon-info">
          <span>
            <svg><!-- Calendar icon --></svg>
            2026년 2월 4일
          </span>
          <span>
            <svg><!-- Church icon --></svg>
            주일 대예배
          </span>
          <span>
            <svg><!-- Eye icon --></svg>
            조회 245
          </span>
        </div>
      </div>
    </header>

    <!-- Audio Player -->
    <div class="audio-player">
      <div class="audio-player-controls">
        <button class="play-btn" aria-label="재생/일시정지">
          <svg><!-- Play icon --></svg>
        </button>
        <div class="audio-progress">
          <input type="range" min="0" max="100" value="0" class="progress-bar">
          <div class="audio-time">
            <span class="current-time">00:00</span>
            <span class="total-time">45:30</span>
          </div>
        </div>
        <div class="audio-actions">
          <button class="audio-btn" aria-label="속도 조절">1.0x</button>
          <button class="audio-btn" aria-label="볼륨">
            <svg><!-- Volume icon --></svg>
          </button>
          <button class="audio-btn" aria-label="다운로드">
            <svg><!-- Download icon --></svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Sermon Content -->
    <div class="sermon-content">
      <!-- Scripture -->
      <section class="sermon-section">
        <h2 class="section-heading">본문 말씀</h2>
        <blockquote class="scripture">
          <p>너희는 먼저 그의 나라와 그의 의를 구하라 그리하면 이 모든 것을 너희에게 더하시리라</p>
          <cite>- 마태복음 6장 33절</cite>
        </blockquote>
      </section>

      <!-- Sermon Text -->
      <section class="sermon-section">
        <h2 class="section-heading">설교 요약</h2>
        <div class="sermon-text">
          <p>은혜로운 삶이란 하나님의 사랑 안에서...</p>
          <p>우리는 매일 하나님의 은혜를 경험하며...</p>
          <!-- More content -->
        </div>
      </section>

      <!-- Related Worship -->
      <section class="sermon-section">
        <h2 class="section-heading">관련 예배</h2>
        <div class="worship-card">
          <h3>주일 대예배</h3>
          <p>매주 일요일 오전 11:00</p>
          <a href="/pages/worship/schedule.html" class="btn btn-outline">
            예배 일정 보기
          </a>
        </div>
      </section>
    </div>

    <!-- Navigation -->
    <nav class="sermon-navigation">
      <a href="#" class="sermon-nav-link prev">
        <span class="nav-label">이전 설교</span>
        <span class="nav-title">믿음의 여정</span>
      </a>
      <a href="#" class="sermon-nav-link next">
        <span class="nav-label">다음 설교</span>
        <span class="nav-title">소망의 빛</span>
      </a>
    </nav>
  </div>
</article>
```

#### JavaScript (audio player - slider.js)

```javascript
// Audio Player
class AudioPlayer {
  constructor(element) {
    this.player = element;
    this.playBtn = this.player.querySelector('.play-btn');
    this.progressBar = this.player.querySelector('.progress-bar');
    this.currentTime = this.player.querySelector('.current-time');
    this.totalTime = this.player.querySelector('.total-time');

    // Mock audio (in real app, use <audio> element)
    this.isPlaying = false;
    this.duration = 2730; // 45:30 in seconds
    this.currentPosition = 0;

    this.init();
  }

  init() {
    this.playBtn.addEventListener('click', () => this.togglePlay());
    this.progressBar.addEventListener('input', (e) => this.seek(e.target.value));
  }

  togglePlay() {
    this.isPlaying = !this.isPlaying;

    if (this.isPlaying) {
      this.playBtn.innerHTML = '<!-- Pause icon -->';
      this.startProgress();
    } else {
      this.playBtn.innerHTML = '<!-- Play icon -->';
      this.pauseProgress();
    }
  }

  startProgress() {
    this.interval = setInterval(() => {
      this.currentPosition++;
      this.updateUI();

      if (this.currentPosition >= this.duration) {
        this.togglePlay();
      }
    }, 1000);
  }

  pauseProgress() {
    clearInterval(this.interval);
  }

  seek(value) {
    this.currentPosition = (value / 100) * this.duration;
    this.updateUI();
  }

  updateUI() {
    const progress = (this.currentPosition / this.duration) * 100;
    this.progressBar.value = progress;
    this.currentTime.textContent = this.formatTime(this.currentPosition);
  }

  formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
}

// Initialize
const audioPlayer = document.querySelector('.audio-player');
if (audioPlayer) {
  new AudioPlayer(audioPlayer);
}
```

### 4.4 이미지 갤러리 상세 (Lightbox)

#### HTML 구조

```html
<!-- Gallery Grid -->
<div class="gallery-grid">
  <figure class="gallery-item" data-lightbox="gallery-1">
    <img src="/assets/images/gallery/thumb-1.jpg" alt="교회 전경" loading="lazy">
    <figcaption>교회 전경</figcaption>
  </figure>
  <!-- More gallery items -->
</div>

<!-- Lightbox Modal -->
<div class="lightbox" id="lightbox">
  <button class="lightbox-close" aria-label="닫기">×</button>
  <button class="lightbox-prev" aria-label="이전">‹</button>
  <button class="lightbox-next" aria-label="다음">›</button>

  <div class="lightbox-content">
    <img src="" alt="" class="lightbox-image">
    <div class="lightbox-caption"></div>
    <div class="lightbox-counter">
      <span class="current">1</span> / <span class="total">12</span>
    </div>
  </div>
</div>
```

#### JavaScript (modal.js - Lightbox)

```javascript
class Lightbox {
  constructor() {
    this.lightbox = document.getElementById('lightbox');
    this.image = this.lightbox.querySelector('.lightbox-image');
    this.caption = this.lightbox.querySelector('.lightbox-caption');
    this.currentCounter = this.lightbox.querySelector('.current');
    this.totalCounter = this.lightbox.querySelector('.total');

    this.images = [];
    this.currentIndex = 0;

    this.init();
  }

  init() {
    // Collect all gallery images
    const galleryItems = document.querySelectorAll('.gallery-item');
    this.images = Array.from(galleryItems).map(item => ({
      src: item.querySelector('img').src.replace('thumb-', 'full-'),
      alt: item.querySelector('img').alt,
      caption: item.querySelector('figcaption')?.textContent
    }));

    this.totalCounter.textContent = this.images.length;

    // Event listeners
    galleryItems.forEach((item, index) => {
      item.addEventListener('click', () => this.open(index));
    });

    this.lightbox.querySelector('.lightbox-close').addEventListener('click', () => this.close());
    this.lightbox.querySelector('.lightbox-prev').addEventListener('click', () => this.prev());
    this.lightbox.querySelector('.lightbox-next').addEventListener('click', () => this.next());

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
      if (!this.lightbox.classList.contains('active')) return;

      if (e.key === 'Escape') this.close();
      if (e.key === 'ArrowLeft') this.prev();
      if (e.key === 'ArrowRight') this.next();
    });
  }

  open(index) {
    this.currentIndex = index;
    this.updateImage();
    this.lightbox.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  close() {
    this.lightbox.classList.remove('active');
    document.body.style.overflow = '';
  }

  prev() {
    this.currentIndex = (this.currentIndex - 1 + this.images.length) % this.images.length;
    this.updateImage();
  }

  next() {
    this.currentIndex = (this.currentIndex + 1) % this.images.length;
    this.updateImage();
  }

  updateImage() {
    const current = this.images[this.currentIndex];
    this.image.src = current.src;
    this.image.alt = current.alt;
    this.caption.textContent = current.caption || '';
    this.currentCounter.textContent = this.currentIndex + 1;
  }
}

// Initialize
new Lightbox();
```

---

## 5. 반응형 디자인 (Responsive Design)

### 5.1 Breakpoints 정의

```css
/* responsive.css */

/* Mobile First Approach */

/* Extra Small (Mobile) - Default (320px+) */
/* Styles are written for mobile first */

/* Small (Large Mobile) */
@media (min-width: 480px) {
  .container {
    padding: 0 var(--space-6);
  }
}

/* Medium (Tablet) */
@media (min-width: 768px) {
  .container {
    padding: 0 var(--space-8);
  }

  /* Grid adjustments */
  .grid-2 {
    grid-template-columns: repeat(2, 1fr);
  }

  .notices-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Large (Desktop) */
@media (min-width: 1024px) {
  .container {
    max-width: 1200px;
  }

  /* Grid adjustments */
  .grid-3 {
    grid-template-columns: repeat(3, 1fr);
  }

  .grid-4 {
    grid-template-columns: repeat(4, 1fr);
  }

  .notices-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .sermons-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* Extra Large (Wide Desktop) */
@media (min-width: 1280px) {
  .container {
    max-width: 1400px;
  }
}
```

### 5.2 모바일 최적화

```css
/* Touch Target Size (최소 44x44px) */
@media (max-width: 767px) {
  .btn,
  .navbar-link,
  .card-link {
    min-height: 44px;
    min-width: 44px;
  }

  /* Font Size Boost */
  body {
    font-size: 16px; /* Prevent iOS auto-zoom */
  }

  /* Stacking */
  .sermon-header {
    flex-direction: column;
  }

  .worship-schedule {
    flex-direction: column;
  }

  /* Full Width Cards */
  .card {
    margin-bottom: var(--space-4);
  }

  /* Mobile-Specific Padding */
  .section {
    padding: var(--space-8) 0;
  }
}
```

---

## 6. 접근성 (Accessibility)

### 6.1 ARIA 레이블

```html
<!-- Skip Navigation -->
<a href="#main-content" class="skip-link">본문으로 건너뛰기</a>

<!-- Landmark Roles -->
<header role="banner"><!-- Navigation --></header>
<main id="main-content" role="main"><!-- Content --></main>
<footer role="contentinfo"><!-- Footer --></footer>

<!-- Button Labels -->
<button aria-label="메뉴 열기" aria-expanded="false">
  <span class="sr-only">메뉴 열기</span>
  <svg aria-hidden="true"><!-- Icon --></svg>
</button>

<!-- Form Labels -->
<label for="search-input">검색</label>
<input id="search-input" type="search" aria-label="사이트 내 검색">

<!-- Loading States -->
<button aria-busy="true" aria-live="polite">
  저장 중...
</button>
```

### 6.2 키보드 네비게이션

```javascript
// Focus Management
class FocusTrap {
  constructor(element) {
    this.element = element;
    this.focusableElements = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';
  }

  trap() {
    const focusableContent = this.element.querySelectorAll(this.focusableElements);
    const firstFocusable = focusableContent[0];
    const lastFocusable = focusableContent[focusableContent.length - 1];

    this.element.addEventListener('keydown', (e) => {
      if (e.key !== 'Tab') return;

      if (e.shiftKey) {
        if (document.activeElement === firstFocusable) {
          lastFocusable.focus();
          e.preventDefault();
        }
      } else {
        if (document.activeElement === lastFocusable) {
          firstFocusable.focus();
          e.preventDefault();
        }
      }
    });

    firstFocusable.focus();
  }
}

// Usage in Modal
const modal = document.querySelector('.modal');
const focusTrap = new FocusTrap(modal);

function openModal() {
  modal.classList.add('active');
  focusTrap.trap();
}
```

### 6.3 색상 대비 (Contrast Ratio)

```css
/* Ensure 4.5:1 contrast ratio */
:root {
  /* Text on White Background */
  --color-text-primary: #111827;   /* 16.07:1 ✅ */
  --color-text-secondary: #374151; /* 11.04:1 ✅ */
  --color-text-tertiary: #6B7280;  /* 5.26:1 ✅ */

  /* Verify contrast with WebAIM Contrast Checker */
  /* https://webaim.org/resources/contrastchecker/ */
}

/* Focus Indicators */
*:focus-visible {
  outline: 3px solid var(--color-primary-500);
  outline-offset: 2px;
}

/* High Contrast Mode Support */
@media (prefers-contrast: high) {
  :root {
    --color-text-primary: #000000;
    --color-bg-primary: #FFFFFF;
    --color-border: #000000;
  }
}
```

---

## 7. 성능 최적화 (Performance)

### 7.1 이미지 최적화

```html
<!-- Responsive Images -->
<img
  srcset="/assets/images/hero/church-320w.jpg 320w,
          /assets/images/hero/church-640w.jpg 640w,
          /assets/images/hero/church-1024w.jpg 1024w,
          /assets/images/hero/church-1920w.jpg 1920w"
  sizes="(max-width: 767px) 100vw,
         (max-width: 1023px) 50vw,
         33vw"
  src="/assets/images/hero/church-640w.jpg"
  alt="교회 전경"
  loading="lazy">

<!-- Modern Formats -->
<picture>
  <source srcset="/assets/images/hero/church.webp" type="image/webp">
  <source srcset="/assets/images/hero/church.jpg" type="image/jpeg">
  <img src="/assets/images/hero/church.jpg" alt="교회 전경">
</picture>
```

### 7.2 CSS 최적화

```css
/* Critical CSS (Inline in <head>) */
/* - Above-the-fold styles */
/* - Layout, typography, colors */

/* Defer Non-Critical CSS */
<link rel="preload" href="/css/pages.css" as="style" onload="this.onload=null;this.rel='stylesheet'">
<noscript><link rel="stylesheet" href="/css/pages.css"></noscript>
```

### 7.3 JavaScript 최적화

```html
<!-- Defer Non-Critical JS -->
<script src="/js/utils.js" defer></script>
<script src="/js/navigation.js" defer></script>

<!-- Lazy Load Heavy Components -->
<script>
  // Intersection Observer for Lazy Loading
  const lazyImages = document.querySelectorAll('img[loading="lazy"]');

  if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target;
          img.src = img.dataset.src;
          imageObserver.unobserve(img);
        }
      });
    });

    lazyImages.forEach(img => imageObserver.observe(img));
  }
</script>
```

---

## 8. 구현 우선순위 (Implementation Priority)

### Week 1: Core Pages (Foundation)

**Day 1-2: 홈페이지 + 네비게이션**
- ✅ index.html (Hero, Quick Links, Latest Content)
- ✅ Navigation (Desktop Mega Menu, Mobile Hamburger)
- ✅ Footer
- ✅ CSS (reset, variables, layout, components)
- ✅ JS (navigation.js, utils.js)

**Day 3-4: 공지사항 + 설교 목록**
- ✅ pages/notices/list.html (Category Tabs, Pagination)
- ✅ pages/sermons/list.html (Audio Preview)
- ✅ CSS (pages.css - List layouts)
- ✅ JS (tabs.js)

**Day 5: 예배 일정**
- ✅ pages/worship/schedule.html (Weekly Schedule)
- ✅ pages/worship/live.html (YouTube Embed)
- ✅ CSS (worship-specific styles)

### Week 2: Detail Pages

**Day 1-2: 상세 페이지**
- ✅ pages/notices/detail.html (Rich Content, Attachments)
- ✅ pages/sermons/detail.html (Audio Player, Scripture)
- ✅ JS (slider.js - Audio Player)

**Day 3: 이미지 갤러리**
- ✅ pages/galleries/image-list.html (Grid Layout)
- ✅ pages/galleries/image-detail.html (Masonry)
- ✅ JS (modal.js - Lightbox)

**Day 4-5: 간증**
- ✅ pages/community/testimonies.html (List)
- ✅ pages/community/testimony-detail.html (Detail)
- ✅ CSS (community-specific styles)

### Week 3: Extended Pages

**Day 1-2: 영상 갤러리 + 기도 제목**
- ✅ pages/galleries/video-list.html (YouTube Grid)
- ✅ pages/community/prayer-requests.html (List)
- ✅ pages/community/prayer-form.html (Form)
- ✅ JS (form-validation.js)

**Day 3-4: 교회 소개 + 사역**
- ✅ pages/about/church.html (Vision, History)
- ✅ pages/about/pastors.html (Pastor Cards)
- ✅ pages/about/staff.html (Staff Grid)
- ✅ pages/about/ministries.html (Ministry Cards)

**Day 5: 주보 + 헌금 + 선교**
- ✅ pages/about/bulletins.html (PDF Download)
- ✅ pages/about/donations.html (Account Info)
- ✅ pages/about/missions.html (Mission Cards)

### Week 4: Admin UI

**Day 1-2: 관리자 대시보드**
- ✅ pages/admin/dashboard.html (Stats, Recent Activity)
- ✅ CSS (admin-specific styles)

**Day 3-4: 콘텐츠 관리**
- ✅ pages/admin/content-form.html (CRUD Forms, Rich Text Editor)
- ✅ JS (form-validation.js enhancements)

**Day 5: 승인 관리 + 최종 리뷰**
- ✅ pages/admin/approval.html (Approval Queue)
- ✅ Final Review, Bug Fixes, Documentation

---

## 9. 품질 검증 (Quality Assurance)

### 9.1 디자인 리뷰 체크리스트

```markdown
✅ 색상 일관성
  - [ ] 디자인 시스템 색상만 사용
  - [ ] 명암비 4.5:1 이상 (WCAG AA)
  - [ ] Dark Mode 지원 (Optional)

✅ 타이포그래피
  - [ ] 최소 16px 본문 글씨
  - [ ] 일관된 폰트 사이즈 스케일
  - [ ] Line height 적절성 (1.5+)

✅ 레이아웃
  - [ ] 정렬 (Alignment) 일관성
  - [ ] 여백 (Spacing) 시스템 준수
  - [ ] 그리드 레이아웃 균형

✅ 반응형
  - [ ] Mobile (320px, 375px, 414px) 테스트
  - [ ] Tablet (768px, 1024px) 테스트
  - [ ] Desktop (1280px, 1920px) 테스트

✅ 인터랙션
  - [ ] 호버 효과 (Desktop)
  - [ ] 터치 최적화 (Mobile, 44px+ targets)
  - [ ] 로딩 상태 (Spinners, Skeletons)
  - [ ] 에러 상태 (Error Messages)
```

### 9.2 접근성 체크리스트

```markdown
✅ 키보드 네비게이션
  - [ ] Tab 순서 논리적
  - [ ] Focus Indicator 명확
  - [ ] Skip Navigation 제공

✅ 스크린 리더
  - [ ] ARIA 레이블 적절
  - [ ] Landmark Roles 명확
  - [ ] 이미지 Alt 텍스트 제공

✅ 색상 대비
  - [ ] WCAG AA 준수 (4.5:1)
  - [ ] 색상만으로 정보 전달 안 함
  - [ ] High Contrast Mode 지원

✅ 폼 접근성
  - [ ] 모든 Input에 Label 연결
  - [ ] 에러 메시지 명확
  - [ ] 필수 필드 표시
```

### 9.3 성능 체크리스트

```markdown
✅ 로딩 속도
  - [ ] HTML 초기 로드 < 1초
  - [ ] Largest Contentful Paint (LCP) < 2.5s
  - [ ] First Input Delay (FID) < 100ms
  - [ ] Cumulative Layout Shift (CLS) < 0.1

✅ 이미지 최적화
  - [ ] WebP 포맷 사용
  - [ ] Lazy Loading 적용
  - [ ] Responsive Images (srcset)

✅ CSS 최적화
  - [ ] Critical CSS Inline
  - [ ] Non-Critical CSS Deferred
  - [ ] Minified CSS

✅ JavaScript 최적화
  - [ ] Defer Non-Critical JS
  - [ ] Minified JS
  - [ ] No Unused Code
```

---

## 10. 다음 단계 (Next Steps)

### 10.1 Phase 3 완료 조건

```markdown
✅ 문서 산출물
  - [ ] 30+ HTML 프로토타입 페이지
  - [ ] 디자인 시스템 문서 (design-system.md)
  - [ ] 컴포넌트 라이브러리 (components.css)
  - [ ] README.md (프로토타입 가이드)

✅ 품질 기준
  - [ ] Lighthouse 접근성 점수 90+
  - [ ] 반응형 3가지 해상도 테스트 통과
  - [ ] 사용자 테스트 2+ 진행
  - [ ] 디자인 리뷰 승인

✅ 코드 준비
  - [ ] HTML 시맨틱 마크업
  - [ ] CSS 재사용 가능
  - [ ] JavaScript 모듈화
  - [ ] Phase 6 Next.js 변환 준비
```

### 10.2 Phase 6 전환 (Next.js Implementation)

```markdown
Phase 6에서 프로토타입 → 프로덕션 전환:

1. HTML → React Components
   - HTML 구조를 JSX로 변환
   - Props 기반 컴포넌트 설계

2. CSS → Tailwind CSS + CSS Modules
   - variables.css → tailwind.config.js
   - components.css → Reusable Components

3. JavaScript → TypeScript + React Hooks
   - Vanilla JS → React State Management
   - 이벤트 핸들러 → Event Handlers

4. API Integration
   - Mock Data → Real API Calls (Phase 4 APIs)
   - fetch → Axios or SWR
```

---

## 11. 참고 자료 (References)

### 11.1 Backend API (Phase 4)
- API 엔드포인트: 150+ REST APIs
- 완료 보고서: `docs/04-report/phase4-completion.report.md`
- 기술 스택: Spring Boot 4.0.2, PostgreSQL 18.1, Valkey

### 11.2 Design Resources
- **Icons**: Heroicons (https://heroicons.com), Lucide (https://lucide.dev)
- **Fonts**: Noto Sans KR (Google Fonts)
- **Images**: Unsplash (https://unsplash.com)
- **Accessibility**: WAVE (https://wave.webaim.org)

### 11.3 Tools
- **VS Code Extensions**:
  - Live Server
  - Prettier
  - ESLint
  - axe Accessibility Linter

- **Browser DevTools**:
  - Chrome Lighthouse
  - Firefox Accessibility Inspector
  - Safari Responsive Design Mode

---

**문서 버전**: 1.0
**마지막 수정**: 2026-02-04
**작성자**: bkit PDCA Agent
**PDCA Phase**: Design (Phase 3)
**Based On**: mockup.plan.md
**Next Phase**: Do (Implementation)
