# Backend Documentation

성복교회 백엔드 프로젝트 문서 모음

## 📁 폴더 구조

```
docs/
├── api/                    # API 문서
│   └── API_DOCUMENTATION.md
│
├── guides/                 # 개발 및 운영 가이드
│   ├── DEPLOYMENT_GUIDE.md
│   ├── SECURITY.md
│   ├── TESTING_GUIDE.md
│   ├── ZERO_SCRIPT_QA_GUIDE.md
│   └── GRADLE_MIGRATION.md
│
├── reports/                # 프로젝트 리포트
│   ├── PDCA_SUMMARY.md
│   ├── PHASE4_COMPLETION_REPORT.md
│   ├── CODE_REVIEW_REPORT.md
│   ├── SECURITY_IMPLEMENTATION_SUMMARY.md
│   └── PHASE4_DEPENDENCIES.md
│
├── 01-plan/               # PDCA Plan 단계
├── 02-design/             # PDCA Design 단계
├── 03-analysis/           # PDCA Check 단계
├── 03-implementation/     # 구현 가이드
├── 04-report/             # PDCA Report 단계
│
├── archive/               # 아카이브된 문서
│
├── IMPLEMENTATION_SUMMARY.md
└── QUERYDSL_TEST_FIX_SUMMARY.md
```

## 📖 주요 문서 안내

### API 개발
- [API 문서](./api/API_DOCUMENTATION.md) - REST API 엔드포인트 명세

### 개발 가이드
- [테스트 가이드](./guides/TESTING_GUIDE.md) - 로컬 테스트 환경 설정
- [Zero Script QA](./guides/ZERO_SCRIPT_QA_GUIDE.md) - Podman 기반 QA
- [Gradle 마이그레이션](./guides/GRADLE_MIGRATION.md) - 빌드 설정

### 배포 및 운영
- [배포 가이드](./guides/DEPLOYMENT_GUIDE.md) - 프로덕션 배포 절차
- [보안 가이드](./guides/SECURITY.md) - 보안 설정

### 프로젝트 리포트
- [PDCA 요약](./reports/PDCA_SUMMARY.md) - 개발 사이클 요약
- [Phase 4 완료 리포트](./reports/PHASE4_COMPLETION_REPORT.md)

## 🔗 관련 문서

- [프로젝트 루트 README](../../README.md)
- [Frontend 문서](../../frontend/README.md)
- [전체 가이드](../../docs/guides/)
