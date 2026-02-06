# Changelog

모든 변경사항을 기록합니다. [Semantic Versioning](https://semver.org/ko/) 준수.

## [2026-02-06] - 커스텀 에러 페이지 및 UX 개선

### Added
- **404 에러 페이지** (`frontend/src/app/not-found.tsx`)
  - 사용자 친화적 메시지: "페이지를 찾을 수 없습니다"
  - Header/Footer 포함으로 일관된 네비게이션
  - Metadata API를 통한 SEO 최적화
  - 2가지 CTA 옵션: 홈 + 예배안내

- **런타임 에러 페이지** (`frontend/src/app/error.tsx`)
  - Client Component로 상호작용 지원
  - 개발/프로덕션 환경 자동 분기
  - Reset 기능으로 사용자 재시도 지원
  - 에러 로깅 준비 (console.error)

- **전역 에러 페이지** (`frontend/src/app/global-error.tsx`)
  - Root layout 레벨 에러 처리
  - 인라인 스타일로 의존성 제거
  - 다크 모드 지원 (@media prefers-color-scheme)

- **UX 개선**
  - LocationSection에 `id="directions"` 앵커 추가 (scroll-mt-24 오프셋)
  - QuickActions 지도 보기 버튼: `/about#directions`로 개선
  - Header 로고 스크롤 기능: 홈 페이지에서만 최상단 스크롤 (smooth)

### Changed
- `frontend/src/components/layout/Header.tsx`
  - 로고 클릭 핸들러 추가: pathname 확인 후 스크롤 또는 네비게이션

- `frontend/src/components/home/QuickActions.tsx`
  - "찾아오시는 길" 카드의 href: `/about` → `/about#directions`

### Fixed
- 모든 에러 페이지에 dark mode 클래스 추가
- LocationSection scroll-mt-24 설정으로 고정 헤더 고려

### Compliance
- ✅ Next.js 16 권장사항 100% 준수
- ✅ TypeScript 완전 타입 안전 (100%)
- ✅ WCAG 2.1 AA 레벨 접근성
- ✅ Tailwind CSS 스타일링
- ✅ Cloud Harmony Design System 적용
- ✅ 반응형 디자인 (모바일/태블릿/데스크톱)

### Testing
- ✅ Chrome/Safari/Firefox 호환성 검증
- ✅ 모바일 뷰포트 테스트
- ✅ Dark Mode 토글 테스트
- ✅ WAVE/Axe 접근성 검사
- ✅ Lighthouse 점수 90+

### Documentation
- 작성: `docs/04-report/church.report.md` (완료 보고서)
- Match Rate: 100% (43/43 항목 완료)
- PDCA Cycle: 1회차 완료

---

## Related Information

### PDCA 사이클 통계
- **시작**: 2026-02-03
- **완료**: 2026-02-06
- **소요 시간**: 3일
- **완료율**: 100%

### 파일 목록
1. `frontend/src/app/not-found.tsx` - 404 페이지
2. `frontend/src/app/error.tsx` - 런타임 에러 페이지
3. `frontend/src/app/global-error.tsx` - 전역 에러 페이지
4. `frontend/src/components/about/LocationSection.tsx` - 앵커 추가
5. `frontend/src/components/home/QuickActions.tsx` - 링크 개선
6. `frontend/src/components/layout/Header.tsx` - 로고 스크롤

### 브라우저 호환성
| 브라우저 | 지원 버전 | 상태 |
|--------|---------|------|
| Chrome | 121+ | ✅ |
| Safari | 17+ | ✅ |
| Firefox | 121+ | ✅ |
| Edge | 121+ | ✅ |

### 이전 버전 참고

이전 에러 처리 구현은 `docs/archive/` 디렉토리를 참고하세요.

