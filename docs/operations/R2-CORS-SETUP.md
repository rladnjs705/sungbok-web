# R2 CORS 설정 가이드

## 방법 1: Cloudflare Dashboard (권장)

1. **Cloudflare Dashboard 접속**: https://dash.cloudflare.com
2. **R2 메뉴 선택** → **sungbok-church-files** 버킷 클릭
3. **Settings** 탭 클릭
4. **CORS Policy** 섹션에서 **Add CORS Policy** 클릭
5. 아래 JSON 입력:

```json
[
  {
    "AllowedOrigins": [
      "http://localhost:3001",
      "https://www.sungbok-church.com"
    ],
    "AllowedMethods": ["PUT", "GET", "HEAD"],
    "AllowedHeaders": [
      "Content-Type",
      "Content-Length",
      "x-amz-checksum-sha256",
      "x-amz-meta-*"
    ],
    "ExposeHeaders": ["ETag", "x-amz-checksum-sha256"],
    "MaxAgeSeconds": 3600
  }
]
```

## 방법 2: AWS CLI (터미널에서)

```bash
# AWS CLI 설치 후 설정
aws configure
# Access Key: R2_ACCESS_KEY
# Secret Key: R2_SECRET_KEY
# Region: auto

# CORS 설정
aws s3api put-bucket-cors \
  --bucket sungbok-church-files \
  --cors-configuration file://cors.json \
  --endpoint-url https://ae0ae9316f064cf9a5a10592af41411f.r2.cloudflarestorage.com
```

## 방법 3: 백엔드에서 프로그래밍 방식

```java
// R2Config.java에 추가
@PostConstruct
public void configureCors() {
    PutBucketCorsRequest corsRequest = PutBucketCorsRequest.builder()
        .bucket(bucket)
        .corsConfiguration(CorsConfiguration.builder()
            .corsRules(CorsRule.builder()
                .allowedOrigins("http://localhost:3001")
                .allowedMethods("PUT", "GET")
                .allowedHeaders("Content-Type", "x-amz-checksum-sha256")
                .maxAgeSeconds(3600)
                .build())
            .build())
        .build();
    
    s3Client.putBucketCors(corsRequest);
}
```

## 테스트 방법

```bash
# 1. 서버 상태 확인
curl http://localhost:8081/actuator/health

# 2. Pre-signed URL 발급 테스트
curl -X POST http://localhost:8081/api/uploads/presign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "filename": "test.txt",
    "folder": "notices",
    "contentType": "text/plain",
    "fileSize": 100,
    "checksum": "abc123"
  }'

# 3. 프론트엔드 접속
open http://localhost:3001
```

## 현재 실행 중인 서비스

| 서비스 | URL | 상태 |
|--------|-----|------|
| 백엔드 | http://localhost:8081 | ✅ 실행 중 |
| 프론트엔드 | http://localhost:3001 | ✅ 실행 중 |
| DB | localhost:5432 | ✅ 실행 중 |
| Redis | localhost:6379 | ✅ 실행 중 |
