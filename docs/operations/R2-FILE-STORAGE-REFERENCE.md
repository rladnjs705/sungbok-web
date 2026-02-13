# Cloudflare R2 파일 저장소 구현 참고 문서

> **참고 프로젝트**: `/Users/jaewon/Documents/Sungbok/sungbok-community`  
> **작성일**: 2026-02-12  
> **목적**: sungbok-web 프로젝트의 파일 업로드 기능 구현 참고용

---

## 1. 의존성 설정

### AWS SDK (S3-compatible)

```gradle
// AWS S3 SDK v2 (OCI S3 호환 API 사용)
implementation platform('software.amazon.awssdk:bom:2.41.25')
implementation ('software.amazon.awssdk:s3') {
    exclude group: 'software.amazon.awssdk', module: 'netty-nio-client'
}
// Apache HTTP Client (동기 작업에 최적화, Netty 취약점 제거)
implementation 'software.amazon.awssdk:apache-client'

// Apache Tika (MIME Type 검증 - Magic Number - 3.2.3)
implementation 'org.apache.tika:tika-core:3.2.3'

// FFmpeg Java wrapper (동영상 메타데이터 검증 - Probing)
implementation 'net.bramp.ffmpeg:ffmpeg:0.8.0'
```

### 의존성 설명

| 의존성 | 버전 | 용도 |
|--------|------|------|
| AWS SDK v2 | 2.41.25 | S3 API 호환 클라이언트 |
| Apache HTTP Client | latest | 동기 HTTP 요청 처리 |
| Apache Tika | 3.2.3 | Magic Number 기반 MIME 타입 검증 |
| FFmpeg Java | 0.8.0 | 동영상 메타데이터 추출 |

---

## 2. 아키텍처 패턴

### 2.1 CQRS (Command Query Responsibility Segregation)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Controller Layer                          │
├─────────────────────────────────────────────────────────────────┤
│  FilesController                                                  │
│  ├── POST /files/upload-presigned  → ChangeFileService           │
│  ├── PUT /files/{id}/uploaded      → ChangeFileService           │
│  ├── GET /files/{id}               → GetFileService              │
│  └── DELETE /files/{id}            → ChangeFileService           │
└─────────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┴───────────────────┐
        │                                       │
┌───────▼────────┐                    ┌─────────▼─────────┐
│  Command (Write)│                    │   Query (Read)     │
├────────────────┤                    ├───────────────────┤
│ChangeFileService│                   │ GetFileService     │
│  - create()    │                    │  - getById()       │
│  - update()    │                    │  - getByEntity()   │
│  - delete()    │                    │  - list()          │
└────────────────┘                    └───────────────────┘
```

### 2.2 Pre-signed URL 방식

```
[흐름도]

1. 클라이언트 → 백엔드: 파일 메타데이터 전송
   POST /files/upload-presigned
   {
     "filename": "document.pdf",
     "mimeType": "application/pdf",
     "size": 1024000
   }

2. 백엔드 → 클라이언트: Pre-signed URL 반환
   {
     "fileId": 123,
     "uploadUrl": "https://r2.cloudflarestorage.com/...",
     "expiresAt": "2026-02-12T12:00:00Z"
   }

3. 클라이언트 → R2: 직접 업로드
   PUT https://r2.cloudflarestorage.com/...
   Body: 파일 바이너리

4. 클라이언트 → 백엔드: 업로드 완료 알림
   PUT /files/123/uploaded

5. 백엔드: 비동기 검증 트리거
   @EventListener → Magic Number 검증 (Tika)
```

**장점**:
- 서버 대역폭 절약 (클이언트가 R2에 직접 업로드)
- 확장성 우수
- 대용량 파일 처리 가능

**단점**:
- 업로드 완료 확인 로직 필요
- 구현 복잡도 증가

---

## 3. 파일 상태 관리

| 상태 | 설명 | 전환 조건 |
|------|------|-----------|
| **PENDING** | URL 생성됨, 업로드 대기중 | Pre-signed URL 생성 시 |
| **ACTIVE** | 업로드 완료, 검증 대기중 | 클라이언트가 업로드 완료 알림 |
| **VERIFIED** | Magic Number 검증 완료 | 비동기 검증 완료 |
| **REJECTED** | 검증 실패 | Magic Number 불일치 |

---

## 4. 보안 체계

### 4.1 파일명 정제 (Path Traversal 방어)

```java
private boolean containsPathTraversal(String filename) {
    String[] dangerousPatterns = {
        "..",           // 상위 디렉토리
        "./",           // 현재 디렉토리  
        "../",          // 상위 디렉토리 경로
        "..\\",         // Windows 상위 디렉토리
        ".\\",          // Windows 현재 디렉토리
        "/",            // 절대 경로
        "\\",           // Windows 절대 경로
        "\0",           // Null byte injection
        "%00",          // URL-encoded null byte
        "%2e%2e",       // URL-encoded ..
        "%2f",          // URL-encoded /
        "%5c"           // URL-encoded \
    };
    // ... 검증 로직
}
```

### 4.2 MIME 타입 검증

```java
// 1. 화이트리스트 검증
List<String> allowedTypes = Arrays.asList(
    "image/jpeg",
    "image/png", 
    "image/webp",
    "application/pdf",
    "video/mp4"
);

// 2. Magic Number 검증 (Apache Tika)
Tika tika = new Tika();
String detectedType = tika.detect(fileInputStream);
if (!allowedTypes.contains(detectedType)) {
    throw new ValidationException("허용되지 않은 파일 형식");
}
```

### 4.3 파일 크기 제한

| 파일 유형 | 최대 크기 |
|-----------|-----------|
| 이미지 | 10MB |
| 문서 | 10MB |
| 동영상 | 100MB |

---

## 5. 데이터베이스 스키마 참고

```sql
CREATE TABLE files (
    file_id BIGSERIAL PRIMARY KEY,
    org_id BIGINT NOT NULL,                    -- 멀티테넌시 (선택)
    related_entity_id BIGINT NOT NULL,         -- 게시글/댓글 ID
    related_entity_type VARCHAR(50) NOT NULL,  -- post, comment 등
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,           -- R2 Object Key
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    uploader_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',      -- PENDING, ACTIVE, VERIFIED, REJECTED
    uploaded_at TIMESTAMP,
    
    -- 동영상 메타데이터 (선택)
    duration DOUBLE PRECISION,                 -- 재생 시간(초)
    resolution VARCHAR(20),                    -- 1920x1080
    codec VARCHAR(50),                         -- h264
    
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    modified_at TIMESTAMP DEFAULT NOW()
);
```

---

## 6. 설정 (application.yml)

```yaml
r2:
  endpoint: ${R2_ENDPOINT:https://<account-id>.r2.cloudflarestorage.com}
  access-key: ${R2_ACCESS_KEY}
  secret-key: ${R2_SECRET_KEY}
  bucket: ${R2_BUCKET:sungbok-church-files}
  public-url: ${R2_PUBLIC_URL:https://pub-<account-id>.r2.dev}
  presigned-url-expiration: 600  # 10분

file:
  upload:
    max-size:
      default: 10485760      # 10MB
      video: 104857600       # 100MB
    allowed-mime-types:
      - image/jpeg
      - image/png
      - image/webp
      - application/pdf
      - video/mp4

ffmpeg:
  path: ${FFMPEG_PATH:/usr/bin/ffmpeg}
  probe-path: ${FFPROBE_PATH:/usr/bin/ffprobe}
```

---

## 7. S3Client 설정

```java
@Bean
public S3Client s3Client(R2Properties props) {
    return S3Client.builder()
        .endpointOverride(URI.create(props.getEndpoint()))
        .region(Region.of("auto"))  // R2는 region 개념 없음
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
        ))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)    // R2 필수
            .chunkedEncodingEnabled(false)   // 권장
            .build())
        .httpClient(ApacheHttpClient.builder()
            .connectionTimeout(Duration.ofSeconds(10))
            .socketTimeout(Duration.ofSeconds(30))
            .maxConnections(100)
            .build())
        .build();
}

@Bean
public S3Presigner s3Presigner(R2Properties props) {
    return S3Presigner.builder()
        .endpointOverride(URI.create(props.getEndpoint()))
        .region(Region.of("auto"))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
        ))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .chunkedEncodingEnabled(false)
            .build())
        .build();
}
```

---

## 8. 테스트 전략

### 8.1 단위 테스트 (In-Memory)

```java
@Profile("test")
@Service
public class InMemoryFileStorageService implements FileStorageService {
    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        storage.put(key, file.getBytes());
        return "memory://" + key;
    }
    // ... 구현
}
```

### 8.2 통합 테스트 (LocalStack)

```java
@Testcontainers
@SpringBootTest
public class FileStorageIntegrationTest {
    
    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.0")
    ).withServices(LocalStackContainer.Service.S3);
    
    // ... 테스트
}
```

---

## 9. 참고 코드 위치

| 프로젝트 | 경로 | 설명 |
|----------|------|------|
| sungbok-community | `src/main/java/.../service/storage/` | OCI Storage Service |
| sungbok-community | `src/main/java/.../service/file/` | File Validation Service |
| sungbok-community | `src/main/java/.../controller/FilesController.java` | REST API |

---

## 10. sungbok-web 적용 시 고려사항

### 현재 상태 (2026-02-12)
- AWS SDK 2.30.32 사용 중
- 직접 업로드 방식 구현 중
- FileStorageService 인터페이스 정의 완료

### 검토 필요 사항
1. AWS SDK 2.41.25로 업그레이드 여부
2. Pre-signed URL 방식 적용 여부
3. CQRS 패턴 적용 여부
4. 파일 상태 관리 (PENDING/ACTIVE/VERIFIED) 필요 여부
5. 동영상 메타데이터 추출 필요 여부

---

> **참고**: 이 문서는 sungbok-community 프로젝트의 파일 업로드 구현을 분석하여 작성했습니다.
