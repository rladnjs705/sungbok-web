/**
 * Testimony Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface TestimonyDTO {
  id: number;
  title: string;
  author: string;
  content: string;
  category: string | null;
  isApproved: boolean;
  viewCount: number;
  publishedAt: string | null; // ISO 8601 format
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Testimony {
  id: number;
  title: string;
  author: string;
  content: string;
  category: string | null;
  isApproved: boolean;
  viewCount: number;
  date: string | null; // publishedAt -> date ("2024.03.17" 형식)
}

/**
 * Testimony 목록 조회 API 응답
 */
export interface TestimoniesResponse {
  content: TestimonyDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Testimony 목록 조회 파라미터
 */
export interface GetTestimoniesParams {
  page?: number;
  size?: number;
  category?: string;
  approved?: boolean; // 승인된 것만 조회
}

/**
 * Testimony 등록 Request (사용자)
 */
export interface TestimonyRequest {
  title: string;
  author: string;
  content: string;
  category?: string;
}

/**
 * Testimony 승인/거절 Request (관리자)
 */
export interface TestimonyApprovalRequest {
  isApproved: boolean;
  rejectionReason?: string;
}
