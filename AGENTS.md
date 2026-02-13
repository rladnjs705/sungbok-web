# 성복교회 홈페이지 프로젝트 - AGENTS.md

> **Kimi Code CLI**를 위한 프로젝트 가이드  
> **Generated**: 2026-02-11  
> **Migration From**: Claude Code + BKit + Context7 + Serena

---

## 🎯 프로젝트 개요

**성복교회 공식 홈페이지** - Next.js 16.1 + Spring Boot 4.0.2 기반 풀스택 웹 애플리케이션

### 핵심 특징
- **PDCA 사이클** 기반 개발 (Plan-Do-Check-Act)
- **Cloud Harmony Design System** 적용
- **ISR (Incremental Static Regeneration)** 전략
- **JWT 기반 인증** + Spring Security
- **Podman** 컨테이너화

---

## 🏗️ 기술 스택

### Frontend
| 기술 | 버전 | 용도 |
|------|------|------|
| Next.js | 16.1 | App Router, SSR/SSG/ISR |
| TypeScript | 5.x | 타입 안정성 |
| Tailwind CSS | 4.x | 유틸리티 CSS |
| shadcn/ui | latest | UI 컴포넌트 |
| Framer Motion | 12.x | 애니메이션 |
| TanStack Query | 5.x | 서버 상태 관리 |
| Zustand | 5.x | 클라이언트 상태 관리 |

### Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Boot | 4.0.2 | REST API |
| PostgreSQL | 18.1 | 메인 DB |
| Valkey (Redis) | latest | 캐시/세션 |
| QueryDSL | latest | 타입 안전 쿼리 |
| Spring Security | latest | 인증/인가 |
| SpringDoc OpenAPI | 3.0.1 | API 문서화 |

### DevOps
| 기술 | 용도 |
|------|------|
| Podman | 컨테이너화 |
| Jenkins | CI/CD |
| OCI (Oracle Cloud) | 배포 인프라 |
| Prometheus/Grafana | 모니터링 |

---

## 📁 프로젝트 구조

```
sungbok-web/
├── AGENTS.md                 # ← 이 파일 (Kimi CLI 설정)
├── README.md                 # 프로젝트 소개 (사용자용)
├── frontend/                 # Next.js 애플리케이션
│   ├── src/
│   │   ├── app/             # App Router (pages)
│   │   ├── components/      # React 컴포넌트
│   │   │   ├── ui/          # shadcn/ui 컴포넌트
│   │   │   └── features/    # 기능별 컴포넌트
│   │   ├── lib/             # 유틸리티
│   │   │   ├── api.ts       # API 클라이언트
│   │   │   └── design-tokens.ts  # 디자인 토큰
│   │   └── types/           # TypeScript 타입
│   ├── public/              # 정적 에셋
│   └── next.config.ts       # Next.js 설정
│
├── backend/                  # Spring Boot 애플리케이션
│   ├── src/main/java/       # Java 소스
│   ├── src/main/resources/  # 설정 파일
│   ├── src/test/java/       # 테스트 코드
│   └── podman/              # Podman Compose 설정
│
└── docs/                     # 프로젝트 문서
    ├── guides/              # 개발/배포 가이드
    ├── architecture/        # 아키텍처 문서
    ├── development/         # PDCA 개발 문서
    ├── operations/          # 운영 문서
    └── archive/             # 아카이브된 문서
```

---

## 🎨 디자인 시스템 (Cloud Harmony)

### 3가지 톤 (Tone) 전략

| 섹션 | 톤 | 키워드 | Primary Color |
|------|-----|--------|---------------|
| **메인 페이지** | 활동적/역동적 | Energetic, Modern | #6B84A3 (Cloud Blue) |
| **부서별 페이지** | 고유성/개성 | Unique, Playful | 부서별 상이 |
| **교회/목사 소개** | 진중한/신뢰감 | Trustworthy, Elegant | #1e293b (Slate 800) |

### 타이포그래피
```css
/* 메인 페이지 */
--font-display: 'Unbounded', 'Pretendard Variable', sans-serif;
--font-body: 'Pretendard Variable', sans-serif;

/* 교회 소개 */
--font-display-serious: 'Cormorant Garamond', 'Noto Serif KR', serif;
--font-body-serious: 'Lora', 'Noto Serif KR', serif;
```

### 디자인 토큰 파일
- `frontend/src/lib/design-tokens.ts` - 토큰 정의
- `frontend/src/app/globals.css` - CSS 변수
- `frontend/src/app/layout.tsx` - 폰트 설정

---

## 🔄 개발 워크플로우 (PDCA)

### 개발 순서
1. **Schema** (Phase 1): 도메인 모델 및 용어 정의
2. **Convention** (Phase 2): 코딩 규칙 및 네이밍
3. **Mockup** (Phase 3): UI/UX 프로토타입
4. **API Design** (Phase 4): REST API 설계
5. **Design System** (Phase 5): 재사용 컴포넌트
6. **UI Integration** (Phase 6): Frontend-Backend 연동
7. **SEO & Security** (Phase 7): 검색 최적화 및 보안
8. **Review** (Phase 8): 코드 리뷰 및 품질 검증
9. **Deployment** (Phase 9): 프로덕션 배포

### 완료된 기능 (Archived)
- **Church 기능**: 백엔드 API 150+ endpoints, 품질 점수 95/100
- **Frontend 페이지**: home, about, worship, ministries, news, mission

---

## 🌐 API 정보

### Backend API Base
- **개발**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **운영 (예정)**: `https://api.sungbok.church`

### 주요 엔드포인트
- `/api/notices` - 공지사항
- `/api/sermons` - 설교
- `/api/worship` - 예배
- `/api/events` - 행사
- `/api/galleries` - 갤러리
- `/api/testimonies` - 간증
- `/api/prayer-requests` - 기도제목

### 인증
- JWT Bearer Token 사용
- 관리자 기능에 🔒 표시

---

## ⚡ 성능 최적화 적용

### Frontend
- ✅ ISR (Incremental Static Regeneration)
  - 메인: 3600초
  - 교회 소개: 86400초
  - 설교 목록: 1800초
  - 설교 상세: 300초
  - 뉴스: 600초
- ✅ 동적 임포트 (Dynamic Imports)
- ✅ 폰트 최적화 (next/font)
- ✅ 이미지 최적화 (next/image)

### Backend
- ✅ N+1 쿼리 방지 (@EntityGraph)
- ✅ 레이스 컨디션 방지 (@Modifying)
- ✅ 삭제 안전성 검증 (existsById)
- ✅ 데이터베이스 연결 풀링 (HikariCP)

---

## 🛡️ 보안 가이드라인

### ⚠️ 중요: Spring Security 302 Redirect 문제

**문제**: `permitAll()` 설정에도 불구하고 302 redirect 발생  
**원인**: JWT Filter의 `@Component` 자동 등록  
**해결**: `backend/docs/SECURITY_ARCHITECTURE.md` 참조

### 환경 변수 (필수)
```bash
# JWT
JWT_SECRET=<min-48-chars-base64>  # openssl rand -base64 48
JWT_EXPIRATION=3600000            # 1시간 (prod)

# Database
DB_URL=jdbc:postgresql://.../sungbok_church
DB_USERNAME=sungbok_user
DB_PASSWORD=<secure-password>

# CORS (Production)
CORS_ALLOWED_ORIGINS=https://www.sungbok-church.com

# Profile
SPRING_PROFILES_ACTIVE=prod
```

### 보안 조치
- JWT Secret 기본값 없음 (startup 실패로 강제)
- 32자 이상 필수 검증
- Production profile에서 SQL 로깅 비활성화
- 에러 상세 정보 숨김
- **Spring Security**: `@Component` 금지, `@Bean` 사용 (302 방지)

---

## 🚀 실행 방법

### Frontend
```bash
cd frontend
npm install
npm run dev        # http://localhost:3000
```

### Backend (Podman 권장)
```bash
cd backend/podman
podman-compose up  # http://localhost:8080/api
```

### Backend (로컬)
```bash
cd backend
mvn spring-boot:run
```

---

## 📝 개발 규칙

### 커밋 컨벤션
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅 (기능 변경 없음)
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 설정 등 기타 변경
```

### 코드 스타일
- **Frontend**: TypeScript strict mode, ESLint, Prettier
- **Backend**: Java 21, Lombok, Constructor Injection
- **API-First**: 백엔드 API 설계 후 프론트엔드 구현
- **Security**: JWT Filter는 `@Component` ❌ → `@Bean` ✅ (302 redirect 방지)

### 소통 방식
- 한국어로 소통
- 기술 용어는 한/영 병기 (예: "Component(컴포넌트)")
- Spring Boot 개념은 간단히, Next.js 개념은 상세히 설명

---

## 📚 참고 문서

| 문서 | 위치 | 내용 |
|------|------|------|
| API 문서 | `backend/API_DOCUMENTATION.md` | REST API 상세 |
| 배포 가이드 | `backend/DEPLOYMENT_GUIDE.md` | 프로덕션 배포 |
| 보안 아키텍처 | `backend/docs/SECURITY_ARCHITECTURE.md` | Spring Security 7.x 상세 |
| 보안 치트시트 | `backend/docs/SECURITY_CHEATSHEET.md` | 빠른 참고용 요약 |
| 디자인 전략 | `docs/02-design/FRONTEND-DESIGN-STRATEGY.md` | UI/UX 전략 |
| Next.js 가이드 | `docs/NEXTJS-GUIDE.md` | Spring 개발자용 |
| ISR 전략 | `docs/02-design/ISR-STRATEGY.md` | 캐싱 전략 |

---

## 🔧 유용한 명령어

### Frontend
```bash
# 개발 서버
npm run dev

# 프로덕션 빌드
npm run build

# 린트 검사
npm run lint

# 컴포넌트 추가
npx shadcn@latest add [component-name]
```

### Backend
```bash
# 테스트 실행
./gradlew test

# 빌드
./gradlew clean build

# 실행
./gradlew bootRun
```

---

## 🐛 문제 해결 체크리스트

### 백엔드 시작 실패 시
1. JWT_SECRET 설정 확인 (48자 이상)
2. DB 연결 정보 확인
3. 포트 중복 확인 (`lsof -i :8080`)
4. 로그 권한 확인

### 프론트엔드 빌드 실패 시
1. Node.js 20+ 버전 확인
2. `node_modules` 삭제 후 재설치
3. `.env.local` 파일 확인

---

## 📞 지원

- **문서 이슈**: AGENTS.md 업데이트 필요시 깃 이슈 생성
- **기술 문의**: `docs/` 폴더의 관련 문서 참조
- **긴급 지원**: 프로젝트 관리자 연락

---

## ⚠️ 중요: 코드 수정 규칙

> **절대 위반하지 마세요.**

### 📝 수정 전 필수 절차

1. **문제점 파악 후 보고** (수정 ❌)
   - 먼저 코드를 읽고 문제점을 파악합니다
   - **수정하지 않고** 사용자에게 문제점을 보고합니다
   - 확인받기 전까지는 수정하지 않습니다

2. **사용자 확인 받기** (허가 ✅)
   - "이 문제를 수정핼까요?" 라고 명시적으로 물어봅니다
   - 사용자가 "수정해" 또는 "OK" 등 명확한 허가를 받습니다

3. **수정 적용** (수정 ✅)
   - 허가받은 수정만 적용합니다
   - 한 번에 여러 곳을 수정해야 한다면, 각 수정마다 허가를 받습니다

### ❌ 금지 사항

- "이건 간단하니까 바로 수정할게요" → **금지**
- "문제점 알려드리고 제가 수정했어요" → **금지**
- "여러 개 수정해야 하는데 한 번에 할게요" → **금지**
- 사용자 확인 없이 코드 수정 → **금지**

### ✅ 올바른 예시

```
[문제점 파악 단계]
사용자: 파일 업로드가 안 돼
에이전트: 확인핼게요. (코드 확인 중...)
→ POST /api/notices/1/attachments 요청 시 302 리다이렉트 발생
→ 원인: Spring Security CSRF 설정으로 인해 로그인 페이지로 리다이렉트됨
→ 해결: CSRF 설정에서 해당 경로를 ignoringRequestMatchers에 추가 필요
수정할까요?

[사용자 확인 단계]
사용자: 수정해

[수정 단계]
에이전트: 수정했습니다. (정확한 수정 내용 설명)
```

---

> **Note**: 이 문서는 Kimi Code CLI가 프로젝트를 이해하는 데 사용됩니다.  
> 변경사항이 있으면 이 파일을 업데이트하세요.
