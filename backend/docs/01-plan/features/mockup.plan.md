# Phase 3: UI/UX Mockup & Prototype Design - 계획서

> **Feature**: 성북교회 홈페이지 UI/UX 목업 및 프로토타입
>
> **Phase**: Phase 3 (Mockup & Prototype)
> **Created**: 2026-02-04
> **Status**: Planning
> **Depends On**: Phase 4 Backend API (Completed ✅)

---

## 1. 개요 (Overview)

### 1.1 목적 (Purpose)

Phase 4에서 완료된 백엔드 API를 기반으로 **사용자 경험(UX) 중심의 프론트엔드 UI 목업을 설계**하여 실제 구현 전 디자인 검증 및 사용자 피드백을 수집합니다.

### 1.2 범위 (Scope)

- **7대 기능군**에 대한 사용자 인터페이스 설계
- HTML/CSS/JavaScript 기반 인터랙티브 프로토타입 제작
- 반응형 디자인 (모바일/태블릿/데스크톱)
- 디자인 시스템 초안 (색상, 타이포그래피, 컴포넌트)

### 1.3 제외 사항 (Out of Scope)

- 프로덕션 코드 작성 (Next.js 구현은 Phase 6)
- 백엔드 API 통합 (Phase 6에서 진행)
- 복잡한 애니메이션 및 인터랙션 (Phase 6에서 고도화)

---

## 2. 설계 원칙 (Design Principles)

### 2.1 사용자 중심 설계 (User-Centered Design)

```
Primary User Personas:
1. 👵 장년층 교인 (60대 이상)
   - 큰 글씨, 명확한 네비게이션
   - 단순한 UI 구조

2. 👨‍👩‍👧 중장년 가족 (30-50대)
   - 빠른 정보 접근
   - 모바일 우선 설계

3. 👨‍💼 교회 관리자
   - 효율적인 콘텐츠 관리
   - 명확한 관리자 UI
```

### 2.2 접근성 (Accessibility)

- **WCAG 2.1 AA 준수**
- 최소 명암비 4.5:1
- 키보드 네비게이션 지원
- 스크린 리더 호환

### 2.3 반응형 디자인 (Responsive Design)

```
Breakpoints:
- Mobile:  320px - 767px
- Tablet:  768px - 1023px
- Desktop: 1024px+
```

---

## 3. 7대 기능군 목업 계획

### 3.1 홈페이지 & 네비게이션

#### 목업 화면
1. **메인 홈페이지**
   - Hero Section (라이브 예배 상태 표시)
   - 최신 공지사항 (3개)
   - 주간 예배 일정
   - 최신 설교 (카드형 레이아웃)
   - Footer (교회 정보, 오시는 길)

2. **글로벌 네비게이션**
   - 상단 메뉴바 (Desktop)
   - 햄버거 메뉴 (Mobile)
   - Mega Menu (예배/교육/사역)

#### 와이어프레임 우선순위
- ✅ P0: Desktop Home
- ✅ P0: Mobile Home
- ✅ P1: Navigation (Desktop/Mobile)

---

### 3.2 공지사항 관리 (Notice Management)

#### API 매핑
```
Backend APIs:
GET    /api/notices              → 목록 페이지
GET    /api/notices/{id}         → 상세 페이지
GET    /api/notices/category/{cat} → 카테고리 필터
GET    /api/notices/search       → 검색 결과
```

#### 목업 화면
1. **공지사항 목록 페이지**
   - 카테고리 탭 (일반/중요/행사)
   - 카드형 레이아웃 (썸네일 + 제목 + 날짜)
   - 페이지네이션 (10개씩)
   - 상단 고정 공지 (Fixed Notice)

2. **공지사항 상세 페이지**
   - 제목 + 카테고리 배지
   - 날짜/조회수
   - 본문 (Rich Text)
   - 첨부파일 다운로드
   - 이전글/다음글 네비게이션

3. **공지사항 검색 페이지**
   - 검색 입력창
   - 필터 (카테고리, 날짜)
   - 검색 결과 목록

#### 와이어프레임 우선순위
- ✅ P0: 목록 페이지 (Desktop)
- ✅ P0: 목록 페이지 (Mobile)
- ✅ P1: 상세 페이지
- ✅ P2: 검색 페이지

---

### 3.3 설교 관리 (Sermon Management)

#### API 매핑
```
Backend APIs:
GET    /api/sermons              → 목록 페이지
GET    /api/sermons/{id}         → 상세 페이지
GET    /api/sermons/search       → 검색 결과
```

#### 목업 화면
1. **설교 목록 페이지**
   - 연도별 필터
   - 카드형 레이아웃 (썸네일 + 설교제목 + 본문 + 목회자)
   - 오디오 플레이어 컨트롤
   - 페이지네이션

2. **설교 상세 페이지**
   - 설교 제목 + 본문
   - 오디오/비디오 플레이어
   - 목회자 정보
   - 예배 정보 (연계된 Worship)
   - 성경 본문

3. **설교 검색 페이지**
   - 검색 입력창
   - 필터 (날짜, 목회자, 성경책)
   - 검색 결과 목록

#### 와이어프레임 우선순위
- ✅ P0: 목록 페이지 (Desktop)
- ✅ P0: 목록 페이지 (Mobile)
- ✅ P1: 상세 페이지 (오디오 플레이어)
- ✅ P2: 검색 페이지

---

### 3.4 예배 일정 (Worship Schedule)

#### API 매핑
```
Backend APIs:
GET    /api/worship              → 예배 일정 페이지
GET    /api/worship/live-now     → 라이브 예배 표시
GET    /api/youtube/lives        → YouTube 라이브 임베드
```

#### 목업 화면
1. **예배 일정 페이지**
   - 주간 예배 일정 (테이블 또는 캘린더형)
   - 라이브 예배 상태 (Live Badge)
   - 예배 유형별 필터 (주일예배/새벽기도/수요예배)
   - 예배 장소 정보

2. **라이브 예배 페이지**
   - YouTube 라이브 임베드
   - 채팅 (Optional)
   - 예배 순서 안내
   - 관련 설교 링크

#### 와이어프레임 우선순위
- ✅ P0: 예배 일정 (Desktop)
- ✅ P0: 예배 일정 (Mobile)
- ✅ P0: 라이브 페이지

---

### 3.5 멀티미디어 (Multimedia)

#### API 매핑
```
Backend APIs:
GET    /api/galleries            → 갤러리 목록
GET    /api/galleries/{id}       → 갤러리 상세
GET    /api/video-galleries      → 영상 갤러리 목록
GET    /api/youtube/playlists    → YouTube 재생목록
```

#### 목업 화면
1. **이미지 갤러리 목록**
   - 그리드 레이아웃 (3열 Desktop, 2열 Mobile)
   - 갤러리 카드 (대표 이미지 + 제목 + 날짜)
   - 카테고리 필터

2. **이미지 갤러리 상세**
   - Lightbox 이미지 뷰어
   - 이미지 그리드 (Masonry 레이아웃)
   - 이미지 슬라이드쇼

3. **영상 갤러리**
   - YouTube 비디오 그리드
   - 비디오 카드 (썸네일 + 제목 + 재생 버튼)

4. **YouTube 재생목록**
   - 재생목록 카드
   - 재생목록 상세 (비디오 리스트)

#### 와이어프레임 우선순위
- ✅ P0: 이미지 갤러리 목록 (Desktop)
- ✅ P0: 이미지 갤러리 상세 (Lightbox)
- ✅ P1: 영상 갤러리
- ✅ P2: YouTube 재생목록

---

### 3.6 커뮤니티 (Community)

#### API 매핑
```
Backend APIs:
GET    /api/testimonies          → 간증 목록
GET    /api/testimonies/{id}     → 간증 상세
GET    /api/prayer-requests      → 기도 제목 목록
POST   /api/prayer-requests      → 기도 제목 등록
```

#### 목업 화면
1. **간증 목록 페이지**
   - 카드형 레이아웃 (썸네일 + 제목 + 작성자 + 날짜)
   - 페이지네이션
   - 검색 기능

2. **간증 상세 페이지**
   - 제목 + 작성자 + 날짜
   - 본문 (Rich Text)
   - 이전글/다음글 네비게이션

3. **기도 제목 목록 페이지**
   - 리스트형 레이아웃
   - 승인된 기도 제목만 표시
   - 작성자 익명 처리

4. **기도 제목 등록 폼**
   - 이름 (익명 가능)
   - 제목
   - 내용
   - 개인정보 동의

#### 와이어프레임 우선순위
- ✅ P0: 간증 목록 (Desktop)
- ✅ P0: 간증 목록 (Mobile)
- ✅ P1: 간증 상세
- ✅ P1: 기도 제목 목록
- ✅ P2: 기도 제목 등록 폼

---

### 3.7 교회 소개 & 행정 (About Church & Administration)

#### API 매핑
```
Backend APIs:
GET    /api/staff                → 교직원 목록
GET    /api/pastors              → 목회자 소개
GET    /api/ministries           → 사역 목록
GET    /api/bulletins            → 주보 목록
GET    /api/donation-accounts    → 헌금 계좌
GET    /api/missions             → 선교 활동
```

#### 목업 화면
1. **교회 소개 페이지**
   - 교회 비전/역사
   - 목회자 소개 (사진 + 약력)
   - 교직원 소개 (그리드 레이아웃)

2. **사역 소개 페이지**
   - 사역 카드 (아이콘 + 제목 + 설명)
   - 사역 상세 (사진 + 상세 설명)

3. **주보 다운로드 페이지**
   - 최신 주보 (날짜별 리스트)
   - PDF 다운로드 버튼
   - 조회수 표시

4. **헌금 안내 페이지**
   - 헌금 종류별 계좌 정보
   - 복사 버튼 (Clipboard)
   - QR 코드 (Optional)

5. **선교 활동 페이지**
   - 선교지 카드 (국가 + 선교사 + 사진)
   - 선교 소식 (블로그형 레이아웃)

#### 와이어프레임 우선순위
- ✅ P0: 교회 소개 (Desktop)
- ✅ P0: 목회자 소개
- ✅ P1: 사역 소개
- ✅ P1: 주보 다운로드
- ✅ P1: 헌금 안내
- ✅ P2: 선교 활동

---

### 3.8 관리자 페이지 (Admin Panel)

#### API 매핑
```
Backend APIs (Admin Only):
POST   /api/notices              → 공지 등록
PUT    /api/notices/{id}         → 공지 수정
POST   /api/sermons              → 설교 등록
GET    /api/prayer-requests/pending → 기도 제목 승인 대기
POST   /api/prayer-requests/{id}/approve → 승인
```

#### 목업 화면
1. **관리자 대시보드**
   - 통계 카드 (공지/설교/갤러리/기도 제목)
   - 최근 활동 로그
   - 빠른 작업 (Quick Actions)

2. **콘텐츠 관리 (CRUD)**
   - 목록 페이지 (테이블 레이아웃)
   - 등록/수정 폼 (Rich Text Editor)
   - 삭제 확인 모달

3. **승인 대기 관리**
   - 승인 대기 목록 (기도 제목, 간증)
   - 승인/거부 버튼
   - 사유 입력

#### 와이어프레임 우선순위
- ✅ P0: 관리자 대시보드
- ✅ P1: 콘텐츠 등록 폼 (공지/설교)
- ✅ P2: 승인 관리

---

## 4. 디자인 시스템 초안 (Design System Draft)

### 4.1 색상 팔레트 (Color Palette)

```css
/* Primary Colors */
--primary-blue: #1E40AF;      /* 교회 메인 색상 */
--primary-light: #60A5FA;     /* 버튼 호버 */
--primary-dark: #1E3A8A;      /* 헤더/푸터 */

/* Secondary Colors */
--secondary-gold: #D97706;    /* 강조 요소 */
--secondary-green: #059669;   /* 성공 메시지 */

/* Neutral Colors */
--gray-50: #F9FAFB;           /* 배경 */
--gray-100: #F3F4F6;          /* 카드 배경 */
--gray-300: #D1D5DB;          /* 테두리 */
--gray-700: #374151;          /* 본문 텍스트 */
--gray-900: #111827;          /* 제목 */

/* Semantic Colors */
--success: #10B981;           /* 성공 */
--warning: #F59E0B;           /* 경고 */
--error: #EF4444;             /* 오류 */
--info: #3B82F6;              /* 정보 */
```

### 4.2 타이포그래피 (Typography)

```css
/* Font Family */
--font-primary: 'Noto Sans KR', sans-serif;
--font-secondary: 'Nanum Gothic', sans-serif;

/* Font Sizes */
--text-xs: 0.75rem;   /* 12px - 메타 정보 */
--text-sm: 0.875rem;  /* 14px - 본문 작은 글씨 */
--text-base: 1rem;    /* 16px - 기본 본문 */
--text-lg: 1.125rem;  /* 18px - 강조 텍스트 */
--text-xl: 1.25rem;   /* 20px - 소제목 */
--text-2xl: 1.5rem;   /* 24px - 제목 */
--text-3xl: 1.875rem; /* 30px - 대제목 */
--text-4xl: 2.25rem;  /* 36px - Hero 제목 */

/* Font Weights */
--font-normal: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
```

### 4.3 컴포넌트 (Components)

#### 버튼 (Button)
```css
/* Primary Button */
.btn-primary {
  background: var(--primary-blue);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: var(--primary-light);
}

/* Secondary Button */
.btn-secondary {
  background: transparent;
  color: var(--primary-blue);
  border: 2px solid var(--primary-blue);
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
}
```

#### 카드 (Card)
```css
.card {
  background: white;
  border-radius: 0.75rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
```

#### 배지 (Badge)
```css
.badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: var(--text-xs);
  font-weight: 600;
}

.badge-primary { background: var(--primary-blue); color: white; }
.badge-success { background: var(--success); color: white; }
.badge-warning { background: var(--warning); color: white; }
```

### 4.4 레이아웃 (Layout)

```css
/* Container */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Grid System */
.grid-2 { display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.5rem; }
.grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.5rem; }
.grid-4 { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; }

/* Responsive Grid */
@media (max-width: 767px) {
  .grid-2, .grid-3, .grid-4 {
    grid-template-columns: 1fr;
  }
}
```

---

## 5. 프로토타입 제작 도구 (Prototyping Tools)

### 5.1 HTML/CSS/JavaScript 프로토타입

**기술 스택**:
- **HTML5**: 시맨틱 마크업
- **CSS3**: Flexbox, Grid, CSS Variables
- **Vanilla JavaScript**: 기본 인터랙션 (슬라이더, 모달, 탭)
- **Optional**: Alpine.js (경량 반응형 라이브러리)

**장점**:
- ✅ 실제 코드 재사용 가능 (Phase 6에서 Next.js 변환)
- ✅ 빠른 프로토타이핑
- ✅ 브라우저에서 즉시 테스트

### 5.2 디자인 도구 (Optional)

**Figma**:
- 와이어프레임 고도화
- 디자인 시스템 문서화
- 개발자 핸드오프

**사용 시나리오**:
- HTML 프로토타입 전 빠른 스케치
- 디자인 시스템 컴포넌트 라이브러리
- 클라이언트 프레젠테이션

---

## 6. 목업 제작 우선순위 (Priority)

### Phase 3.1: Core Pages (1주)
- ✅ P0: 홈페이지 (Desktop/Mobile)
- ✅ P0: 네비게이션 (Mega Menu, 햄버거 메뉴)
- ✅ P0: 공지사항 목록 (Desktop/Mobile)
- ✅ P0: 설교 목록 (Desktop/Mobile)
- ✅ P0: 예배 일정 (Desktop/Mobile)

### Phase 3.2: Detail Pages (1주)
- ✅ P1: 공지사항 상세
- ✅ P1: 설교 상세 (오디오 플레이어)
- ✅ P1: 라이브 예배 페이지
- ✅ P1: 이미지 갤러리 상세 (Lightbox)
- ✅ P1: 간증 목록/상세

### Phase 3.3: Extended Pages (1주)
- ✅ P2: 영상 갤러리
- ✅ P2: 기도 제목 목록/등록
- ✅ P2: 교회 소개/사역 소개
- ✅ P2: 주보 다운로드
- ✅ P2: 헌금 안내

### Phase 3.4: Admin UI (1주)
- ✅ P2: 관리자 대시보드
- ✅ P2: 콘텐츠 등록 폼
- ✅ P2: 승인 관리

---

## 7. 성공 기준 (Success Criteria)

### 7.1 완성도 (Completeness)

| 기준 | 목표 | 측정 방법 |
|------|------|----------|
| **화면 수** | 30+ 화면 | 와이어프레임 + HTML 프로토타입 |
| **반응형 화면** | 100% | Mobile/Tablet/Desktop 테스트 |
| **디자인 시스템** | 80% | 색상/폰트/컴포넌트 문서화 |
| **인터랙션** | 50% | 기본 인터랙션 (클릭, 호버, 모달) |

### 7.2 사용성 (Usability)

- ✅ **접근성**: WCAG 2.1 AA 준수 (명암비, 키보드 네비게이션)
- ✅ **모바일 우선**: 터치 최적화, 큰 버튼
- ✅ **직관성**: 3-클릭 룰 (주요 콘텐츠 3번 클릭 내 도달)
- ✅ **일관성**: 동일한 UI 패턴 적용

### 7.3 성능 (Performance)

- ✅ **로딩 속도**: HTML 프로토타입 < 1초
- ✅ **이미지 최적화**: WebP, Lazy Loading
- ✅ **CSS 최적화**: Minify, Critical CSS

---

## 8. 품질 검증 (Quality Assurance)

### 8.1 디자인 리뷰 (Design Review)

**리뷰 항목**:
1. **색상 일관성**: 디자인 시스템 준수
2. **타이포그래피**: 가독성 (최소 16px 본문)
3. **레이아웃**: 정렬, 여백, 균형
4. **반응형**: 3가지 해상도 테스트

### 8.2 사용자 테스트 (User Testing)

**테스트 시나리오**:
1. **장년층 사용자** (60대+)
   - 최신 설교 찾기
   - 예배 일정 확인

2. **중장년 사용자** (30-50대)
   - 공지사항 검색
   - 갤러리 보기

3. **관리자**
   - 공지 등록
   - 기도 제목 승인

### 8.3 접근성 검증 (Accessibility Audit)

**도구**:
- **WAVE**: 웹 접근성 자동 검사
- **Lighthouse**: Chrome DevTools 접근성 점수
- **Axe DevTools**: 접근성 위반 감지

**목표**:
- ✅ Lighthouse 접근성 점수 90+
- ✅ 키보드 네비게이션 100% 지원
- ✅ 스크린 리더 호환

---

## 9. 산출물 (Deliverables)

### 9.1 문서 (Documentation)

1. **디자인 시스템 문서** (`design-system.md`)
   - 색상 팔레트
   - 타이포그래피
   - 컴포넌트 라이브러리

2. **와이어프레임 문서** (`wireframes.md`)
   - 화면별 와이어프레임 (저해상도)
   - 사용자 플로우

3. **프로토타입 링크** (`prototype-links.md`)
   - HTML 프로토타입 URL
   - Figma 링크 (Optional)

### 9.2 코드 (Code)

```
/prototypes/
  ├── index.html              # 홈페이지
  ├── notices.html            # 공지사항 목록
  ├── notice-detail.html      # 공지사항 상세
  ├── sermons.html            # 설교 목록
  ├── sermon-detail.html      # 설교 상세
  ├── worship.html            # 예배 일정
  ├── galleries.html          # 이미지 갤러리
  ├── testimonies.html        # 간증 목록
  ├── prayer-requests.html    # 기도 제목
  ├── about.html              # 교회 소개
  ├── ministries.html         # 사역 소개
  ├── admin-dashboard.html    # 관리자 대시보드
  ├── css/
  │   ├── reset.css           # CSS 리셋
  │   ├── variables.css       # CSS 변수
  │   ├── components.css      # 컴포넌트
  │   └── layout.css          # 레이아웃
  ├── js/
  │   ├── navigation.js       # 네비게이션
  │   ├── modal.js            # 모달
  │   └── slider.js           # 슬라이더
  └── assets/
      ├── images/             # 목업용 이미지
      └── icons/              # 아이콘
```

---

## 10. 리스크 관리 (Risk Management)

### 10.1 잠재적 리스크

| 리스크 | 영향도 | 완화 전략 |
|--------|--------|----------|
| **디자이너 부재** | 높음 | 트렌드 분석, UI 라이브러리 활용 |
| **기술 제약** | 중간 | HTML/CSS/JS 기본 기술 활용 |
| **시간 부족** | 중간 | 우선순위 기반 단계별 제작 |
| **피드백 수집 어려움** | 낮음 | 온라인 설문, 화상 미팅 |

### 10.2 의존성 (Dependencies)

- ✅ **Phase 4 완료**: 백엔드 API 스펙 확정 (완료됨)
- ⏳ **디자인 에셋**: 로고, 브랜딩 가이드 (필요 시 요청)
- ⏳ **콘텐츠**: 샘플 이미지, 텍스트 (Lorem Ipsum 대체 가능)

---

## 11. 타임라인 (Timeline)

### 11.1 전체 일정

```
Week 1: Phase 3.1 - Core Pages
  Day 1-2: 홈페이지 + 네비게이션 (Desktop/Mobile)
  Day 3-4: 공지사항 목록 + 설교 목록
  Day 5: 예배 일정

Week 2: Phase 3.2 - Detail Pages
  Day 1-2: 공지사항 상세 + 설교 상세
  Day 3: 라이브 예배 + 이미지 갤러리
  Day 4-5: 간증 목록/상세

Week 3: Phase 3.3 - Extended Pages
  Day 1-2: 영상 갤러리 + 기도 제목
  Day 3-4: 교회 소개 + 사역 소개
  Day 5: 주보 + 헌금 안내

Week 4: Phase 3.4 - Admin UI
  Day 1-2: 관리자 대시보드
  Day 3-4: 콘텐츠 등록 폼 + 승인 관리
  Day 5: 최종 리뷰 및 정리
```

### 11.2 마일스톤 (Milestones)

- **Week 1 종료**: Core Pages 완료, 디자인 리뷰
- **Week 2 종료**: Detail Pages 완료, 사용자 테스트
- **Week 3 종료**: Extended Pages 완료, 접근성 검증
- **Week 4 종료**: Admin UI 완료, 최종 인수

---

## 12. 다음 단계 (Next Steps)

### Phase 4 (현재)
✅ 백엔드 API 완료 (96% 설계 일치도, 86/100 품질점수)

### Phase 3 (다음)
⏳ UI/UX 목업 및 프로토타입 제작

### Phase 5 (이후)
⏳ 디자인 시스템 구축 (shadcn/ui 기반)

### Phase 6 (이후)
⏳ Next.js App Router 구현 및 API 통합

---

## 13. 참고 자료 (References)

### 13.1 Backend API Documentation
- Phase 4 완료 보고서: `docs/04-report/phase4-completion.report.md`
- API 엔드포인트: 150+ REST APIs
- 기술 스택: Spring Boot 4.0.2, PostgreSQL 18.1, Valkey

### 13.2 Design Inspiration
- **교회 홈페이지 트렌드**: Minimalist, 큰 타이포그래피, Hero 이미지
- **참고 사이트**:
  - Hillsong Church (https://hillsong.com)
  - Saddleback Church (https://saddleback.com)
  - ONNURI Church (https://www.onnuri.or.kr)

### 13.3 Technical Resources
- **CSS Framework**: Tailwind CSS (선택적)
- **Icons**: Heroicons, Lucide Icons
- **Fonts**: Noto Sans KR (Google Fonts)
- **Images**: Unsplash (무료 스톡 이미지)

---

## 14. 승인 (Approval)

| 역할 | 이름 | 승인 날짜 | 서명 |
|------|------|-----------|------|
| **Project Manager** | | | |
| **Lead Designer** | | | |
| **Lead Developer** | | | |
| **Stakeholder** | | | |

---

**문서 버전**: 1.0
**마지막 수정**: 2026-02-04
**작성자**: bkit PDCA Agent
**PDCA Phase**: Plan (Phase 3)
