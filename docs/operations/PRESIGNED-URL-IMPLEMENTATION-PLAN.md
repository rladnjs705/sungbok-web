# Pre-signed URL 방식 파일 업로드 구현 계획

> **목적**: Java 25 TLS 문제 회피 + 확장성 있는 파일 업로드 아키텍처  
> **기술 스택**: Spring Boot 4.0.2 (Java 25) + Next.js 16

---

## 1. 전체 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND (Next.js)                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                 │
│  │  파일 선택   │───▶│  백엔드 API  │───▶│ Pre-signed │                 │
│  │  (브라우저)  │    │  (/upload)  │    │ URL 수신   │                 │
│  └─────────────┘    └─────────────┘    └──────┬──────┘                 │
│                                                 │                        │
│  ┌─────────────┐    ┌─────────────┐            │                        │
│  │ 업로드 완료  │◀───│   R2 직접   │◀───────────┘                        │
│  │  알림 전송  │    │   업로드    │                                     │
│  └──────┬──────┘    └─────────────┘                                     │
│         │                                                               │
│         ▼                                                               │
│  ┌─────────────┐                                                        │
│  │  백엔드 API  │                                                        │
│  │ (/complete) │                                                        │
│  └─────────────┘                                                        │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                              BACKEND (Spring Boot)                       │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                 │
│  │ Presigned   │───▶│    R2       │    │  파일 검증   │                 │
│  │ URL 생성    │    │  (S3 API)   │    │  (HEAD)     │                 │
│  └─────────────┘    └─────────────┘    └─────────────┘                 │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐                                    │
│  │   DB 저장   │    │  Orphan 정리 │                                    │
│  │ (FileUpload)│    │  (스케줄러)   │                                    │
│  └─────────────┘    └─────────────┘                                    │
└─────────────────────────────────────────────────────────────────────────┘
```

**핵심**: 서버가 R2에 직접 파일을 업로드하지 않고, **클리언트가 R2에 직접 업로드**
→ Java 25 TLS 핸드셰이크 문제 완전 회피

---

## 2. 프론트엔드 (Next.js) 설정

### 2.1 next.config.js 설정

```javascript
// frontend/next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  // API Routes CORS 설정
  async headers() {
    return [
      {
        source: '/api/:path*',
        headers: [
          { key: 'Access-Control-Allow-Credentials', value: 'true' },
          { key: 'Access-Control-Allow-Origin', value: process.env.NEXT_PUBLIC_API_URL || '*' },
          { key: 'Access-Control-Allow-Methods', value: 'GET,POST,PUT,DELETE,OPTIONS' },
          { key: 'Access-Control-Allow-Headers', value: 'Content-Type, Authorization' },
        ],
      },
    ];
  },

  // 이미지 도메인 설정 (R2 Public URL)
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'pub-*.r2.dev',
      },
      {
        protocol: 'https',
        hostname: '*.r2.cloudflarestorage.com',
      },
    ],
  },

  // 파일 업로드 크기 제한
  experimental: {
    serverActions: {
      bodySizeLimit: '10mb',
    },
  },
};

module.exports = nextConfig;
```

### 2.2 환경 변수 설정

```bash
# frontend/.env.local
NEXT_PUBLIC_API_URL=http://localhost:8081/api
NEXT_PUBLIC_R2_PUBLIC_URL=https://pub-ae0ae9316f064cf9a5a10592af41411f.r2.dev

# 파일 업로드 설정
NEXT_PUBLIC_MAX_FILE_SIZE=10485760  # 10MB
NEXT_PUBLIC_UPLOAD_TIMEOUT=300000    # 5분
```

### 2.3 API Routes (프론트엔드 → 백엔드 프록시)

```typescript
// frontend/app/api/upload/presign/route.ts
import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    
    // 백엔드로 Pre-signed URL 요청
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/uploads/presign`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': request.headers.get('Authorization') || '',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const error = await response.json();
      return NextResponse.json(error, { status: response.status });
    }

    const data = await response.json();
    return NextResponse.json(data);
    
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to get presigned URL' },
      { status: 500 }
    );
  }
}

// 업로드 완료 확인
// frontend/app/api/upload/[uploadId]/complete/route.ts
export async function POST(
  request: NextRequest,
  { params }: { params: { uploadId: string } }
) {
  try {
    const body = await request.json();
    
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/uploads/${params.uploadId}/complete`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': request.headers.get('Authorization') || '',
        },
        body: JSON.stringify(body),
      }
    );

    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
    
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to confirm upload' },
      { status: 500 }
    );
  }
}
```

### 2.4 파일 업로드 Hook

```typescript
// frontend/hooks/useFileUpload.ts
'use client';

import { useState, useCallback } from 'react';
import { useAuth } from '@/contexts/AuthContext';

interface UploadProgress {
  loaded: number;
  total: number;
  percentage: number;
}

interface UploadResult {
  success: boolean;
  fileUrl?: string;
  error?: string;
}

export function useFileUpload() {
  const [progress, setProgress] = useState<UploadProgress | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const { token } = useAuth();

  /**
   * SHA-256 체크섬 계산
   */
  const calculateChecksum = async (file: File): Promise<string> => {
    const buffer = await file.arrayBuffer();
    const hashBuffer = await crypto.subtle.digest('SHA-256', buffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  };

  /**
   * Pre-signed URL 요청
   */
  const getPresignedUrl = async (
    file: File,
    folder: string,
    checksum: string
  ): Promise<{ uploadId: number; url: string; key: string }> => {
    const response = await fetch('/api/upload/presign', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        filename: file.name,
        folder,
        contentType: file.type,
        fileSize: file.size,
        checksum,
      }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get presigned URL');
    }

    return response.json();
  };

  /**
   * R2에 직접 업로드 (진행률 추적)
   */
  const uploadToR2 = (
    url: string,
    file: File
  ): Promise<void> => {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      
      // 진행률 추적
      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          setProgress({
            loaded: event.loaded,
            total: event.total,
            percentage: Math.round((event.loaded / event.total) * 100),
          });
        }
      });

      xhr.addEventListener('load', () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          resolve();
        } else {
          reject(new Error(`Upload failed: ${xhr.statusText}`));
        }
      });

      xhr.addEventListener('error', () => reject(new Error('Upload failed')));
      xhr.addEventListener('abort', () => reject(new Error('Upload aborted')));

      xhr.open('PUT', url, true);
      xhr.setRequestHeader('Content-Type', file.type);
      // 체크섬 헤더 (권장)
      xhr.setRequestHeader('x-amz-checksum-sha256', btoa(checksum));
      xhr.send(file);
    });
  };

  /**
   * 업로드 완료 확인
   */
  const confirmUpload = async (
    uploadId: number,
    key: string,
    checksum: string
  ): Promise<UploadResult> => {
    const response = await fetch(`/api/upload/${uploadId}/complete`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({ key, checksum }),
    });

    const data = await response.json();
    
    if (!response.ok) {
      return { success: false, error: data.message };
    }

    return { success: true, fileUrl: data.fileUrl };
  };

  /**
   * 전체 업로드 프로세스
   */
  const upload = useCallback(
    async (
      file: File,
      folder: string,
      onProgress?: (progress: UploadProgress) => void
    ): Promise<UploadResult> => {
      setIsUploading(true);
      setProgress(null);

      try {
        // 1. 체크섬 계산
        const checksum = await calculateChecksum(file);

        // 2. Pre-signed URL 요청
        const { uploadId, url, key } = await getPresignedUrl(
          file,
          folder,
          checksum
        );

        // 3. R2에 직접 업로드
        await uploadToR2(url, file);

        // 4. 업로드 완료 확인
        const result = await confirmUpload(uploadId, key, checksum);
        
        return result;
      } catch (error) {
        return {
          success: false,
          error: error instanceof Error ? error.message : 'Upload failed',
        };
      } finally {
        setIsUploading(false);
        setProgress(null);
      }
    },
    [token]
  );

  return {
    upload,
    progress,
    isUploading,
  };
}
```

### 2.5 파일 업로드 컴포넌트

```tsx
// frontend/components/FileUpload.tsx
'use client';

import { useFileUpload } from '@/hooks/useFileUpload';
import { Progress } from '@/components/ui/progress';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useState, useRef } from 'react';

interface FileUploadProps {
  folder: string;
  onSuccess?: (url: string) => void;
  onError?: (error: string) => void;
}

export function FileUpload({ folder, onSuccess, onError }: FileUploadProps) {
  const { upload, progress, isUploading } = useFileUpload();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // 파일 크기 검증
      if (file.size > 10 * 1024 * 1024) {
        onError?.('파일 크기는 10MB를 초과할 수 없습니다.');
        return;
      }
      setSelectedFile(file);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    const result = await upload(selectedFile, folder);

    if (result.success && result.fileUrl) {
      onSuccess?.(result.fileUrl);
      setSelectedFile(null);
      if (inputRef.current) {
        inputRef.current.value = '';
      }
    } else {
      onError?.(result.error || '업로드 실패');
    }
  };

  return (
    <div className="space-y-4">
      <Input
        ref={inputRef}
        type="file"
        onChange={handleFileSelect}
        disabled={isUploading}
        accept="image/*,application/pdf,.doc,.docx"
      />

      {selectedFile && (
        <p className="text-sm text-gray-600">
          선택된 파일: {selectedFile.name} ({formatFileSize(selectedFile.size)})
        </p>
      )}

      {isUploading && progress && (
        <div className="space-y-2">
          <Progress value={progress.percentage} />
          <div className="flex justify-between text-sm text-gray-500">
            <span>{formatFileSize(progress.loaded)}</span>
            <span>{formatFileSize(progress.total)}</span>
          </div>
        </div>
      )}

      <Button
        onClick={handleUpload}
        disabled={!selectedFile || isUploading}
        className="w-full"
      >
        {isUploading ? '업로드 중...' : '업로드'}
      </Button>
    </div>
  );
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}
```

---

## 3. 백엔드-프론트 통신 API

### 3.1 API 명세

| 메소드 | 경로 | 설명 | 요청 본문 | 응답 |
|--------|------|------|-----------|------|
| POST | `/api/uploads/presign` | Pre-signed URL 발급 | `{filename, folder, contentType, fileSize, checksum}` | `{uploadId, url, key, expiresAt}` |
| POST | `/api/uploads/{id}/complete` | 업로드 완료 확인 | `{key, checksum}` | `{success, fileUrl}` |

### 3.2 에러 처리 흐름

```
[에러 발생 시]
1. 클라이언트에서 파일 선택 → 검증 (크기, 타입)
2. Pre-signed URL 요청 → 백엔드에서 권한/용량 검증
3. R2 직접 업로드 → 실패 시 재시도 (최대 3회)
4. 완료 확인 → 체크섬 불일치 시 파일 삭제
```

---

## 4. 구현 단계 (수정됨)

### Phase 1: 프론트엔드 기본 설정 (0.5일)
- [ ] next.config.js CORS 설정
- [ ] 환경 변수 설정
- [ ] API Routes 생성

### Phase 2: 백엔드 기본 구조 (1일)
- [ ] FileUpload Entity 생성
- [ ] PresignedUrlController 생성
- [ ] S3Presigner 설정

### Phase 3: 프론트엔드 업로드 구현 (1일)
- [ ] useFileUpload hook 구현
- [ ] FileUpload 컴포넌트 구현
- [ ] 진행률 표시 UI

### Phase 4: 통합 및 테스트 (1일)
- [ ] E2E 테스트
- [ ] 에러 처리 확인
- [ ] CORS 문제 해결

### Phase 5: 고급 기능 (1일)
- [ ] 체크섬 검증
- [ ] Orphan 정리 스케줄러
- [ ] 이미지 미리보기

---

## 5. CORS 문제 해결 체크리스트

### 5.1 R2 버킷 CORS 설정
```bash
# AWS CLI로 R2 CORS 설정
aws s3api put-bucket-cors \
  --bucket sungbok-church-files \
  --cors-configuration file://cors.json \
  --endpoint-url https://ae0ae9316f064cf9a5a10592af41411f.r2.cloudflarestorage.com
```

**cors.json**:
```json
{
  "CORSRules": [
    {
      "AllowedOrigins": [
        "https://www.sungbok-church.com",
        "http://localhost:3000"
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
}
```

### 5.2 문제 해결

| 문제 | 원인 | 해결 |
|------|------|------|
| CORS 에러 | R2 CORS 미설정 | 버킷 CORS 정책 설정 |
| 403 Forbidden | URL 만료 | 만료 시간 연장 (10→30분) |
| 체크섬 불일치 | 네트워크 오류 | 재업로드 또는 멀티파트 |

---

## 6. 테스트 시나리오

### 6.1 정상 케이스
1. 파일 선택 → 2. URL 요청 → 3. R2 업로드 → 4. 완료 확인 → 5. DB 저장

### 6.2 에러 케이스
1. 네트워크 중단 → 자동 재시도
2. URL 만료 → 새로운 URL 발급
3. 체크섬 불일치 → 파일 삭제 및 재업로드
4. 권한 없음 → 403 에러 반환

---

**이 계획으로 구현 시작할까요?** 수정 필요한 부분 있으면 말씀해 주세요.
