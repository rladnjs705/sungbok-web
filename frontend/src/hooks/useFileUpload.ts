/**
 * File Upload Hook
 * 
 * SHA-256 체크섬 계산, Pre-signed URL 요청, R2 직접 업로드를 처리하는 Hook입니다.
 * XMLHttpRequest를 사용하여 업로드 진행률을 추적합니다.
 */

import { useState, useCallback, useRef } from 'react';
import {
  UploadStatus,
  UploadProgress,
  UploadResult,
  PresignRequest,
  PresignResponse,
  CompleteUploadRequest,
  CompleteUploadResponse,
} from '@/types/upload';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

/**
 * 파일 크기를 포맷팅합니다.
 * @param bytes - 바이트 단위 크기
 * @returns 포맷팅된 문자열 (예: "1.5 MB")
 */
function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB'];
  const k = 1024;
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  const size = bytes / Math.pow(k, i);
  return `${size.toFixed(i === 0 ? 0 : 2)} ${units[i]}`;
}

/**
 * ArrayBuffer를 Base64 문자열로 변환합니다.
 */
function arrayBufferToBase64(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}

/**
 * 파일의 SHA-256 체크섬을 계산합니다.
 * @param file - 대상 파일
 * @returns Base64 인코딩된 체크섬
 */
async function calculateSHA256(file: File): Promise<string> {
  const buffer = await file.arrayBuffer();
  const hashBuffer = await crypto.subtle.digest('SHA-256', buffer);
  return arrayBufferToBase64(hashBuffer);
}

/**
 * Pre-signed URL을 요청합니다.
 */
async function requestPresignedUrl(
  request: PresignRequest,
  token: string
): Promise<PresignResponse> {
  const response = await fetch(`${API_BASE_URL}/upload/presign`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Pre-signed URL 요청 실패: ${response.status} ${errorText}`);
  }

  return response.json();
}

/**
 * R2에 파일을 업로드합니다.
 * XMLHttpRequest를 사용하여 진행률을 추적합니다.
 */
function uploadToR2(
  presignedUrl: string,
  file: File,
  onProgress: (progress: UploadProgress) => void
): Promise<void> {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();

    // 업로드 진행률 이벤트 리스너
    xhr.upload.addEventListener('progress', (event) => {
      if (event.lengthComputable) {
        const percentage = Math.round((event.loaded / event.total) * 100);
        onProgress({
          loaded: event.loaded,
          total: event.total,
          percentage,
        });
      }
    });

    // 업로드 완료/에러 이벤트 리스너
    xhr.addEventListener('load', () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve();
      } else {
        reject(new Error(`R2 업로드 실패: ${xhr.status} ${xhr.statusText}`));
      }
    });

    xhr.addEventListener('error', () => {
      reject(new Error('R2 업로드 중 네트워크 에러가 발생했습니다.'));
    });

    xhr.addEventListener('abort', () => {
      reject(new Error('업로드가 중단되었습니다.'));
    });

    xhr.open('PUT', presignedUrl, true);
    xhr.setRequestHeader('Content-Type', file.type);
    xhr.send(file);
  });
}

/**
 * 업로드 완료를 서버에 알립니다.
 */
async function completeUpload(
  uploadId: string,
  request: CompleteUploadRequest,
  token: string
): Promise<CompleteUploadResponse> {
  const response = await fetch(`${API_BASE_URL}/upload/${uploadId}/complete`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`업로드 완료 확인 실패: ${response.status} ${errorText}`);
  }

  return response.json();
}

/**
 * 파일 업로드 Hook
 * @returns 업로드 함수, 진행률, 상태 등
 */
export function useFileUpload(): {
  upload: (file: File, folder: string, token: string) => Promise<UploadResult>;
  progress: UploadProgress;
  isUploading: boolean;
  status: UploadStatus;
  error: string | null;
  reset: () => void;
} {
  const [progress, setProgress] = useState<UploadProgress>({
    loaded: 0,
    total: 0,
    percentage: 0,
  });
  const [status, setStatus] = useState<UploadStatus>('idle');
  const [error, setError] = useState<string | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  const reset = useCallback(() => {
    setProgress({ loaded: 0, total: 0, percentage: 0 });
    setStatus('idle');
    setError(null);
    abortControllerRef.current?.abort();
    abortControllerRef.current = null;
  }, []);

  const upload = useCallback(
    async (file: File, folder: string, token: string): Promise<UploadResult> => {
      // 파일 크기 검증
      if (file.size > MAX_FILE_SIZE) {
        throw new Error(
          `파일 크기가 너무 큽니다. (${formatFileSize(file.size)} / 최대 ${formatFileSize(
            MAX_FILE_SIZE
          )})`
        );
      }

      try {
        // 상태 초기화
        setError(null);
        abortControllerRef.current = new AbortController();

        // 1. SHA-256 체크섬 계산
        setStatus('calculating');
        const checksum = await calculateSHA256(file);

        // 2. Pre-signed URL 요청
        setStatus('presigning');
        const presignRequest: PresignRequest = {
          filename: file.name,
          folder,
          contentType: file.type,
          fileSize: file.size,
          checksum,
        };
        const presignResponse = await requestPresignedUrl(presignRequest, token);

        // 3. R2에 직접 업로드
        setStatus('uploading');
        setProgress({ loaded: 0, total: file.size, percentage: 0 });

        await uploadToR2(presignResponse.presignedUrl, file, (uploadProgress) => {
          setProgress(uploadProgress);
        });

        // 4. 업로드 완료 확인
        setStatus('completing');
        const completeRequest: CompleteUploadRequest = {
          key: presignResponse.key,
          checksum,
        };
        const completeResponse = await completeUpload(
          presignResponse.uploadId,
          completeRequest,
          token
        );

        if (!completeResponse.success) {
          throw new Error(completeResponse.error || '업로드 완료 확인에 실패했습니다.');
        }

        setStatus('success');

        return {
          id: presignResponse.uploadId,
          url: completeResponse.url,
          key: presignResponse.key,
          filename: file.name,
          fileSize: file.size,
          contentType: file.type,
          checksum,
          uploadedAt: new Date().toISOString(),
        };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '알 수 없는 에러가 발생했습니다.';
        setError(errorMessage);
        setStatus('error');
        throw new Error(errorMessage);
      }
    },
    []
  );

  return {
    upload,
    progress,
    isUploading: status !== 'idle' && status !== 'success' && status !== 'error',
    status,
    error,
    reset,
  };
}
