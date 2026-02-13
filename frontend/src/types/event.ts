/**
 * Event Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface EventDTO {
  id: number;
  title: string;
  description: string | null;
  category: string | null;
  organizer: string | null;
  posterImageUrl: string | null;
  location: string | null;
  startDate: string; // ISO 8601 format
  endDate: string; // ISO 8601 format
  registrationRequired: boolean;
  maxParticipants: number | null;
  currentParticipants: number;
  isPublished: boolean;
  isFull: boolean; // 계산된 필드 (maxParticipants <= currentParticipants)
  isOngoing: boolean; // 계산된 필드 (현재 시간이 startDate와 endDate 사이)
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Event {
  id: number;
  title: string;
  description: string | null;
  category: string | null;
  organizer: string | null;
  posterImage: string | null; // posterImageUrl -> posterImage
  location: string | null;
  startDate: string; // "2024.03.17 10:00" 형식
  endDate: string; // "2024.03.17 18:00" 형식
  registrationRequired: boolean;
  maxParticipants: number | null;
  currentParticipants: number;
  isFull: boolean;
  isOngoing: boolean;
  isPublished: boolean;
}

/**
 * Event 목록 조회 API 응답
 * Spring Data Page 응답 구조
 */
export interface EventsResponse {
  content: EventDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Event 목록 조회 파라미터
 */
export interface GetEventsParams {
  page?: number;
  size?: number;
  category?: string;
  ongoing?: boolean; // 진행 중인 행사만 조회
  upcoming?: boolean; // 예정된 행사만 조회
  startDate?: string; // ISO 8601
  endDate?: string; // ISO 8601
}

/**
 * Event 등록/수정 Request
 */
export interface EventRequest {
  title: string;
  description?: string;
  category?: string;
  organizer?: string;
  posterImageUrl?: string;
  location?: string;
  startDate: string; // ISO 8601
  endDate: string; // ISO 8601
  registrationRequired?: boolean;
  maxParticipants?: number;
  isPublished?: boolean;
}
