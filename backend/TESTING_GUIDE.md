# Testing Guide

## 환경 설정

### 1. 환경 변수 설정

```bash
# .env 파일 생성
cp .env.example .env

# 환경 변수 입력
cat > .env << 'EOF'
DB_USERNAME=postgres
DB_PASSWORD=your_password
YOUTUBE_API_KEY=your_api_key
YOUTUBE_CHANNEL_ID=your_channel_id
EOF
```

### 2. 데이터베이스 준비

```bash
# PostgreSQL 실행 (Docker)
docker run -d \
  --name sungbok-postgres \
  -e POSTGRES_DB=sungbok_church \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  postgres:17

# 또는 로컬 PostgreSQL 사용
createdb sungbok_church
```

### 3. Valkey (Redis-compatible) 실행

```bash
# Valkey 9.0.1 실행
docker run -d \
  --name sungbok-valkey \
  -p 6379:6379 \
  valkey/valkey:9.0.1
```

---

## 빌드 & 실행

### 빌드

```bash
# 빌드 (환경 변수 불필요)
./gradlew build

# 테스트 제외 빌드
./gradlew build -x test
```

### 실행

```bash
# 환경 변수 설정 후 실행
export $(cat .env | xargs)
./gradlew bootRun

# 또는 직접 환경 변수 지정
DB_USERNAME=postgres \
DB_PASSWORD=your_password \
YOUTUBE_API_KEY=your_key \
YOUTUBE_CHANNEL_ID=your_id \
./gradlew bootRun
```

---

## API 테스트

### Health Check

```bash
# 서버 실행 확인
curl http://localhost:8080/actuator/health
```

### 공개 API (GET) 테스트

```bash
# 공지사항 목록
curl http://localhost:8080/api/notices

# 설교 목록
curl http://localhost:8080/api/sermons

# 예배 시간
curl http://localhost:8080/api/worships

# YouTube 라이브 상태
curl http://localhost:8080/api/youtube/lives
```

### API 경로 확인

**수정 전 (문제)**: `/api/api/notices`
**수정 후 (정상)**: `/api/notices`

---

## 주요 엔드포인트

### 공지사항
- `GET /api/notices` - 목록 조회
- `GET /api/notices/{id}` - 상세 조회
- `POST /api/notices` - 생성 (🚨 보안 필요)
- `PUT /api/notices/{id}` - 수정 (🚨 보안 필요)
- `DELETE /api/notices/{id}` - 삭제 (🚨 보안 필요)

### 설교
- `GET /api/sermons` - 목록 조회
- `GET /api/sermons/{id}` - 상세 조회
- `GET /api/sermons/search?keyword=사랑` - 검색

### 예배
- `GET /api/worships` - 활성화된 예배 목록
- `GET /api/worships/live-now` - 현재 라이브 중인 예배

### YouTube 통합
- `GET /api/youtube/lives` - 라이브 방송 목록
- `GET /api/youtube/playlists` - 재생목록 목록
- `GET /api/youtube/quota/stats` - Quota 사용 통계 (🚨 보안 필요)

### 기도요청
- `GET /api/prayer-requests` - 승인된 기도요청
- `GET /api/prayer-requests/pending` - 대기 중 (🚨 보안 필요)
- `POST /api/prayer-requests/{id}/approve` - 승인 (🚨 보안 필요)

---

## 보안 테스트 시나리오 ✅ (Spring Security 구현 완료)

### 1. 회원가입

```bash
# 새 사용자 등록
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "name": "테스트 사용자",
    "email": "test@example.com",
    "phone": "010-1234-5678"
  }'
# → 201 Created + JWT 토큰 자동 반환
```

### 2. 로그인

```bash
# JWT 토큰 획득
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"
```

### 3. 익명 사용자 접근 테스트

```bash
# 공개 API는 접근 가능 (GET)
curl http://localhost:8080/api/notices
# → 200 OK

curl http://localhost:8080/api/sermons
# → 200 OK

# 관리자 API는 거부 (POST, PUT, DELETE)
curl -X POST http://localhost:8080/api/notices \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","content":"Test"}'
# → 401 Unauthorized
```

### 4. 인증된 관리자 접근 테스트

```bash
# JWT 토큰으로 관리자 API 접근
curl -X POST http://localhost:8080/api/notices \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 공지사항",
    "content": "보안 테스트",
    "category": "GENERAL"
  }'
# → 201 Created

# 승인 대기 콘텐츠 조회 (관리자 전용)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/prayer-requests/pending
# → 200 OK

# YouTube Quota 통계 (관리자 전용)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/youtube/quota/stats
# → 200 OK
```

---

## 문제 해결

### 빌드 실패

```bash
# 캐시 삭제 후 재빌드
./gradlew clean build --refresh-dependencies
```

### 환경 변수 누락

```
Error: The datasource property 'username' must be set
```

**해결**: `.env` 파일 확인 또는 환경 변수 직접 설정

### 데이터베이스 연결 실패

```
Connection refused: localhost:5432
```

**해결**:
1. PostgreSQL 서버 실행 확인
2. 포트 확인
3. 방화벽 설정 확인

### Valkey 연결 실패

```
Unable to connect to Redis at localhost:6379
```

**해결**:
1. Valkey 컨테이너 실행 확인: `docker ps | grep valkey`
2. 재시작: `docker restart sungbok-valkey`
3. Valkey 없이 실행: 기본 프로파일 사용 (캐시 비활성화)

---

## 성능 테스트

### 부하 테스트 (Apache Bench)

```bash
# 100개 요청, 10개 동시 연결
ab -n 100 -c 10 http://localhost:8080/api/notices

# 결과 확인
# - Requests per second
# - Time per request
# - Failed requests
```

### 조회수 동시성 테스트

```bash
# 동일한 공지사항을 동시에 조회
for i in {1..10}; do
  curl http://localhost:8080/api/notices/1 &
done
wait

# viewCount가 정확히 10 증가했는지 확인
# (현재: Race condition으로 인해 부정확할 수 있음)
```

---

## 코드 리뷰 후 변경 사항 확인

### ✅ 수정 완료

1. **하드코딩된 시크릿 제거**
   ```bash
   # 확인: application.yml에 기본값 없음
   grep "AIzaSy" src/main/resources/application.yml
   # → 결과 없음 (정상)
   ```

2. **API 경로 중복 해결**
   ```bash
   # 확인: context-path 제거됨
   grep "context-path" src/main/resources/application.yml
   # → 결과 없음 (정상)

   # API 경로 테스트
   curl http://localhost:8080/api/notices
   # → 200 OK (정상)

   curl http://localhost:8080/api/api/notices
   # → 404 Not Found (정상)
   ```

### ✅ 완료된 작업 (자동 수정)

1. **Spring Security 구현** ✅
   - 상태: 구현 완료
   - JWT 기반 인증/인가
   - 역할 기반 접근 제어 (USER, ADMIN)
   - `/api/auth/login`, `/api/auth/register` 엔드포인트 추가

2. **CORS 설정** ✅
   - 상태: 구현 완료
   - WebConfig에 CORS 정책 설정
   - 환경 변수로 허용 도메인 관리 가능

3. **조회수 Race Condition** ✅
   - 상태: 수정 완료
   - EntityManager.refresh() 사용
   - 영향 받는 서비스: Notice, Sermon, Gallery, Testimony, VideoGallery, Event

4. **삭제 전 존재 확인** ✅
   - 상태: 수정 완료
   - existsById() 체크 추가
   - 영향 받는 서비스: Notice, Sermon, Gallery, Testimony, VideoGallery, Event

5. **계좌번호 마스킹** ✅
   - 상태: 구현 완료
   - DonationAccountResponse.getMaskedAccountNumber() 메서드 추가
   - 마지막 4자리 제외하고 마스킹

### ⚠️ 남은 작업

1. **엔티티 캡슐화**
   - 상태: 미수정
   - @Setter 제거 및 비즈니스 메서드 추가 필요
   - 우선순위: ⚠️ 중간

2. **N+1 쿼리 최적화**
   - 상태: 미수정
   - @EntityGraph 또는 JOIN FETCH 사용 필요
   - 우선순위: ⚠️ 중간

---

## 배포 준비

### 프로덕션 설정

```yaml
# application-prod.yml 생성
spring:
  jpa:
    show-sql: false  # 프로덕션에서는 비활성화
    hibernate:
      ddl-auto: validate  # 절대 update/create 사용 금지

logging:
  level:
    com.sungbok.church: INFO  # DEBUG → INFO
    org.hibernate.SQL: OFF
```

### 환경 변수 (프로덕션)

```bash
# Kubernetes Secret 또는 AWS Secrets Manager 사용
DB_USERNAME=prod_user
DB_PASSWORD=<strong_password>
YOUTUBE_API_KEY=<production_key>
YOUTUBE_CHANNEL_ID=<production_channel>
```

---

## 다음 단계

1. ✅ 빌드 확인 완료
2. ⏳ Spring Security 구현
3. ⏳ 실제 데이터로 API 테스트
4. ⏳ 프론트엔드 연동 테스트
5. ⏳ 부하 테스트
6. ⏳ 프로덕션 배포

---

## 참고 문서

- [GRADLE_MIGRATION.md](./GRADLE_MIGRATION.md) - Gradle 전환 가이드
- [CODE_REVIEW_REPORT.md](./CODE_REVIEW_REPORT.md) - 코드 리뷰 전체 보고서
- [application-valkey.yml](./src/main/resources/application-valkey.yml) - Valkey 설정
