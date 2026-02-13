# 성북교회 API 문서

## 🎯 Overview

성북교회 백엔드 REST API는 SpringDoc OpenAPI 3.0.1을 사용하여 자동으로 문서화됩니다.

## 📚 API 문서 접근

### Swagger UI (대화형 문서)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON Specification
```
http://localhost:8080/v3/api-docs
```

### OpenAPI YAML Specification
```
http://localhost:8080/v3/api-docs.yaml
```

## 🔐 인증 (Authentication)

API는 JWT Bearer Token 인증을 사용합니다.

### 인증 방법
1. Swagger UI 우측 상단 "Authorize" 버튼 클릭
2. Bearer Token 입력: `Bearer <your_jwt_token>`
3. "Authorize" 클릭하여 적용

## 📋 API 카테고리

### 1. 공지사항 (Notice)
- `GET /api/notices` - 공지사항 목록 조회
- `GET /api/notices/{id}` - 공지사항 상세 조회
- `GET /api/notices/category/{category}` - 카테고리별 조회
- `GET /api/notices/pinned` - 상단 고정 공지사항
- `GET /api/notices/search` - 통합 검색
- `POST /api/notices` - 공지사항 생성 🔒
- `PUT /api/notices/{id}` - 공지사항 수정 🔒
- `DELETE /api/notices/{id}` - 공지사항 삭제 🔒

### 2. 설교 (Sermon)
- `GET /api/sermons` - 설교 목록 조회
- `GET /api/sermons/{id}` - 설교 상세 조회
- `POST /api/sermons` - 설교 등록 🔒
- `PUT /api/sermons/{id}` - 설교 수정 🔒
- `DELETE /api/sermons/{id}` - 설교 삭제 🔒

### 3. 예배 (Worship)
- `GET /api/worship` - 예배 일정 조회
- `GET /api/worship/{id}` - 예배 상세 조회
- `POST /api/worship` - 예배 등록 🔒
- `PUT /api/worship/{id}` - 예배 수정 🔒
- `DELETE /api/worship/{id}` - 예배 삭제 🔒

### 4. 행사 (Event)
- `GET /api/events` - 행사 목록 조회
- `GET /api/events/{id}` - 행사 상세 조회
- `GET /api/events/ongoing` - 진행 중인 행사
- `GET /api/events/upcoming` - 예정된 행사
- `POST /api/events` - 행사 생성 🔒
- `PUT /api/events/{id}` - 행사 수정 🔒
- `DELETE /api/events/{id}` - 행사 삭제 🔒

### 5. 갤러리 (Gallery)
- `GET /api/galleries` - 갤러리 목록 조회
- `GET /api/galleries/{id}` - 갤러리 상세 조회
- `POST /api/galleries` - 갤러리 생성 🔒
- `PUT /api/galleries/{id}` - 갤러리 수정 🔒
- `DELETE /api/galleries/{id}` - 갤러리 삭제 🔒

### 6. 간증 (Testimony)
- `GET /api/testimonies` - 간증 목록 조회
- `GET /api/testimonies/{id}` - 간증 상세 조회
- `POST /api/testimonies` - 간증 등록
- `PUT /api/testimonies/{id}` - 간증 수정 🔒
- `DELETE /api/testimonies/{id}` - 간증 삭제 🔒

### 7. 기도제목 (Prayer Request)
- `GET /api/prayer-requests` - 기도제목 목록
- `POST /api/prayer-requests` - 기도제목 등록
- `PUT /api/prayer-requests/{id}` - 기도제목 수정
- `DELETE /api/prayer-requests/{id}` - 기도제목 삭제

### 8. 유튜브 라이브 (YouTube Live)
- `GET /api/youtube/live` - 라이브 방송 목록
- `GET /api/youtube/live/current` - 현재 라이브 중인 방송
- `GET /api/youtube/live/upcoming` - 예정된 라이브
- `POST /api/youtube/live` - 라이브 생성 🔒
- `PUT /api/youtube/live/{id}` - 라이브 수정 🔒
- `DELETE /api/youtube/live/{id}` - 라이브 삭제 🔒

### 9. 유튜브 재생목록 (YouTube Playlist)
- `GET /api/youtube/playlists` - 재생목록 조회
- `POST /api/youtube/playlists` - 재생목록 생성 🔒
- `PUT /api/youtube/playlists/{id}` - 재생목록 수정 🔒
- `DELETE /api/youtube/playlists/{id}` - 재생목록 삭제 🔒

### 10. 사역 (Ministry)
- `GET /api/ministries` - 사역 목록
- `GET /api/ministries/{id}` - 사역 상세
- `POST /api/ministries` - 사역 생성 🔒
- `PUT /api/ministries/{id}` - 사역 수정 🔒
- `DELETE /api/ministries/{id}` - 사역 삭제 🔒

### 11. 헌금 계좌 (Donation Account)
- `GET /api/donation-accounts` - 헌금 계좌 목록
- `POST /api/donation-accounts` - 계좌 생성 🔒
- `PUT /api/donation-accounts/{id}` - 계좌 수정 🔒
- `DELETE /api/donation-accounts/{id}` - 계좌 삭제 🔒

### 12. 교직원 (Staff)
- `GET /api/staff` - 교직원 목록
- `POST /api/staff` - 교직원 등록 🔒
- `PUT /api/staff/{id}` - 교직원 수정 🔒
- `DELETE /api/staff/{id}` - 교직원 삭제 🔒

### 13. 목회자 (Pastor)
- `GET /api/pastors` - 목회자 목록
- `POST /api/pastors` - 목회자 등록 🔒
- `PUT /api/pastors/{id}` - 목회자 수정 🔒
- `DELETE /api/pastors/{id}` - 목회자 삭제 🔒

### 14. 주보 (Bulletin)
- `GET /api/bulletins` - 주보 목록
- `POST /api/bulletins` - 주보 생성 🔒
- `PUT /api/bulletins/{id}` - 주보 수정 🔒
- `DELETE /api/bulletins/{id}` - 주보 삭제 🔒

### 15. 선교 (Mission)
- `GET /api/missions` - 선교 활동 목록
- `POST /api/missions` - 선교 활동 등록 🔒
- `PUT /api/missions/{id}` - 선교 활동 수정 🔒
- `DELETE /api/missions/{id}` - 선교 활동 삭제 🔒

### 16. 페이지 (Custom Pages)
- `GET /api/pages` - 페이지 목록
- `POST /api/pages` - 페이지 생성 🔒
- `PUT /api/pages/{id}` - 페이지 수정 🔒
- `DELETE /api/pages/{id}` - 페이지 삭제 🔒

### 17. 찬송가 (Hymn)
- `GET /api/hymns` - 찬송가 검색
- `GET /api/hymns/{number}` - 찬송가 상세

### 18. 영상 갤러리 (Video Gallery)
- `GET /api/video-galleries` - 영상 갤러리 목록
- `POST /api/video-galleries` - 영상 생성 🔒
- `PUT /api/video-galleries/{id}` - 영상 수정 🔒
- `DELETE /api/video-galleries/{id}` - 영상 삭제 🔒

🔒 = 관리자 인증 필요

## 🎨 Swagger UI 기능

### 1. Try it out
각 API 엔드포인트에서 "Try it out" 버튼을 클릭하여 직접 API를 테스트할 수 있습니다.

### 2. Request/Response 샘플
- Request Body 예시
- Response 스키마
- HTTP Status Codes

### 3. Model 정의
하단 "Schemas" 섹션에서 모든 DTO 및 Entity 모델 구조를 확인할 수 있습니다.

## 📝 개발 가이드

### API 엔드포인트 추가 시
1. Controller에 `@Operation` 애노테이션 추가
2. `@ApiResponses`로 응답 코드 정의
3. `@Parameter`로 파라미터 설명 추가

### 예시
```java
@Operation(summary = "공지사항 조회", description = "공지사항 목록을 페이징하여 조회합니다.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
})
@GetMapping
public ResponseEntity<Page<NoticeResponse>> getNotices(
    @Parameter(description = "페이징 정보") Pageable pageable) {
    // ...
}
```

## 🚀 배포 환경

### 개발 서버
- URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 운영 서버 (예정)
- URL: `https://api.sungbok.church`
- Swagger UI: `https://api.sungbok.church/swagger-ui.html`

## 📊 API 통계

- **총 엔드포인트**: 150+ endpoints
- **컨트롤러**: 18개
- **인증 방식**: JWT Bearer Token
- **문서화 도구**: SpringDoc OpenAPI 3.0.1
- **API 버전**: v1.0.0

## 🔧 설정

### application.yml
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: alpha  # 작업을 알파벳순으로 정렬
    tags-sorter: alpha        # 태그를 알파벳순으로 정렬
```

### OpenApiConfig.java
- API 메타데이터 정의
- JWT 보안 스키마 설정
- 서버 URL 설정

## 📞 지원

문의사항이 있으시면 다음으로 연락주세요:
- Email: contact@sungbok.church
- Web: https://sungbok.church

---

Generated with SpringDoc OpenAPI 3.0.1 | Phase 4 Complete ✅
