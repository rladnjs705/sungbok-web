# 성복교회 홈페이지

성복교회 공식 웹사이트 프로젝트입니다.

## 프로젝트 개요

### 주요 기능
- 예배 안내 및 설교 영상
- 교회 소식 및 공지사항

### 기술 스택

**Frontend**
- Next.js 16.1 (App Router)
- TypeScript
- Tailwind CSS
- shadcn/ui
- SEO 최적화
- 반응형 디자인

**Backend**
- Spring Boot 4.0.2
- PostgreSQL 18.1
- Podman
- REST API

**Deployment**
- GitHub (Version Control)
- Jenkins (CI/CD)
- OCI (Oracle Cloud Infrastructure)

## 프로젝트 구조

```
sungbok-web/
├── frontend/              # Next.js 프론트엔드
│   ├── src/
│   │   ├── app/          # App Router 페이지
│   │   ├── components/   # React 컴포넌트
│   │   ├── lib/          # 유틸리티 (API 클라이언트 등)
│   │   └── types/        # TypeScript 타입 정의
│   └── public/           # 정적 파일
│
├── backend/              # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/java/
│   │   └── test/java/
│   └── docker/           # Podman 설정
│
└── docs/                 # PDCA 문서
    ├── 01-plan/
    ├── 02-design/
    ├── 03-analysis/
    └── 04-report/
```

## 시작하기

### 사전 요구사항

- Node.js 20+
- Java 21
- Maven 3.9+
- Podman & Podman Compose
- PostgreSQL 18.1

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

Frontend는 http://localhost:3000에서 실행됩니다.

### Backend 실행

**Option 1: Podman Compose 사용 (권장)**

```bash
cd backend/docker
podman-compose up
```

**Option 2: 로컬 실행**

PostgreSQL이 로컬에서 실행 중이어야 합니다.

```bash
cd backend
mvn spring-boot:run
```

Backend API는 http://localhost:8080/api에서 실행됩니다.

### 환경 변수 설정

**Frontend** (`frontend/.env.local`)
```bash
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_SITE_NAME=성복교회
NEXT_PUBLIC_SITE_URL=https://sungbok-church.com
```

**Backend** (`backend/src/main/resources/application.yml`)
- Podman Compose 사용 시 자동 설정
- 로컬 실행 시 PostgreSQL 접속 정보 확인

## 개발 워크플로우

### PDCA 사이클 기반 개발

1. **Plan** (계획): 기능 요구사항 정의 및 계획 수립
2. **Do** (실행): API 설계 → Backend 구현 → Frontend 구현
3. **Check** (검증): Gap 분석 및 품질 검증
4. **Act** (개선): 개선사항 반영 및 문서화

### 개발 순서

1. **Schema 정의** (Phase 1): 도메인 모델 및 용어 정의
2. **Convention** (Phase 2): 코딩 규칙 및 네이밍 컨벤션
3. **Mockup** (Phase 3): UI/UX 프로토타입
4. **API Design** (Phase 4): REST API 설계 및 구현
5. **Design System** (Phase 5): 재사용 가능한 컴포넌트
6. **UI Integration** (Phase 6): Frontend-Backend 연동
7. **SEO & Security** (Phase 7): 검색 최적화 및 보안
8. **Review** (Phase 8): 코드 리뷰 및 품질 검증
9. **Deployment** (Phase 9): 프로덕션 배포

## API 문서

Backend API 문서는 `docs/02-design/api-spec.md`에서 확인할 수 있습니다.

Swagger UI: http://localhost:8080/swagger-ui.html (예정)

## 배포

### CI/CD 파이프라인

GitHub → Jenkins → OCI

자세한 배포 가이드는 `docs/02-design/deployment.md`를 참조하세요. (예정)

## 기여 가이드

1. 기능 브랜치 생성: `git checkout -b feature/기능명`
2. 변경사항 커밋: `git commit -m "feat: 기능 추가"`
3. 브랜치 푸시: `git push origin feature/기능명`
4. Pull Request 생성

### Commit 컨벤션

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅 (기능 변경 없음)
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 설정 등 기타 변경
```

## 라이선스

Copyright © 2026 성복교회. All rights reserved.

---

**Generated with bkit v1.5.0 - Dynamic Level**
