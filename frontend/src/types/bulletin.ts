/**
 * Bulletin Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface BulletinDTO {
  id: number;
  title: string;
  bulletinDate: string; // ISO 8601 format (YYYY-MM-DD)
  pdfUrl: string;
  fileSize: number; // 바이트 단위
  thumbnailUrl: string | null;
  downloadCount: number;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Bulletin {
  id: number;
  title: string;
  date: string; // bulletinDate -> date ("2024.03.17" 형식)
  pdfUrl: string;
  fileSize: number; // 바이트 단위 ("2.5MB" 등으로 변환하여 표시)
  thumbnailUrl: string | null;
  downloadCount: number;
  isPublished: boolean;
}

/**
 * Bulletin 목록 조회 API 응답
 */
export interface BulletinsResponse {
  content: BulletinDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Bulletin 목록 조회 파라미터
 */
export interface GetBulletinsParams {
  page?: number;
  size?: number;
  year?: number; // 특정 연도 필터
  month?: number; // 특정 월 필터 (1-12)
}

/**
 * Bulletin 등록/수정 Request
 */
export interface BulletinRequest {
  title: string;
  bulletinDate: string; // ISO 8601 (YYYY-MM-DD)
  pdfUrl: string;
  fileSize: number;
  thumbnailUrl?: string;
  isPublished?: boolean;
}
