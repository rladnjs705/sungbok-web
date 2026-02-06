# Sungbok Church Website Project

## Project Overview

성복교회 공식 홈페이지 프로젝트입니다.

### Project Level
**Dynamic Level** - Fullstack development with separate frontend and backend

### Tech Stack

**Frontend:**
- Next.js 16.1 (App Router)
- TypeScript
- Tailwind CSS
- shadcn/ui
- SEO 최적화
- 반응형 디자인

**Backend:**
- Spring Boot 4.0.2
- PostgreSQL 18.1
- Docker
- REST API

**Deployment:**
- GitHub (Version Control)
- Jenkins (CI/CD)
- OCI (Oracle Cloud Infrastructure)

### Core Features

1. **예배 안내 및 설교 영상**
   - 주일예배/수요예배 시간 안내
   - 설교 영상 스트리밍 및 다운로드
   - 과거 설교 아카이브

2. **교회 소식 및 공지사항**
   - 새소식 게시판
   - 행사 일정
   - 주보 게시판

### Development Approach

- PDCA (Plan-Do-Check-Act) 사이클 기반 개발
- API-First 설계
- Component-Driven Development
- Test-Driven Development (TDD)

### Project Structure

```
sungbok-web/
├── frontend/              # Next.js application
│   ├── src/
│   │   ├── app/          # App Router pages
│   │   ├── components/   # React components
│   │   ├── lib/          # Utilities
│   │   └── types/        # TypeScript types
│   └── public/           # Static assets
│
├── backend/              # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── docker/           # Docker configurations
│
└── docs/                 # PDCA documents
    ├── 01-plan/
    ├── 02-design/
    ├── 03-analysis/
    └── 04-report/
```

### Developer Information

**Backend Developer**: Spring Boot 경험자
**Frontend Framework**: Next.js (학습 필요)

### Development Guidelines

1. **API 우선 설계**: Backend API를 먼저 설계하고 문서화
2. **타입 안정성**: TypeScript 활용한 타입 안정성 확보
3. **SEO 최적화**: Next.js App Router의 metadata API 활용
4. **반응형 디자인**: Mobile-first approach
5. **접근성**: WCAG 2.1 AA 레벨 준수

### Communication

- 한국어로 소통
- 기술 용어는 한/영 병기
- Spring Boot 개념은 간단히 설명, Next.js 개념은 상세히 설명

---

Generated with bkit v1.5.0 - Dynamic Level
