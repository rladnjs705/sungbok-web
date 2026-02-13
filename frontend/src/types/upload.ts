/**
 * File Upload Domain Types
 * 파일 업로드 기능을 위한 타입 정의
 */

/**
 * 업로드 진행 상태 타입
 */
export type UploadStatus = 'idle' | 'calculating' | 'presigning' | 'uploading' | 'completing' | 'success' | 'error';

/**
 * 업로드 진행률 인터페이스
 */
export interface UploadProgress {
  /** 업로드된 바이트 수 */
  loaded: number;
  /** 전체 파일 크기 (바이트) */
  total: number;
  /** 업로드 진행률 (0-100) */
  percentage: number;
}

/**
 * 업로드 결과 인터페이스
 */
export interface UploadResult {
  /** 업로드된 파일의 고유 ID */
  id: string;
  /** 업로드된 파일의 공개 URL */
  url: string;
  /** 업로드된 파일의 키 (R2 Object Key) */
  key: string;
  /** 파일 이름 */
  filename: string;
  /** 파일 크기 (바이트) */
  fileSize: number;
  /** 파일 MIME 타입 */
  contentType: string;
  /** SHA-256 체크섬 */
  checksum: string;
  /** 업로드 완료 시간 */
  uploadedAt: string;
}

/**
 * Pre-signed URL 요청 DTO
 */
export interface PresignRequest {
  /** 파일 이름 */
  filename: string;
  /** 업로드 폴더 경로 */
  folder: string;
  /** 파일 MIME 타입 */
  contentType: string;
  /** 파일 크기 (바이트) */
  fileSize: number;
  /** SHA-256 체크섬 (Base64 인코딩) */
  checksum: string;
}

/**
 * Pre-signed URL 응답 DTO
 */
export interface PresignResponse {
  /** 업로드 ID */
  uploadId: string;
  /** R2에 직접 업로드할 Pre-signed URL */
  presignedUrl: string;
  /** 업로드될 파일의 Key */
  key: string;
  /** 만료 시간 (ISO 8601) */
  expiresAt: string;
}

/**
 * 업로드 완료 확인 요청 DTO
 */
export interface CompleteUploadRequest {
  /** 업로드된 파일의 Key */
  key: string;
  /** 파일 SHA-256 체크섬 */
  checksum: string;
}

/**
 * 업로드 완료 확인 응답 DTO
 */
export interface CompleteUploadResponse {
  /** 업로드 성공 여부 */
  success: boolean;
  /** 업로드된 파일의 공개 URL */
  url: string;
  /** 에러 메시지 (실패 시) */
  error?: string;
}

/**
 * 파일 업로드 Hook 반환값 인터페이스
 */
export interface UseFileUploadReturn {
  /** 파일 업로드 함수 */
  upload: (file: File, folder: string) => Promise<UploadResult>;
  /** 현재 업로드 진행률 */
  progress: UploadProgress;
  /** 업로드 중 여부 */
  isUploading: boolean;
  /** 현재 업로드 상태 */
  status: UploadStatus;
  /** 에러 메시지 */
  error: string | null;
  /** 업로드 상태 초기화 함수 */
  reset: () => void;
}
