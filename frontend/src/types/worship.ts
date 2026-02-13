/**
 * Worship Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * 예배 유형 Enum
 */
export type WorshipType = 'SUNDAY' | 'WEDNESDAY' | 'DAWN' | 'FRIDAY' | 'SPECIAL';

/**
 * 요일 Enum (DayOfWeek)
 */
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

/**
 * Backend API 응답 DTO
 */
export interface WorshipDTO {
  id: number;
  type: WorshipType;
  title: string;
  description: string | null;
  dayOfWeek: DayOfWeek;
  startTime: string; // "HH:mm" 형식
  location: string | null;
  liveStreamUrl: string | null;
  isLiveNow: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Worship {
  id: number;
  type: WorshipType;
  typeLabel: string; // "주일예배", "수요예배" 등 한국어 레이블
  title: string;
  description: string | null;
  dayOfWeek: DayOfWeek;
  dayOfWeekLabel: string; // "일요일", "수요일" 등
  startTime: string; // "오전 11:00" 형식
  location: string | null;
  liveStreamUrl: string | null;
  isLiveNow: boolean;
  isActive: boolean;
}

/**
 * Worship 목록 조회 API 응답
 */
export interface WorshipsResponse {
  content: WorshipDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Worship 목록 조회 파라미터
 */
export interface GetWorshipsParams {
  page?: number;
  size?: number;
  type?: WorshipType;
  active?: boolean; // 활성화된 예배만 조회
  dayOfWeek?: DayOfWeek;
}

/**
 * Worship 등록/수정 Request
 */
export interface WorshipRequest {
  type: WorshipType;
  title: string;
  description?: string;
  dayOfWeek: DayOfWeek;
  startTime: string; // "HH:mm" 형식
  location?: string;
  liveStreamUrl?: string;
  isActive?: boolean;
}

/**
 * Worship 유형별 한국어 레이블 매핑
 */
export const WORSHIP_TYPE_LABELS: Record<WorshipType, string> = {
  SUNDAY: '주일예배',
  WEDNESDAY: '수요예배',
  DAWN: '새벽예배',
  FRIDAY: '금요기도회',
  SPECIAL: '특별예배',
};

/**
 * 요일 한국어 레이블 매핑
 */
export const DAY_OF_WEEK_LABELS: Record<DayOfWeek, string> = {
  SUNDAY: '일요일',
  MONDAY: '월요일',
  TUESDAY: '화요일',
  WEDNESDAY: '수요일',
  THURSDAY: '목요일',
  FRIDAY: '금요일',
  SATURDAY: '토요일',
};
