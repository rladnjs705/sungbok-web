# Zero Script QA 가이드

## 📋 개요

Zero Script QA는 테스트 스크립트를 작성하지 않고 Podman 로그를 실시간 분석하여 API 품질을 검증하는 방법론입니다.

## 🎯 Zero Script QA의 장점

### 기존 테스트 방식의 문제점
- ❌ 테스트 코드 작성에 많은 시간 소요
- ❌ 테스트 코드 유지보수 부담
- ❌ 실제 운영 환경과 다른 테스트 환경
- ❌ 통합 테스트의 복잡도

### Zero Script QA의 해결책
- ✅ 테스트 스크립트 작성 불필요
- ✅ 실제 Podman 환경에서 검증
- ✅ 로그 기반 자동 분석
- ✅ 빠른 피드백 사이클

## 🚀 실행 방법

### 1. 사전 요구사항

```bash
# Podman 및 Podman Compose 설치 확인
podman --version
podman-compose --version
```

### 2. Zero Script QA 실행

```bash
# Zero Script QA 스크립트 실행
./zero-script-qa.sh
```

### 3. 실행 과정

```
[1/7] Podman 환경 확인 중...
[2/7] 기존 컨테이너 정리 중...
[3/7] Podman 컨테이너 시작 중...
[4/7] 서비스 Health Check 대기 중...
[5/7] 주요 API 엔드포인트 테스트 중...
[6/7] Podman 로그 분석 중...
[7/7] 최종 리포트 생성 중...
```

## 📊 검증 항목

### 1. 환경 검증
- ✓ Podman 설치 확인
- ✓ Podman Compose 설치 확인
- ✓ 포트 충돌 확인 (5432, 6379, 8080)

### 2. 컨테이너 Health Check
- ✓ PostgreSQL 18.1 정상 시작
- ✓ Valkey (Redis-compatible) 정상 시작
- ✓ Spring Boot Application 정상 시작

### 3. API 엔드포인트 테스트
- ✓ `/actuator/health` - 애플리케이션 헬스 체크
- ✓ `/v3/api-docs` - OpenAPI JSON 스펙
- ✓ `/swagger-ui.html` - Swagger UI 접근

### 4. 로그 분석
- ERROR 로그 개수
- WARN 로그 개수
- EXCEPTION 발생 개수
- 애플리케이션 시작 확인

### 5. 품질 점수 계산

```
기본 점수: 100점
- API 실패 1개당: -10점
- ERROR 로그 1개당: -2점
- EXCEPTION 1개당: -5점

평가 기준:
- 90점 이상: ✅ 우수 (프로덕션 배포 가능)
- 70~89점: ⚠️ 양호 (경미한 이슈 존재)
- 70점 미만: ❌ 불합격 (주요 이슈 해결 필요)
```

## 📁 생성되는 파일

### 1. QA 리포트
```
./qa-logs/qa-report-YYYYMMDD_HHMMSS.md
```

QA 리포트에는 다음 정보가 포함됩니다:
- 환경 설정 검증 결과
- Podman 컨테이너 시작 로그
- Health Check 결과
- API 엔드포인트 테스트 결과
- Podman 로그 분석 결과
- 최종 품질 점수 및 평가

### 2. Podman 로그
```
./qa-logs/podman-YYYYMMDD_HHMMSS.log
```

전체 Podman 로그가 저장되어 상세 분석에 활용됩니다.

## 🔧 Podman Compose 구성

### 서비스 구성

#### 1. PostgreSQL 18.1
```yaml
- 포트: 5432
- 데이터베이스: sungbok_church
- 계정: postgres/postgres
- Health Check: pg_isready
```

#### 2. Valkey (Redis-compatible)
```yaml
- 포트: 6379
- 이미지: bitnami/valkey:latest
- Health Check: valkey-cli ping
```

#### 3. Spring Boot Backend
```yaml
- 포트: 8080
- JDK: 25
- Profile: prod
- Health Check: /actuator/health
```

## 📝 수동 테스트 방법

### 1. Podman Compose로 환경 시작

```bash
cd podman
podman-compose up -d --build
```

### 2. 로그 모니터링

```bash
# 전체 로그 확인
podman-compose logs -f

# 백엔드만 확인
podman logs -f sungbok-backend

# 에러 로그만 필터링
podman logs sungbok-backend 2>&1 | grep -i error
```

### 3. API 테스트

```bash
# Health Check
curl http://localhost:8080/actuator/health

# OpenAPI 문서
curl http://localhost:8080/v3/api-docs | jq .

# Swagger UI 접속
open http://localhost:8080/swagger-ui.html
```

### 4. 컨테이너 상태 확인

```bash
# 실행 중인 컨테이너
podman-compose ps

# 리소스 사용량
podman stats
```

### 5. 환경 정리

```bash
# 컨테이너 중지
podman-compose down

# 볼륨까지 삭제
podman-compose down -v
```

## 🐛 문제 해결

### 포트 충돌

```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080
lsof -i :5432
lsof -i :6379

# 프로세스 종료
kill -9 <PID>
```

### 컨테이너 시작 실패

```bash
# 상세 로그 확인
podman-compose logs backend

# 컨테이너 재빌드
podman-compose up -d --build --force-recreate
```

### Health Check 실패

```bash
# 백엔드 로그 실시간 확인
podman logs -f sungbok-backend

# PostgreSQL 접속 확인
podman exec -it sungbok-postgres psql -U postgres -d sungbok_church

# Valkey 접속 확인
podman exec -it sungbok-valkey valkey-cli ping
```

## 📈 CI/CD 통합

### GitHub Actions 예시

```yaml
name: Zero Script QA

on:
  pull_request:
    branches: [ main ]

jobs:
  qa:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Run Zero Script QA
        run: |
          cd backend
          chmod +x zero-script-qa.sh
          ./zero-script-qa.sh

      - name: Upload QA Report
        uses: actions/upload-artifact@v3
        with:
          name: qa-report
          path: backend/qa-logs/
```

## 🎓 Best Practices

### 1. 로그 레벨 설정
- **개발**: DEBUG (상세한 로그)
- **운영**: INFO (필수 정보만)
- **QA**: DEBUG (문제 진단용)

### 2. Structured Logging
```java
@Slf4j
public class NoticeService {
    public Notice getNotice(Long id) {
        log.debug("Fetching notice: id={}", id);
        // ...
        log.info("Notice retrieved: id={}, title={}", id, notice.getTitle());
    }
}
```

### 3. Health Check 엔드포인트
- `/actuator/health`: 기본 헬스 체크
- `/actuator/info`: 애플리케이션 정보
- `/actuator/metrics`: 메트릭 정보

### 4. 에러 처리
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
    }
}
```

## 🔒 보안 고려사항

### 환경 변수 관리
```bash
# .env 파일 생성 (Git 제외)
DB_PASSWORD=secure-password
JWT_SECRET=secure-jwt-secret-minimum-32-characters
YOUTUBE_API_KEY=your-api-key
```

### 프로덕션 배포
```yaml
# podman-compose.prod.yml
environment:
  DB_PASSWORD: ${DB_PASSWORD}
  JWT_SECRET: ${JWT_SECRET}
  YOUTUBE_API_KEY: ${YOUTUBE_API_KEY}
```

## 📞 지원

문제가 발생하면 다음을 확인하세요:
1. Podman 로그: `./qa-logs/podman-*.log`
2. QA 리포트: `./qa-logs/qa-report-*.md`
3. GitHub Issues: 이슈 등록

---

**Zero Script QA v1.0** | Phase 4 Complete ✅
