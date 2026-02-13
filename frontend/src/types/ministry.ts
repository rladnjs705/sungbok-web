/**
 * Ministry Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface MinistryDTO {
  id: number;
  name: string;
  slug: string; // URL 라우팅용
  category: string; // "CHILDREN" | "YOUTH" | "YOUNG_ADULT" | "ADULT" | "SENIOR" | "MISSION" | "WORSHIP" | "MEDIA"
  description: string | null;
  targetAge: string | null;
  schedule: string | null;
  location: string | null;
  leader: string | null;
  contact: string | null;
  photoUrl: string | null;
  isActive: boolean;
  displayOrder: number | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Ministry {
  id: number;
  name: string;
  slug: string;
  category: string; // 한국어 카테고리
  description: string | null;
  targetAge: string | null;
  schedule: string | null;
  location: string | null;
  leader: string | null;
  contact: string | null;
  photo: string | null; // photoUrl → photo
  isActive: boolean;
  displayOrder: number | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Ministry 목록 조회 API 응답
 */
export interface MinistriesResponse {
  ministries: MinistryDTO[];
  total: number;
  page: number;
  size: number;
}

/**
 * Ministry 목록 조회 파라미터
 */
export interface GetMinistriesParams {
  page?: number;
  size?: number;
  category?: string;
  active?: boolean;
}
