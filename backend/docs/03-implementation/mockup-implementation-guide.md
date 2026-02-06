# Phase 3: UI/UX Mockup - Implementation Guide (Do Phase)

> **Feature**: 성북교회 홈페이지 UI/UX 목업 프로토타입 구현
>
> **Phase**: Do (Implementation)
> **Created**: 2026-02-04
> **Status**: In Progress
> **Based On**: mockup.design.md

---

## 🎯 Implementation Overview

### 목표 (Goals)
- ✅ **30+ HTML 프로토타입 페이지** 제작
- ✅ **디자인 시스템** 구현 (CSS Variables, Components)
- ✅ **반응형 디자인** (Mobile/Tablet/Desktop)
- ✅ **JavaScript 인터랙션** (Navigation, Modal, Audio Player, Lightbox)
- ✅ **접근성 준수** (WCAG 2.1 AA, Lighthouse 90+)

### 기간 (Duration)
**4주 (Week 1-4)** - 단계별 구현

---

## 📋 Week 1: Foundation & Core Pages (Day 1-5)

### Day 1-2: 프로젝트 설정 + 디자인 시스템

#### Step 1.1: 프로젝트 디렉토리 생성

```bash
# 프로토타입 디렉토리 생성
cd /Users/jaewon/Documents/sungbok-web
mkdir -p prototypes/{pages/{notices,sermons,worship,galleries,community,about,admin},css,js,assets/{images/{hero,placeholders,icons},fonts}}

# 디렉토리 구조 확인
tree prototypes -L 2
```

#### Step 1.2: CSS 리셋 파일 생성

**File**: `prototypes/css/reset.css`

```css
/* Modern CSS Reset */
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html {
  -webkit-text-size-adjust: 100%;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  min-height: 100vh;
  line-height: 1.5;
  font-family: system-ui, -apple-system, sans-serif;
}

img,
picture,
video,
canvas,
svg {
  display: block;
  max-width: 100%;
}

input,
button,
textarea,
select {
  font: inherit;
}

button {
  cursor: pointer;
}

a {
  text-decoration: none;
  color: inherit;
}

ul,
ol {
  list-style: none;
}

/* Screen Reader Only */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Skip Navigation */
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #000;
  color: #fff;
  padding: 8px;
  z-index: 100;
}

.skip-link:focus {
  top: 0;
}
```

#### Step 1.3: CSS Variables (디자인 시스템)

**File**: `prototypes/css/variables.css`

```css
:root {
  /* Primary Colors */
  --color-primary-50: #EFF6FF;
  --color-primary-100: #DBEAFE;
  --color-primary-700: #1D4ED8;
  --color-primary-800: #1E40AF;

  /* Secondary Colors */
  --color-secondary-600: #D97706;

  /* Neutral Colors */
  --color-gray-50: #F9FAFB;
  --color-gray-100: #F3F4F6;
  --color-gray-300: #D1D5DB;
  --color-gray-700: #374151;
  --color-gray-900: #111827;

  /* Semantic Colors */
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error: #EF4444;

  /* Background */
  --color-bg-primary: #FFFFFF;
  --color-bg-secondary: var(--color-gray-50);

  /* Text */
  --color-text-primary: var(--color-gray-900);
  --color-text-secondary: var(--color-gray-700);
  --color-text-tertiary: var(--color-gray-500);

  /* Border */
  --color-border: var(--color-gray-300);

  /* Font Family */
  --font-primary: 'Noto Sans KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  /* Font Sizes (Fluid Typography) */
  --text-xs: clamp(0.75rem, 0.7rem + 0.25vw, 0.875rem);
  --text-sm: clamp(0.875rem, 0.825rem + 0.25vw, 1rem);
  --text-base: clamp(1rem, 0.95rem + 0.25vw, 1.125rem);
  --text-lg: clamp(1.125rem, 1.05rem + 0.375vw, 1.25rem);
  --text-xl: clamp(1.25rem, 1.15rem + 0.5vw, 1.5rem);
  --text-2xl: clamp(1.5rem, 1.35rem + 0.75vw, 1.875rem);
  --text-3xl: clamp(1.875rem, 1.65rem + 1.125vw, 2.25rem);
  --text-4xl: clamp(2.25rem, 1.95rem + 1.5vw, 3rem);
  --text-5xl: clamp(3rem, 2.5rem + 2.5vw, 4rem);

  /* Font Weights */
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;

  /* Spacing (8px base) */
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-6: 1.5rem;
  --space-8: 2rem;
  --space-12: 3rem;
  --space-16: 4rem;

  /* Border Radius */
  --radius-sm: 0.25rem;
  --radius-md: 0.5rem;
  --radius-lg: 0.75rem;
  --radius-xl: 1rem;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

/* Apply base styles */
body {
  font-family: var(--font-primary);
  font-size: var(--text-base);
  color: var(--color-text-primary);
  background: var(--color-bg-primary);
}
```

#### Step 1.4: Google Fonts 추가

**Add to all HTML files** in `<head>`:

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700&display=swap" rel="stylesheet">
```

---

### Day 3: 레이아웃 + 컴포넌트 CSS

#### Step 2.1: Layout CSS

**File**: `prototypes/css/layout.css`

```css
/* Container */
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

@media (min-width: 768px) {
  .container {
    padding: 0 var(--space-8);
  }
}

/* Grid System */
.grid-2 {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-6);
}

@media (min-width: 768px) {
  .grid-2 {
    grid-template-columns: repeat(2, 1fr);
  }
}

.grid-3 {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-6);
}

@media (min-width: 1024px) {
  .grid-3 {
    grid-template-columns: repeat(3, 1fr);
  }
}

.grid-4 {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-6);
}

@media (min-width: 768px) {
  .grid-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .grid-4 {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* Section */
.section {
  padding: var(--space-12) 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-text-primary);
}

.section-link {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-primary-700);
  transition: color 0.2s;
}

.section-link:hover {
  color: var(--color-primary-800);
}
```

#### Step 2.2: Components CSS

**File**: `prototypes/css/components.css`

```css
/* Button Component */
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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--color-primary-700);
  color: white;
}

.btn-primary:hover {
  background: var(--color-primary-800);
}

.btn-secondary {
  background: var(--color-secondary-600);
  color: white;
}

.btn-outline {
  background: transparent;
  color: var(--color-primary-700);
  border: 2px solid var(--color-primary-700);
}

.btn-outline:hover {
  background: var(--color-primary-50);
}

/* Card Component */
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
  border-bottom: 1px solid var(--color-border);
}

.card-body {
  padding: var(--space-6);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border-top: 1px solid var(--color-border);
  background: var(--color-bg-secondary);
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
  line-height: 1.75;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Badge Component */
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
}

.badge-primary {
  background: var(--color-primary-100);
  color: var(--color-primary-700);
}

.badge-warning {
  background: #FEF3C7;
  color: var(--color-secondary-600);
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

/* Form Controls */
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
  border-color: var(--color-primary-700);
  box-shadow: 0 0 0 3px rgba(29, 78, 216, 0.1);
}
```

---

### Day 4-5: 홈페이지 (index.html)

#### Step 3.1: 홈페이지 HTML 구조

**File**: `prototypes/index.html`

```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>성북교회 - 하나님의 사랑을 전하는 교회</title>

  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700&display=swap" rel="stylesheet">

  <!-- CSS -->
  <link rel="stylesheet" href="/css/reset.css">
  <link rel="stylesheet" href="/css/variables.css">
  <link rel="stylesheet" href="/css/layout.css">
  <link rel="stylesheet" href="/css/components.css">
  <link rel="stylesheet" href="/css/navigation.css">
  <link rel="stylesheet" href="/css/pages.css">
</head>
<body>
  <!-- Skip Navigation -->
  <a href="#main-content" class="skip-link">본문으로 건너뛰기</a>

  <!-- Navigation -->
  <nav class="navbar" role="banner">
    <div class="container navbar-container">
      <a href="/" class="navbar-logo">
        <span>성북교회</span>
      </a>

      <!-- Desktop Menu -->
      <ul class="navbar-menu">
        <li><a href="/pages/notices/list.html" class="navbar-link">교회 소식</a></li>
        <li><a href="/pages/sermons/list.html" class="navbar-link">설교 말씀</a></li>
        <li><a href="/pages/worship/schedule.html" class="navbar-link">예배 안내</a></li>
        <li><a href="/pages/galleries/image-list.html" class="navbar-link">갤러리</a></li>
        <li><a href="/pages/about/church.html" class="navbar-link">교회 소개</a></li>
      </ul>

      <!-- Mobile Toggle -->
      <button class="navbar-toggle" aria-label="메뉴 열기" aria-expanded="false">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>
  </nav>

  <!-- Mobile Menu Overlay -->
  <div class="mobile-menu">
    <div class="mobile-menu-header">
      <span>성북교회</span>
      <button class="mobile-menu-close" aria-label="메뉴 닫기">×</button>
    </div>
    <nav class="mobile-menu-nav">
      <a href="/pages/notices/list.html">교회 소식</a>
      <a href="/pages/sermons/list.html">설교 말씀</a>
      <a href="/pages/worship/schedule.html">예배 안내</a>
      <a href="/pages/galleries/image-list.html">갤러리</a>
      <a href="/pages/about/church.html">교회 소개</a>
    </nav>
  </div>

  <!-- Main Content -->
  <main id="main-content" role="main">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">하나님의 사랑을 전하는 교회</h1>
        <p class="hero-subtitle">성북교회에 오신 것을 환영합니다</p>
        <div class="hero-actions">
          <a href="/pages/worship/schedule.html" class="btn btn-primary">예배 안내</a>
          <a href="/pages/about/church.html" class="btn btn-outline">교회 소개</a>
        </div>
      </div>
    </section>

    <!-- Quick Links -->
    <section class="section quick-links">
      <div class="container">
        <div class="grid-4">
          <a href="/pages/worship/schedule.html" class="quick-link-card">
            <div class="quick-link-icon">📖</div>
            <h3>예배 안내</h3>
          </a>
          <a href="/pages/notices/list.html" class="quick-link-card">
            <div class="quick-link-icon">📢</div>
            <h3>공지사항</h3>
          </a>
          <a href="/pages/sermons/list.html" class="quick-link-card">
            <div class="quick-link-icon">🎤</div>
            <h3>설교 말씀</h3>
          </a>
          <a href="/pages/galleries/image-list.html" class="quick-link-card">
            <div class="quick-link-icon">📷</div>
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
          <a href="/pages/notices/list.html" class="section-link">전체보기 →</a>
        </div>
        <div class="grid-3">
          <!-- Notice Card 1 -->
          <article class="card">
            <div class="card-header">
              <span class="badge badge-primary">일반</span>
              <time class="card-date">2026.02.04</time>
            </div>
            <div class="card-body">
              <h3 class="card-title">주일예배 시간 변경 안내</h3>
              <p class="card-description">
                2월 둘째 주부터 주일예배 시간이 오전 11시로 변경됩니다. 많은 참여 부탁드립니다.
              </p>
            </div>
            <div class="card-footer">
              <span class="card-meta">조회 123</span>
              <a href="/pages/notices/detail.html" class="card-link">자세히 보기 →</a>
            </div>
          </article>

          <!-- Notice Card 2 -->
          <article class="card">
            <div class="card-header">
              <span class="badge badge-warning">중요</span>
              <time class="card-date">2026.02.03</time>
            </div>
            <div class="card-body">
              <h3 class="card-title">새벽기도회 일정 안내</h3>
              <p class="card-description">
                매주 금요일 오전 5시 30분 새벽기도회가 진행됩니다.
              </p>
            </div>
            <div class="card-footer">
              <span class="card-meta">조회 89</span>
              <a href="/pages/notices/detail.html" class="card-link">자세히 보기 →</a>
            </div>
          </article>

          <!-- Notice Card 3 -->
          <article class="card">
            <div class="card-header">
              <span class="badge badge-primary">행사</span>
              <time class="card-date">2026.02.02</time>
            </div>
            <div class="card-body">
              <h3 class="card-title">2월 전교인 수련회</h3>
              <p class="card-description">
                2월 15일-17일 전교인 수련회가 열립니다. 많은 참여 부탁드립니다.
              </p>
            </div>
            <div class="card-footer">
              <span class="card-meta">조회 245</span>
              <a href="/pages/notices/detail.html" class="card-link">자세히 보기 →</a>
            </div>
          </article>
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
  </main>

  <!-- Footer -->
  <footer class="footer" role="contentinfo">
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

#### Step 3.2: Navigation JavaScript

**File**: `prototypes/js/navigation.js`

```javascript
// Mobile Menu Toggle
class MobileNav {
  constructor() {
    this.toggle = document.querySelector('.navbar-toggle');
    this.mobileMenu = document.querySelector('.mobile-menu');
    this.closeBtn = document.querySelector('.mobile-menu-close');

    this.init();
  }

  init() {
    if (!this.toggle || !this.mobileMenu) return;

    this.toggle.addEventListener('click', () => this.open());
    this.closeBtn.addEventListener('click', () => this.close());

    // Close on outside click
    this.mobileMenu.addEventListener('click', (e) => {
      if (e.target === this.mobileMenu) {
        this.close();
      }
    });

    // Close on escape key
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.mobileMenu.classList.contains('active')) {
        this.close();
      }
    });
  }

  open() {
    this.mobileMenu.classList.add('active');
    this.toggle.setAttribute('aria-expanded', 'true');
    document.body.style.overflow = 'hidden';
  }

  close() {
    this.mobileMenu.classList.remove('active');
    this.toggle.setAttribute('aria-expanded', 'false');
    document.body.style.overflow = '';
  }
}

// Initialize
new MobileNav();
```

#### Step 3.3: Navigation CSS

**File**: `prototypes/css/navigation.css`

```css
/* Navbar */
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
}

.navbar-logo {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-primary-700);
}

.navbar-menu {
  display: none;
  gap: var(--space-8);
}

@media (min-width: 768px) {
  .navbar-menu {
    display: flex;
  }
}

.navbar-link {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-text-secondary);
  transition: color 0.2s;
}

.navbar-link:hover {
  color: var(--color-primary-700);
}

/* Mobile Toggle */
.navbar-toggle {
  display: flex;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: var(--space-2);
}

@media (min-width: 768px) {
  .navbar-toggle {
    display: none;
  }
}

.navbar-toggle span {
  width: 24px;
  height: 3px;
  background: var(--color-text-primary);
  border-radius: var(--radius-sm);
  transition: all 0.3s;
}

/* Mobile Menu */
.mobile-menu {
  position: fixed;
  inset: 0;
  z-index: 200;
  background: rgba(0, 0, 0, 0.5);
  display: none;
}

.mobile-menu.active {
  display: block;
}

.mobile-menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border);
}

.mobile-menu-close {
  font-size: 2rem;
  background: none;
  border: none;
  cursor: pointer;
}

.mobile-menu-nav {
  background: var(--color-bg-primary);
  height: calc(100vh - 80px);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.mobile-menu-nav a {
  padding: var(--space-4);
  font-size: var(--text-lg);
  font-weight: var(--font-medium);
  color: var(--color-text-primary);
  border-radius: var(--radius-md);
  transition: background 0.2s;
}

.mobile-menu-nav a:hover {
  background: var(--color-bg-secondary);
}
```

---

## 📋 Week 2-4: Detailed Implementation Steps

I'll provide detailed step-by-step implementation guides for:

### Week 2: Detail Pages
- Day 1-2: 공지사항 상세 + 설교 상세 (Audio Player)
- Day 3: 이미지 갤러리 (Lightbox)
- Day 4-5: 간증 목록/상세

### Week 3: Extended Pages
- Day 1-2: 영상 갤러리 + 기도 제목
- Day 3-4: 교회 소개 + 사역 소개
- Day 5: 주보 + 헌금 + 선교

### Week 4: Admin UI
- Day 1-2: 관리자 대시보드
- Day 3-4: 콘텐츠 관리 폼
- Day 5: 최종 리뷰 + 정리

---

## ✅ Implementation Checklist

### Week 1: Foundation ✓
- [ ] Step 1.1: 프로젝트 디렉토리 생성
- [ ] Step 1.2: CSS 리셋 파일 (`reset.css`)
- [ ] Step 1.3: CSS Variables (`variables.css`)
- [ ] Step 1.4: Google Fonts 추가
- [ ] Step 2.1: Layout CSS (`layout.css`)
- [ ] Step 2.2: Components CSS (`components.css`)
- [ ] Step 3.1: 홈페이지 HTML (`index.html`)
- [ ] Step 3.2: Navigation JS (`navigation.js`)
- [ ] Step 3.3: Navigation CSS (`navigation.css`)

### Week 2: Detail Pages
- [ ] 공지사항 목록 (`pages/notices/list.html`)
- [ ] 공지사항 상세 (`pages/notices/detail.html`)
- [ ] 설교 목록 (`pages/sermons/list.html`)
- [ ] 설교 상세 + Audio Player (`pages/sermons/detail.html`)
- [ ] 예배 일정 (`pages/worship/schedule.html`)
- [ ] 라이브 예배 (`pages/worship/live.html`)

### Week 3: Extended Pages
- [ ] 이미지 갤러리 목록 (`pages/galleries/image-list.html`)
- [ ] 이미지 갤러리 상세 + Lightbox (`pages/galleries/image-detail.html`)
- [ ] 영상 갤러리 (`pages/galleries/video-list.html`)
- [ ] 간증 목록 (`pages/community/testimonies.html`)
- [ ] 기도 제목 (`pages/community/prayer-requests.html`)
- [ ] 교회 소개 (`pages/about/church.html`)
- [ ] 목회자 소개 (`pages/about/pastors.html`)
- [ ] 사역 소개 (`pages/about/ministries.html`)

### Week 4: Admin UI
- [ ] 관리자 대시보드 (`pages/admin/dashboard.html`)
- [ ] 콘텐츠 등록/수정 폼 (`pages/admin/content-form.html`)
- [ ] 승인 관리 (`pages/admin/approval.html`)

---

## 🧪 Testing & Validation

### Browser Testing
```bash
# Test in multiple browsers
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
```

### Responsive Testing
```bash
# Test breakpoints
- Mobile: 375px (iPhone), 414px (iPhone Plus)
- Tablet: 768px (iPad), 1024px (iPad Pro)
- Desktop: 1280px, 1920px
```

### Accessibility Testing
```bash
# Tools
- Chrome Lighthouse (접근성 점수 90+ 목표)
- WAVE Browser Extension
- axe DevTools
- Screen Reader (NVDA/JAWS)
```

### Performance Testing
```bash
# Core Web Vitals
- LCP (Largest Contentful Paint) < 2.5s
- FID (First Input Delay) < 100ms
- CLS (Cumulative Layout Shift) < 0.1
```

---

## 🚀 Next Steps

### After Week 1 Complete:
1. ✅ Review 홈페이지 with stakeholders
2. ✅ Run Lighthouse accessibility test
3. ✅ Test mobile responsive design
4. ✅ Proceed to Week 2 (Detail Pages)

### After Week 4 Complete:
1. ✅ Complete all 30+ pages
2. ✅ Run comprehensive QA testing
3. ✅ Document all components in README.md
4. ✅ Execute `/pdca analyze mockup` (Gap Analysis)

---

## 📚 Resources

### Development Tools
- **VS Code**: Code editor
- **Live Server Extension**: Local development server
- **Prettier**: Code formatting
- **Chrome DevTools**: Debugging

### Design Resources
- **Icons**: Heroicons (https://heroicons.com)
- **Images**: Unsplash (https://unsplash.com)
- **Fonts**: Google Fonts (Noto Sans KR)

### Documentation
- **MDN Web Docs**: HTML/CSS/JS reference
- **Can I Use**: Browser compatibility
- **WCAG Guidelines**: Accessibility standards

---

**문서 버전**: 1.0
**마지막 수정**: 2026-02-04
**작성자**: bkit PDCA Agent
**PDCA Phase**: Do (Implementation)
**Estimated Duration**: 4주 (20 working days)
