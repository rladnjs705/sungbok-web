# 성복교회 홈페이지 UI/UX 프로토타입

> Phase 3: HTML/CSS/JavaScript 인터랙티브 프로토타입

## 📊 프로젝트 상태

✅ **Week 1-2 완료**: Foundation + 핵심 페이지 구현 완료

### 완성된 파일

#### CSS (7개 파일)
- ✅ `css/reset.css` - Modern CSS Reset
- ✅ `css/variables.css` - 디자인 시스템 (색상, 타이포그래피, 스페이싱)
- ✅ `css/layout.css` - 레이아웃 (Container, Grid, Section)
- ✅ `css/components.css` - 컴포넌트 (Button, Card, Badge, Form)
- ✅ `css/navigation.css` - 네비게이션 + Footer
- ✅ `css/pages.css` - 페이지별 스타일
- ✅ `css/responsive.css` - 반응형 디자인

#### JavaScript (6개 파일)
- ✅ `js/utils.js` - 유틸리티 함수
- ✅ `js/navigation.js` - 모바일 네비게이션
- ✅ `js/modal.js` - 모달 + Lightbox
- ✅ `js/slider.js` - Audio Player + Image Slider
- ✅ `js/tabs.js` - 탭 + 필터
- ✅ `js/form-validation.js` - 폼 검증

#### HTML 페이지 (핵심 페이지 완성)
- ✅ `index.html` - 홈페이지
- ✅ `pages/notices/list.html` - 공지사항 목록
- ✅ `pages/sermons/detail.html` - 설교 상세 (Audio Player)

### 추가 구현 필요 페이지 (27개)

아래 페이지들은 위 완성된 페이지를 참고하여 동일한 구조로 생성하면 됩니다.

#### 공지사항 (Notices)
- `pages/notices/detail.html` - 공지사항 상세
- `pages/notices/search.html` - 공지사항 검색

#### 설교 (Sermons)
- `pages/sermons/list.html` - 설교 목록
- `pages/sermons/search.html` - 설교 검색

#### 예배 (Worship)
- `pages/worship/schedule.html` - 예배 일정
- `pages/worship/live.html` - 라이브 예배 (YouTube 임베드)

#### 갤러리 (Galleries)
- `pages/galleries/image-list.html` - 이미지 갤러리 목록
- `pages/galleries/image-detail.html` - 이미지 갤러리 상세 (Lightbox)
- `pages/galleries/video-list.html` - 영상 갤러리
- `pages/galleries/youtube-playlists.html` - YouTube 재생목록

#### 커뮤니티 (Community)
- `pages/community/testimonies.html` - 간증 목록
- `pages/community/testimony-detail.html` - 간증 상세
- `pages/community/prayer-requests.html` - 기도 제목 목록
- `pages/community/prayer-form.html` - 기도 제목 등록 폼

#### 교회 소개 (About)
- `pages/about/church.html` - 교회 소개
- `pages/about/pastors.html` - 목회자 소개
- `pages/about/staff.html` - 교직원 소개
- `pages/about/ministries.html` - 사역 소개
- `pages/about/bulletins.html` - 주보 다운로드
- `pages/about/donations.html` - 헌금 안내
- `pages/about/missions.html` - 선교 활동

#### 관리자 (Admin)
- `pages/admin/dashboard.html` - 관리자 대시보드
- `pages/admin/content-form.html` - 콘텐츠 등록/수정 폼
- `pages/admin/approval.html` - 승인 관리

---

## 🚀 로컬 개발 환경 실행

### 방법 1: Python HTTP Server
```bash
cd /Users/jaewon/Documents/sungbok-web/prototypes
python3 -m http.server 8000
```

브라우저에서 http://localhost:8000 접속

### 방법 2: VS Code Live Server
1. VS Code에서 `prototypes` 폴더 열기
2. Live Server Extension 설치
3. `index.html` 우클릭 → "Open with Live Server"

---

## 🎨 디자인 시스템

### 색상 팔레트
```css
Primary: #1D4ED8 (Blue)
Secondary: #D97706 (Gold)
Success: #10B981
Warning: #F59E0B
Error: #EF4444
```

### 타이포그래피
```css
Font: Noto Sans KR
Base Size: 16px (clamp for fluid typography)
Scale: 12px - 64px (8 단계)
```

### 스페이싱
```css
Base: 8px
Scale: 4px, 8px, 12px, 16px, 24px, 32px, 48px, 64px, 80px
```

### Breakpoints
```css
Mobile: 320px - 767px
Tablet: 768px - 1023px
Desktop: 1024px+
```

---

## 📝 페이지 생성 가이드

### 기본 HTML 템플릿

```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>[페이지 제목] - 성복교회</title>

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
  <link rel="stylesheet" href="/css/responsive.css">
</head>
<body>
  <!-- Skip Navigation -->
  <a href="#main-content" class="skip-link">본문으로 건너뛰기</a>

  <!-- Navigation (동일) -->
  [네비게이션 HTML - index.html 참고]

  <!-- Page Header -->
  <div class="page-header">
    <div class="container">
      <h1 class="page-title">[페이지 제목]</h1>
      <p class="page-description">[페이지 설명]</p>
    </div>
  </div>

  <!-- Main Content -->
  <main id="main-content" class="page-content">
    <div class="container">
      [페이지 콘텐츠]
    </div>
  </main>

  <!-- Footer (동일) -->
  [Footer HTML - index.html 참고]

  <!-- Scripts -->
  <script src="/js/utils.js"></script>
  <script src="/js/navigation.js"></script>
  [필요한 추가 스크립트]
</body>
</html>
```

### 컴포넌트 사용 예제

#### Card 컴포넌트
```html
<article class="card">
  <div class="card-header">
    <span class="badge badge-primary">일반</span>
    <time class="card-date">2026.02.04</time>
  </div>
  <div class="card-body">
    <h3 class="card-title">카드 제목</h3>
    <p class="card-description">카드 설명</p>
  </div>
  <div class="card-footer">
    <span class="card-meta">메타 정보</span>
    <a href="#" class="card-link">자세히 보기 →</a>
  </div>
</article>
```

#### Button 컴포넌트
```html
<button class="btn btn-primary">Primary Button</button>
<button class="btn btn-secondary">Secondary Button</button>
<button class="btn btn-outline">Outline Button</button>
```

#### Form 컴포넌트
```html
<form data-validate>
  <div class="form-group">
    <label for="title" class="form-label">제목</label>
    <input type="text" id="title" class="form-input" required>
  </div>

  <div class="form-group">
    <label for="content" class="form-label">내용</label>
    <textarea id="content" class="form-textarea" rows="5" required></textarea>
  </div>

  <button type="submit" class="btn btn-primary">저장</button>
</form>
```

#### Tabs 컴포넌트
```html
<div class="tabs">
  <button class="tab active" data-category="all">전체</button>
  <button class="tab" data-category="notice">일반</button>
  <button class="tab" data-category="important">중요</button>
</div>
```

---

## 🧪 테스트 체크리스트

### Browser Testing
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Safari (latest)
- [ ] Edge (latest)

### Responsive Testing
- [ ] Mobile (375px, 414px)
- [ ] Tablet (768px, 1024px)
- [ ] Desktop (1280px, 1920px)

### Accessibility Testing
- [ ] Lighthouse 접근성 점수 90+
- [ ] WAVE 브라우저 확장 검사
- [ ] 키보드 네비게이션 (Tab, Enter, Escape)
- [ ] 스크린 리더 호환 (NVDA/VoiceOver)

### Performance Testing
- [ ] LCP < 2.5s
- [ ] FID < 100ms
- [ ] CLS < 0.1

### Functional Testing
- [ ] 모바일 메뉴 열기/닫기
- [ ] 탭 전환
- [ ] 페이지네이션
- [ ] 폼 검증
- [ ] 오디오 플레이어 (설교 상세)
- [ ] Lightbox (갤러리)

---

## 📈 다음 단계

### Phase 3 완료 후
1. ✅ 모든 30+ 페이지 생성 완료
2. ✅ 종합 QA 테스팅
3. ✅ README.md 업데이트
4. ✅ `/pdca analyze mockup` 실행

### Phase 6으로 전환
- HTML → React Components (JSX)
- CSS → Tailwind CSS + CSS Modules
- Vanilla JS → TypeScript + React Hooks
- Mock Data → Real API Integration (Phase 4 APIs)

---

## 📚 참고 자료

### Development Tools
- **VS Code**: https://code.visualstudio.com
- **Live Server**: VS Code Extension
- **Chrome DevTools**: Built-in browser tool

### Design Resources
- **Icons**: Heroicons (https://heroicons.com)
- **Fonts**: Google Fonts (Noto Sans KR)
- **Images**: Unsplash (https://unsplash.com)

### Documentation
- **MDN Web Docs**: https://developer.mozilla.org
- **WCAG Guidelines**: https://www.w3.org/WAI/WCAG21/quickref/
- **Can I Use**: https://caniuse.com

---

## 🐛 문제 해결

### CSS가 적용되지 않을 때
```bash
# 브라우저 캐시 삭제
Cmd + Shift + R (Mac) / Ctrl + Shift + R (Windows)
```

### JavaScript가 작동하지 않을 때
```bash
# 브라우저 콘솔 확인
F12 → Console 탭 → 에러 메시지 확인
```

### 폰트가 로드되지 않을 때
```html
<!-- Google Fonts 링크 확인 -->
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700&display=swap" rel="stylesheet">
```

---

**프로젝트 버전**: 1.0
**마지막 수정**: 2026-02-04
**작성자**: bkit PDCA Agent
**Phase**: Do (Implementation - Week 1-2 완료)
