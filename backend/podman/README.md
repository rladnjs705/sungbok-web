# Podman 컨테이너 설정

## 📦 개요

이 디렉토리는 Podman을 사용한 컨테이너화 설정을 포함합니다.

### Podman vs Docker

**Podman을 선택한 이유:**
- ✅ **데몬리스(Daemonless)**: 백그라운드 데몬 없이 실행
- ✅ **루트리스(Rootless)**: 루트 권한 없이 컨테이너 실행 가능
- ✅ **보안 강화**: 더 안전한 아키텍처
- ✅ **Docker 호환**: Docker CLI와 거의 동일한 명령어
- ✅ **OCI 표준**: Open Container Initiative 표준 준수

## 📁 파일 구조

```
podman/
├── Containerfile       # Podman 표준 컨테이너 파일
├── Dockerfile          # Docker 호환성을 위한 파일
├── podman-compose.yml  # Podman Compose 설정
└── README.md          # 이 파일
```

## 🚀 사용 방법

### 1. Podman 설치

#### macOS
```bash
brew install podman
brew install podman-compose
```

#### Linux (Ubuntu/Debian)
```bash
# Podman 설치
sudo apt-get update
sudo apt-get -y install podman

# Podman Compose 설치
pip3 install podman-compose
```

#### Linux (RHEL/CentOS/Fedora)
```bash
# Podman 설치
sudo dnf install podman

# Podman Compose 설치
pip3 install podman-compose
```

### 2. Podman 머신 초기화 (macOS/Windows)

```bash
# 머신 생성 및 시작
podman machine init
podman machine start

# 머신 상태 확인
podman machine list
```

### 3. 컨테이너 실행

```bash
# 이미지 빌드 및 컨테이너 시작
podman-compose up -d --build

# 로그 확인
podman-compose logs -f

# 컨테이너 상태 확인
podman-compose ps
```

### 4. 컨테이너 중지

```bash
# 컨테이너 중지
podman-compose down

# 볼륨까지 삭제
podman-compose down -v
```

## 🔧 서비스 구성

### PostgreSQL 18.1
```yaml
- 포트: 5432
- 데이터베이스: sungbok_church
- 사용자: postgres
- 볼륨: postgres_data
```

### Valkey (Redis 호환)
```yaml
- 포트: 6379
- 이미지: bitnami/valkey:latest
- 프로토콜: Redis-compatible
```

### Spring Boot Backend
```yaml
- 포트: 8080
- JDK: 25
- 빌드: Gradle
- Health Check: /actuator/health
```

## 📊 Health Check

모든 서비스는 Health Check를 통해 상태를 모니터링합니다:

```bash
# PostgreSQL
pg_isready -U postgres

# Valkey
valkey-cli ping

# Backend
wget --quiet --tries=1 --spider http://localhost:8080/actuator/health
```

## 🔍 Podman 명령어

### 기본 명령어

```bash
# 컨테이너 목록
podman ps

# 이미지 목록
podman images

# 로그 확인
podman logs sungbok-backend

# 컨테이너 접속
podman exec -it sungbok-backend sh

# 리소스 사용량
podman stats
```

### Compose 명령어

```bash
# 서비스 시작
podman-compose up -d

# 서비스 중지
podman-compose down

# 로그 확인
podman-compose logs -f backend

# 서비스 재시작
podman-compose restart backend
```

## 🐛 문제 해결

### 1. 포트 충돌

```bash
# 포트 사용 확인
lsof -i :5432
lsof -i :6379
lsof -i :8080

# Podman 포트 매핑 확인
podman port sungbok-backend
```

### 2. 컨테이너 시작 실패

```bash
# 로그 확인
podman logs sungbok-backend

# 컨테이너 상태 확인
podman inspect sungbok-backend

# 강제 재빌드
podman-compose up -d --build --force-recreate
```

### 3. 볼륨 문제

```bash
# 볼륨 목록
podman volume ls

# 볼륨 삭제
podman volume rm postgres_data

# 볼륨 검사
podman volume inspect postgres_data
```

### 4. 네트워크 문제

```bash
# 네트워크 목록
podman network ls

# 네트워크 검사
podman network inspect sungbok-network

# 네트워크 재생성
podman network rm sungbok-network
podman network create sungbok-network
```

## 🔄 Docker에서 Podman으로 마이그레이션

### 명령어 대응표

| Docker | Podman |
|--------|--------|
| `docker ps` | `podman ps` |
| `docker images` | `podman images` |
| `docker run` | `podman run` |
| `docker build` | `podman build` |
| `docker-compose up` | `podman-compose up` |
| `docker-compose down` | `podman-compose down` |
| `docker logs` | `podman logs` |
| `docker exec` | `podman exec` |

### Alias 설정 (선택사항)

Docker 명령어를 Podman으로 자동 전환:

```bash
# ~/.bashrc 또는 ~/.zshrc에 추가
alias docker='podman'
alias docker-compose='podman-compose'
```

## 🔐 보안

### Rootless 모드

Podman은 기본적으로 rootless 모드로 실행됩니다:

```bash
# 현재 사용자로 실행
podman run -d --name test nginx

# 루트 권한 없이 확인
podman ps
```

### SELinux/AppArmor

보안 강화를 위한 설정:

```bash
# SELinux 레이블 확인 (Linux)
podman inspect sungbok-backend | grep -i selinux

# AppArmor 프로파일 확인 (Ubuntu)
podman inspect sungbok-backend | grep -i apparmor
```

## 📈 성능 최적화

### 이미지 최적화

```dockerfile
# 멀티스테이지 빌드 사용 (현재 적용됨)
FROM eclipse-temurin:25-jdk-alpine AS builder
# ... 빌드 단계 ...
FROM eclipse-temurin:25-jre-alpine
# ... 실행 단계 ...
```

### 볼륨 성능

```yaml
# 볼륨 옵션 최적화 (필요시)
volumes:
  - postgres_data:/var/lib/postgresql/data:Z
```

## 🎯 프로덕션 배포

### 환경 변수 관리

```bash
# .env 파일 생성 (Git 제외)
cat > .env << EOF
DB_PASSWORD=secure-production-password
JWT_SECRET=secure-jwt-secret-minimum-32-characters-required
YOUTUBE_API_KEY=actual-youtube-api-key
YOUTUBE_CHANNEL_ID=actual-channel-id
EOF

# Compose에서 사용
podman-compose --env-file .env up -d
```

### 로그 관리

```bash
# 로그 로테이션 설정
podman run -d \
  --log-driver=journald \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  my-app
```

## 📞 지원

문제가 발생하면:
1. `podman logs sungbok-backend` 확인
2. `podman-compose logs` 전체 로그 확인
3. GitHub Issues에 문의

## 📚 참고 자료

- [Podman 공식 문서](https://docs.podman.io/)
- [Podman Compose](https://github.com/containers/podman-compose)
- [OCI 스펙](https://opencontainers.org/)
- [컨테이너 보안 가이드](https://docs.podman.io/en/latest/markdown/podman-security.1.html)

---

**Podman 기반 컨테이너화** | bkit v1.5.0
