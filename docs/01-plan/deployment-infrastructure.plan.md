# 배포 인프라 계획 (Podman 기반)

## 📋 개요

**작성일**: 2026-02-04
**Phase**: 9 - Deployment
**컨테이너 기술**: Podman (데몬리스, 루트리스)

---

## 🎯 배포 전략

### 배포 환경
- **개발 환경**: Local (Podman)
- **스테이징 환경**: OCI VM (Podman)
- **운영 환경**: OCI VM (Podman + Jenkins CI/CD)

### 컨테이너 기술: Podman

#### Podman 선택 이유
- ✅ **보안**: Rootless 컨테이너 (루트 권한 불필요)
- ✅ **안정성**: Daemonless 아키텍처 (단일 장애점 제거)
- ✅ **효율성**: 리소스 사용량 87% 감소
- ✅ **표준**: OCI(Open Container Initiative) 완전 준수
- ✅ **호환성**: Docker CLI와 거의 동일

---

## 🏗️ 인프라 아키텍처

### 1. 컨테이너 구성

```yaml
services:
  # Database
  postgres:
    image: postgres:18.1
    ports: ["5432:5432"]
    volumes: [postgres_data:/var/lib/postgresql/data]
    healthcheck: pg_isready

  # Cache
  valkey:
    image: bitnami/valkey:latest
    ports: ["6379:6379"]
    healthcheck: valkey-cli ping

  # Application
  backend:
    build: podman/Containerfile
    ports: ["8080:8080"]
    depends_on: [postgres, valkey]
    healthcheck: /actuator/health
```

### 2. 네트워크 구성

```
Internet
    ↓
[Nginx Reverse Proxy]
    ↓
[Podman Network Bridge]
    ├── Backend Container (8080)
    ├── PostgreSQL (5432)
    └── Valkey (6379)
```

### 3. 볼륨 관리

| 볼륨 | 용도 | 백업 주기 |
|------|------|-----------|
| `postgres_data` | PostgreSQL 데이터 | 매일 자동 |
| `valkey_data` | Valkey 캐시 | 백업 불필요 |
| `logs` | 애플리케이션 로그 | 주간 로테이션 |

---

## 🚀 CI/CD 파이프라인 (Jenkins)

### 파이프라인 단계

```groovy
// Jenkinsfile
pipeline {
    agent any

    environment {
        REGISTRY = 'registry.sungbok.church'
        IMAGE_NAME = 'sungbok-backend'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/sungbok/sungbok-web.git'
            }
        }

        stage('Build') {
            steps {
                sh '''
                    cd backend
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    cd backend
                    ./gradlew test
                '''
            }
        }

        stage('Container Build') {
            steps {
                sh '''
                    cd backend/podman
                    podman build -t ${IMAGE_NAME}:${BUILD_NUMBER} -f Containerfile ..
                    podman tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Push to Registry') {
            steps {
                sh '''
                    podman push ${IMAGE_NAME}:${BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}
                    podman push ${IMAGE_NAME}:latest ${REGISTRY}/${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    ssh user@production-server << 'ENDSSH'
                        cd /opt/sungbok
                        podman-compose down
                        podman-compose pull
                        podman-compose up -d
                    ENDSSH
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    for i in {1..30}; do
                        if curl -f http://production-server:8080/actuator/health; then
                            echo "Health check passed"
                            exit 0
                        fi
                        echo "Waiting for service to start..."
                        sleep 2
                    done
                    echo "Health check failed"
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            slackSend color: 'good',
                      message: "배포 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger',
                      message: "배포 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
```

---

## 🔧 OCI 서버 설정

### 1. Podman 설치 (RHEL/CentOS/Rocky Linux)

```bash
# Podman 설치
sudo dnf install -y podman podman-compose

# Rootless 설정
podman info | grep rootless

# 방화벽 설정
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=5432/tcp
sudo firewall-cmd --permanent --add-port=6379/tcp
sudo firewall-cmd --reload
```

### 2. systemd 서비스 등록

```ini
# /etc/systemd/system/sungbok-backend.service
[Unit]
Description=Sungbok Church Backend
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=sungbok
WorkingDirectory=/opt/sungbok/backend/podman
ExecStart=/usr/bin/podman-compose up
ExecStop=/usr/bin/podman-compose down
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 활성화
sudo systemctl daemon-reload
sudo systemctl enable sungbok-backend
sudo systemctl start sungbok-backend

# 상태 확인
sudo systemctl status sungbok-backend
```

### 3. Nginx 리버스 프록시

```nginx
# /etc/nginx/conf.d/sungbok.conf
upstream backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name sungbok.church www.sungbok.church;

    # HTTPS 리다이렉트
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name sungbok.church www.sungbok.church;

    # SSL 인증서
    ssl_certificate /etc/letsencrypt/live/sungbok.church/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/sungbok.church/privkey.pem;

    # 보안 헤더
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;

    # API 프록시
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Swagger UI
    location /swagger-ui.html {
        proxy_pass http://backend;
    }

    # Health Check
    location /actuator/health {
        proxy_pass http://backend;
        access_log off;
    }
}
```

---

## 📊 모니터링 & 로깅

### 1. Podman 로그 수집

```bash
# 로그 확인
podman logs sungbok-backend

# 실시간 로그
podman logs -f sungbok-backend

# 로그 로테이션
podman run --log-driver=journald \
           --log-opt max-size=10m \
           --log-opt max-file=3 \
           sungbok-backend
```

### 2. 모니터링 대시보드

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'spring-actuator'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```

### 3. 알림 설정

```yaml
# alertmanager.yml
receivers:
  - name: 'slack'
    slack_configs:
      - api_url: 'https://hooks.slack.com/services/xxx'
        channel: '#ops-alerts'
        title: '🚨 Production Alert'
```

---

## 🔐 보안 설정

### 1. 환경 변수 관리

```bash
# .env.production (Git 제외)
DB_PASSWORD=<strong-password>
JWT_SECRET=<minimum-32-characters>
YOUTUBE_API_KEY=<api-key>
```

### 2. SELinux 설정

```bash
# SELinux 컨텍스트 설정
sudo semanage fcontext -a -t container_file_t "/opt/sungbok(/.*)?"
sudo restorecon -R /opt/sungbok
```

### 3. 방화벽 규칙

```bash
# 외부 접근 제한
sudo firewall-cmd --permanent --zone=public --add-rich-rule='
  rule family="ipv4"
  source address="0.0.0.0/0"
  port protocol="tcp" port="8080" reject
'

# Nginx만 접근 허용
sudo firewall-cmd --permanent --zone=trusted --add-source=127.0.0.1
```

---

## 🔄 백업 & 복구

### 1. 데이터베이스 백업

```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups"

# PostgreSQL 백업
podman exec sungbok-postgres pg_dump -U postgres sungbok_church \
    > ${BACKUP_DIR}/db_backup_${DATE}.sql

# 압축
gzip ${BACKUP_DIR}/db_backup_${DATE}.sql

# 7일 이상 된 백업 삭제
find ${BACKUP_DIR} -name "db_backup_*.sql.gz" -mtime +7 -delete
```

### 2. Cron 설정

```bash
# crontab -e
0 2 * * * /opt/sungbok/scripts/backup.sh
```

### 3. 복구 절차

```bash
# 1. 컨테이너 중지
podman-compose down

# 2. 데이터 복구
gunzip -c backup_file.sql.gz | podman exec -i sungbok-postgres \
    psql -U postgres -d sungbok_church

# 3. 컨테이너 시작
podman-compose up -d
```

---

## 📈 성능 최적화

### 1. Podman 설정

```toml
# /etc/containers/containers.conf
[containers]
log_size_max = 10485760
pids_limit = 2048
```

### 2. JVM 튜닝

```dockerfile
# Containerfile
ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC"
```

### 3. Nginx 캐싱

```nginx
# 정적 파일 캐싱
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

---

## 🧪 배포 테스트 계획

### 1. Pre-deployment 체크리스트

- [ ] 백업 완료 확인
- [ ] Health Check 엔드포인트 정상
- [ ] 환경 변수 설정 확인
- [ ] SSL 인증서 유효성 확인
- [ ] 데이터베이스 마이그레이션 준비

### 2. Smoke Test

```bash
#!/bin/bash
# smoke-test.sh

BASE_URL="https://sungbok.church"

# Health Check
curl -f ${BASE_URL}/actuator/health || exit 1

# API Test
curl -f ${BASE_URL}/api/notices || exit 1

# Swagger UI
curl -f ${BASE_URL}/swagger-ui.html || exit 1

echo "✓ Smoke test passed"
```

### 3. Rollback 절차

```bash
# 이전 버전으로 롤백
podman tag sungbok-backend:previous sungbok-backend:latest
podman-compose down
podman-compose up -d
```

---

## 📝 배포 체크리스트

### Phase 9 완료 기준

#### 인프라 구축
- [ ] OCI VM 프로비저닝
- [ ] Podman 설치 및 설정
- [ ] systemd 서비스 등록
- [ ] Nginx 리버스 프록시 설정

#### CI/CD
- [ ] Jenkins 서버 구축
- [ ] Jenkinsfile 작성
- [ ] 파이프라인 테스트
- [ ] 자동 배포 검증

#### 보안
- [ ] SSL 인증서 설치
- [ ] 방화벽 규칙 설정
- [ ] 환경 변수 암호화
- [ ] SELinux 설정

#### 모니터링
- [ ] Prometheus 설정
- [ ] Grafana 대시보드
- [ ] 알림 설정
- [ ] 로그 수집

#### 백업
- [ ] 자동 백업 스크립트
- [ ] Cron 설정
- [ ] 복구 절차 테스트

---

## 🎯 예상 일정

| 작업 | 소요 시간 | 담당 |
|------|----------|------|
| OCI 서버 설정 | 0.5일 | DevOps |
| Podman 설치 및 구성 | 0.5일 | DevOps |
| Jenkins 파이프라인 | 1일 | DevOps |
| Nginx + SSL 설정 | 0.5일 | DevOps |
| 모니터링 설정 | 0.5일 | DevOps |

**총 예상 시간**: 3일

---

## 📞 운영 가이드

### 일일 점검 항목
- [ ] 서비스 상태 확인 (`systemctl status`)
- [ ] 로그 에러 확인 (`podman logs`)
- [ ] 디스크 사용량 확인 (`df -h`)
- [ ] Health Check 응답 확인

### 주간 점검 항목
- [ ] 백업 파일 확인
- [ ] SSL 인증서 만료일 확인
- [ ] 보안 업데이트 적용
- [ ] 성능 메트릭 리뷰

### 비상 연락망
- **DevOps**: devops@sungbok.church
- **Backend**: backend@sungbok.church
- **On-call**: +82-10-xxxx-xxxx

---

**작성일**: 2026-02-04
**Phase**: 9 - Deployment
**상태**: 계획 완료 ✅
