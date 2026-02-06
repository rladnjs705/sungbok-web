#!/bin/bash

# Zero Script QA - Podman 로그 기반 테스트 자동화
# 작성일: 2026-02-04
# 목적: 테스트 스크립트 없이 Podman 로그를 분석하여 API 품질 검증

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 파일 (절대 경로 사용)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="${SCRIPT_DIR}/qa-logs"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
QA_REPORT="${LOG_DIR}/qa-report-${TIMESTAMP}.md"
PODMAN_LOG="${LOG_DIR}/podman-${TIMESTAMP}.log"

# 디렉토리 생성
mkdir -p ${LOG_DIR}

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Zero Script QA - 성북교회 백엔드 API  ${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# QA Report 초기화
cat > ${QA_REPORT} << 'REPORT_HEADER'
# Zero Script QA Report

**생성 시간**: $(date "+%Y-%m-%d %H:%M:%S")
**테스트 방식**: Podman 로그 기반 Zero Script QA

---

## 1. 환경 설정 검증

REPORT_HEADER

# Step 1: Podman 환경 확인
echo -e "${YELLOW}[1/7]${NC} Podman 환경 확인 중..."
if ! command -v podman &> /dev/null; then
    echo -e "${RED}✗ Podman이 설치되어 있지 않습니다.${NC}"
    echo "### ✗ Podman 미설치" >> ${QA_REPORT}
    exit 1
fi

if ! command -v podman-compose &> /dev/null; then
    echo -e "${RED}✗ Podman Compose가 설치되어 있지 않습니다.${NC}"
    echo "### ✗ Podman Compose 미설치" >> ${QA_REPORT}
    exit 1
fi

echo -e "${GREEN}✓ Podman 환경 확인 완료${NC}"
echo "### ✓ Podman 환경 확인 완료" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# Step 2: 기존 컨테이너 정리
echo -e "${YELLOW}[2/7]${NC} 기존 컨테이너 정리 중..."
cd podman
podman-compose down -v 2>&1 | tee -a ${PODMAN_LOG}
echo -e "${GREEN}✓ 기존 컨테이너 정리 완료${NC}"

# Step 3: Podman 이미지 빌드 및 컨테이너 시작
echo -e "${YELLOW}[3/7]${NC} Podman 컨테이너 시작 중..."
echo "## 2. Podman 컨테이너 시작" >> ${QA_REPORT}
echo '```bash' >> ${QA_REPORT}
podman-compose up -d --build 2>&1 | tee -a ${PODMAN_LOG} | tee -a ${QA_REPORT}
echo '```' >> ${QA_REPORT}
echo "" >> ${QA_REPORT}
cd ..

# Step 4: 서비스 Health Check
echo -e "${YELLOW}[4/7]${NC} 서비스 Health Check 대기 중..."
echo "## 3. Health Check 결과" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

MAX_WAIT=120
WAIT_COUNT=0
HEALTH_OK=false

while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
        HEALTH_OK=true
        break
    fi
    echo -n "."
    sleep 2
    WAIT_COUNT=$((WAIT_COUNT + 2))
done

echo ""

if [ "$HEALTH_OK" = true ]; then
    echo -e "${GREEN}✓ Health Check 통과 (${WAIT_COUNT}초 소요)${NC}"
    echo "### ✓ Health Check 통과" >> ${QA_REPORT}
    echo "- 소요 시간: ${WAIT_COUNT}초" >> ${QA_REPORT}
else
    echo -e "${RED}✗ Health Check 실패 (${MAX_WAIT}초 타임아웃)${NC}"
    echo "### ✗ Health Check 실패" >> ${QA_REPORT}
    echo "- 타임아웃: ${MAX_WAIT}초" >> ${QA_REPORT}
    echo "" >> ${QA_REPORT}
    echo "#### Podman 로그:" >> ${QA_REPORT}
    echo '```' >> ${QA_REPORT}
    podman logs sungbok-backend --tail 50 2>&1 | tee -a ${QA_REPORT}
    echo '```' >> ${QA_REPORT}
    exit 1
fi

echo "" >> ${QA_REPORT}

# Step 5: API 엔드포인트 테스트
echo -e "${YELLOW}[5/7]${NC} 주요 API 엔드포인트 테스트 중..."
echo "## 4. API 엔드포인트 테스트" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# 테스트할 엔드포인트 목록
declare -A ENDPOINTS=(
    ["Health Check"]="http://localhost:8080/actuator/health"
    ["OpenAPI Docs"]="http://localhost:8080/v3/api-docs"
    ["Swagger UI"]="http://localhost:8080/swagger-ui.html"
)

SUCCESS_COUNT=0
FAIL_COUNT=0

for name in "${!ENDPOINTS[@]}"; do
    url="${ENDPOINTS[$name]}"
    echo -n "  Testing: $name ... "

    if response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>&1); then
        if [ "$response" -eq 200 ]; then
            echo -e "${GREEN}✓ $response${NC}"
            echo "- ✓ **$name**: HTTP $response" >> ${QA_REPORT}
            SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
        else
            echo -e "${RED}✗ $response${NC}"
            echo "- ✗ **$name**: HTTP $response" >> ${QA_REPORT}
            FAIL_COUNT=$((FAIL_COUNT + 1))
        fi
    else
        echo -e "${RED}✗ Connection Failed${NC}"
        echo "- ✗ **$name**: Connection Failed" >> ${QA_REPORT}
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
done

echo "" >> ${QA_REPORT}
echo "**결과**: ${SUCCESS_COUNT}/${#ENDPOINTS[@]} 성공" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# Step 6: Podman 로그 분석
echo -e "${YELLOW}[6/7]${NC} Podman 로그 분석 중..."
echo "## 5. Podman 로그 분석" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# 에러 패턴 검색
podman logs sungbok-backend > ${PODMAN_LOG} 2>&1

ERROR_COUNT=$(grep -i "error" ${PODMAN_LOG} | wc -l | tr -d ' ')
WARN_COUNT=$(grep -i "warn" ${PODMAN_LOG} | wc -l | tr -d ' ')
EXCEPTION_COUNT=$(grep -i "exception" ${PODMAN_LOG} | wc -l | tr -d ' ')

echo "### 로그 통계" >> ${QA_REPORT}
echo "- 🔴 ERROR: ${ERROR_COUNT}건" >> ${QA_REPORT}
echo "- 🟡 WARN: ${WARN_COUNT}건" >> ${QA_REPORT}
echo "- 🔴 EXCEPTION: ${EXCEPTION_COUNT}건" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# 심각한 에러 추출
if [ $ERROR_COUNT -gt 0 ] || [ $EXCEPTION_COUNT -gt 0 ]; then
    echo "### 발견된 에러 (최근 20개)" >> ${QA_REPORT}
    echo '```' >> ${QA_REPORT}
    grep -i -E "(error|exception)" ${PODMAN_LOG} | tail -20 >> ${QA_REPORT}
    echo '```' >> ${QA_REPORT}
    echo "" >> ${QA_REPORT}
fi

# 애플리케이션 시작 확인
if grep -q "Started SungbokChurchBackendApplication" ${PODMAN_LOG}; then
    echo -e "${GREEN}✓ 애플리케이션 정상 시작${NC}"
    echo "### ✓ 애플리케이션 정상 시작" >> ${QA_REPORT}
else
    echo -e "${YELLOW}⚠ 애플리케이션 시작 로그 미확인${NC}"
    echo "### ⚠ 애플리케이션 시작 로그 미확인" >> ${QA_REPORT}
fi

echo "" >> ${QA_REPORT}

# Step 7: 최종 리포트 생성
echo -e "${YELLOW}[7/7]${NC} 최종 리포트 생성 중..."
echo "## 6. 최종 평가" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

# 품질 점수 계산
TOTAL_SCORE=100
if [ $FAIL_COUNT -gt 0 ]; then
    TOTAL_SCORE=$((TOTAL_SCORE - FAIL_COUNT * 10))
fi
if [ $ERROR_COUNT -gt 0 ]; then
    TOTAL_SCORE=$((TOTAL_SCORE - ERROR_COUNT * 2))
fi
if [ $EXCEPTION_COUNT -gt 0 ]; then
    TOTAL_SCORE=$((TOTAL_SCORE - EXCEPTION_COUNT * 5))
fi

# 최소값 보정
if [ $TOTAL_SCORE -lt 0 ]; then
    TOTAL_SCORE=0
fi

echo "### 품질 점수: ${TOTAL_SCORE}/100" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}

if [ $TOTAL_SCORE -ge 90 ]; then
    echo "**평가**: ✅ 우수 - 프로덕션 배포 가능" >> ${QA_REPORT}
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}  ✅ QA 통과 - 품질 점수: ${TOTAL_SCORE}/100  ${NC}"
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
elif [ $TOTAL_SCORE -ge 70 ]; then
    echo "**평가**: ⚠️  양호 - 경미한 이슈 존재" >> ${QA_REPORT}
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${YELLOW}  ⚠️  QA 통과 (주의) - 품질 점수: ${TOTAL_SCORE}/100  ${NC}"
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
else
    echo "**평가**: ❌ 불합격 - 주요 이슈 해결 필요" >> ${QA_REPORT}
    echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${RED}  ❌ QA 실패 - 품질 점수: ${TOTAL_SCORE}/100  ${NC}"
    echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
fi

echo "" >> ${QA_REPORT}
echo "## 7. 생성된 파일" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}
echo "- QA 리포트: \`${QA_REPORT}\`" >> ${QA_REPORT}
echo "- Podman 로그: \`${PODMAN_LOG}\`" >> ${QA_REPORT}
echo "" >> ${QA_REPORT}
echo "---" >> ${QA_REPORT}
echo "*Generated by Zero Script QA v1.0 (Podman)*" >> ${QA_REPORT}

echo ""
echo -e "${BLUE}📊 QA 리포트: ${QA_REPORT}${NC}"
echo -e "${BLUE}📝 Podman 로그: ${PODMAN_LOG}${NC}"
echo ""
echo -e "${GREEN}✓ Zero Script QA 완료!${NC}"

# 컨테이너 정리 옵션
echo ""
read -p "Podman 컨테이너를 중지하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    cd podman
    podman-compose down
    cd ..
    echo -e "${GREEN}✓ 컨테이너 중지 완료${NC}"
else
    echo -e "${BLUE}ℹ️  컨테이너가 계속 실행 중입니다.${NC}"
    echo -e "${BLUE}   - Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
    echo -e "${BLUE}   - Health Check: http://localhost:8080/actuator/health${NC}"
fi

exit 0
