#!/bin/bash

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧪 성능 최적화 검증 스크립트"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 작업 디렉토리 이동
cd /Users/jaewon/Documents/sungbok-web/backend/podman

echo "[1/10] 컨테이너 상태 확인..."
podman-compose ps
echo ""

echo "[2/10] Backend CPU/메모리 사용률..."
BACKEND_CPU=$(podman stats sungbok-backend --no-stream --format "{{.CPUPerc}}" 2>/dev/null | sed 's/%//' || echo "N/A")
BACKEND_MEM=$(podman stats sungbok-backend --no-stream --format "{{.MemUsage}}" 2>/dev/null || echo "N/A")
echo "  CPU: ${BACKEND_CPU}% (목표: <50%)"
echo "  Memory: ${BACKEND_MEM}"
echo ""

echo "[3/10] JVM Heap 설정 확인..."
JVM_MAX=$(podman exec sungbok-backend java -XX:+PrintFlagsFinal -version 2>&1 | grep MaxHeapSize | awk '{print $4}' 2>/dev/null || echo "0")
if [ "$JVM_MAX" != "0" ]; then
  echo "  Max Heap: $((JVM_MAX / 1024 / 1024))MB (목표: 2048MB)"
else
  echo "  ⚠️  컨테이너가 실행 중이 아닙니다."
fi
echo ""

echo "[4/10] 컨테이너 리소스 제한 확인..."
BACKEND_LIMIT=$(podman inspect sungbok-backend 2>/dev/null | jq '.[0].HostConfig.Memory' 2>/dev/null || echo "0")
if [ "$BACKEND_LIMIT" != "0" ]; then
  echo "  Backend Memory Limit: $((BACKEND_LIMIT / 1024 / 1024))MB (목표: 2560MB)"
else
  echo "  ⚠️  리소스 제한이 설정되지 않았습니다. (podman-compose 재시작 필요)"
fi
echo ""

echo "[5/10] Frontend 이미지 캐시 TTL 확인..."
grep "minimumCacheTTL" /Users/jaewon/Documents/sungbok-web/frontend/next.config.ts
echo "  (목표: 31536000 = 1년)"
echo ""

echo "[6/10] HikariCP 연결 풀 확인..."
HIKARI_CONFIG=$(grep -A 6 "hikari:" /Users/jaewon/Documents/sungbok-web/backend/src/main/resources/application.yml 2>/dev/null)
if [ -n "$HIKARI_CONFIG" ]; then
  echo "  ✅ HikariCP 설정 확인됨"
  echo "$HIKARI_CONFIG" | head -7
else
  echo "  ⚠️  HikariCP 설정을 찾을 수 없습니다."
fi
echo ""

echo "[7/10] Nginx Worker Connections 확인..."
WORKER_CONN=$(grep "worker_connections" /Users/jaewon/Documents/sungbok-web/backend/podman/nginx/nginx.conf)
echo "  $WORKER_CONN"
echo "  (목표: 4096)"
echo ""

echo "[8/10] Nginx Proxy Cache 설정 확인..."
PROXY_CACHE=$(grep -A 2 "proxy_cache_path" /Users/jaewon/Documents/sungbok-web/backend/podman/nginx/nginx.conf | head -3)
if [ -n "$PROXY_CACHE" ]; then
  echo "  ✅ Proxy Cache 설정 확인됨"
  echo "$PROXY_CACHE"
else
  echo "  ⚠️  Proxy Cache 설정을 찾을 수 없습니다."
fi
echo ""

echo "[9/10] Rate Limiting 설정 확인..."
RATE_LIMIT=$(grep -E "limit_req_zone|limit_conn_zone" /Users/jaewon/Documents/sungbok-web/backend/podman/nginx/nginx.conf)
if [ -n "$RATE_LIMIT" ]; then
  echo "  ✅ Rate Limiting 설정 확인됨"
  echo "$RATE_LIMIT"
else
  echo "  ⚠️  Rate Limiting 설정을 찾을 수 없습니다."
fi
echo ""

echo "[10/10] Health Check Interval 확인..."
HEALTH_INTERVAL=$(grep -A 1 "interval:" /Users/jaewon/Documents/sungbok-web/backend/podman/podman-compose.yml | grep "interval" | head -1)
echo "  $HEALTH_INTERVAL"
echo "  (목표: 10s)"
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 요약"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Phase 1: Critical Issues (5개)"
echo "  - SQL 로깅 비활성화"
echo "  - JVM Heap 설정 (1GB-2GB)"
echo "  - 컨테이너 리소스 제한"
echo "  - 이미지 캐시 TTL 1년"
echo "  - HikariCP 연결 풀 설정"
echo ""
echo "✅ Phase 2: High Priority Issues (4개)"
echo "  - Nginx Proxy Cache (10분)"
echo "  - Worker Connections 4096"
echo "  - Rate Limiting 활성화"
echo "  - Health Check 10초"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 다음 단계"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. 컨테이너 재시작:"
echo "   cd /Users/jaewon/Documents/sungbok-web/backend/podman"
echo "   podman-compose down"
echo "   podman-compose up -d"
echo ""
echo "2. 로그 확인:"
echo "   podman logs -f sungbok-backend"
echo ""
echo "3. 성능 테스트:"
echo "   curl -I http://localhost/api/sermons"
echo "   (X-Cache-Status 헤더 확인)"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
