/**
 * PrayerRequest Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * 기도요청 상태 Enum
 */
export type PrayerStatus = 'PENDING' | 'ANSWERED';

/**
 * Backend API 응답 DTO
 */
export interface PrayerRequestDTO {
  id: number;
  title: string;
  content: string;
  requester: string;
  isAnonymous: boolean;
  isApproved: boolean;
  status: PrayerStatus;
  prayerCount: number; // 기도 횟수
  answeredAt: string | null; // ISO 8601 format
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface PrayerRequest {
  id: number;
  title: string;
  content: string;
  requester: string | null; // isAnonymous가 true면 null
  isAnonymous: boolean;
  isApproved: boolean;
  status: PrayerStatus;
  prayerCount: number;
  answeredAt: string | null;
  createdAt: string; // "2024.03.17" 형식
}

/**
 * PrayerRequest 목록 조회 API 응답
 */
export interface PrayerRequestsResponse {
  content: PrayerRequestDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * PrayerRequest 목록 조회 파라미터
 */
export interface GetPrayerRequestsParams {
  page?: number;
  size?: number;
  status?: PrayerStatus;
  approved?: boolean;
}

/**
 * PrayerRequest 등록 Request
 */
export interface PrayerRequestRequest {
  title: string;
  content: string;
  requester: string;
  isAnonymous?: boolean;
}

/**
 * 기도하기 (Prayer Count 증가) Request
 */
export interface PrayerCountRequest {
  prayerRequestId: number;
}
