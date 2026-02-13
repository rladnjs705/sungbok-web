/**
 * VideoGallery Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface VideoGalleryDTO {
  id: number;
  title: string;
  description: string | null;
  youtubeVideoId: string | null;
  videoUrl: string | null;
  thumbnailUrl: string | null;
  eventDate: string | null; // ISO 8601 format (YYYY-MM-DD)
  category: string | null;
  viewCount: number;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface VideoGallery {
  id: number;
  title: string;
  description: string | null;
  youtubeVideoId: string | null;
  videoUrl: string | null;
  thumbnailUrl: string | null;
  eventDate: string | null; // "2024.03.17" 형식
  category: string | null;
  viewCount: number;
  isPublished: boolean;
}

/**
 * VideoGallery 목록 조회 API 응답
 */
export interface VideoGalleriesResponse {
  content: VideoGalleryDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * VideoGallery 목록 조회 파라미터
 */
export interface GetVideoGalleriesParams {
  page?: number;
  size?: number;
  category?: string;
  year?: number;
}

/**
 * VideoGallery 등록/수정 Request
 */
export interface VideoGalleryRequest {
  title: string;
  description?: string;
  youtubeVideoId?: string;
  videoUrl?: string;
  thumbnailUrl?: string;
  eventDate?: string; // ISO 8601 (YYYY-MM-DD)
  category?: string;
  isPublished?: boolean;
}
