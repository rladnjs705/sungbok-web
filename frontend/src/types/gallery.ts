/**
 * Gallery Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface GalleryDTO {
  id: number;
  title: string;
  description: string | null;
  eventDate: string; // ISO 8601 format (YYYY-MM-DD)
  coverImageUrl: string | null;
  viewCount: number;
  isPublished: boolean;
  imageCount: number; // 포함된 이미지 수 (백엔드에서 계산)
  createdAt: string;
  updatedAt: string;
}

/**
 * Gallery 이미지 아이템
 */
export interface GalleryImageDTO {
  id: number;
  galleryId: number;
  imageUrl: string;
  thumbnailUrl: string | null;
  caption: string | null;
  displayOrder: number;
  createdAt: string;
}

/**
 * Frontend 표시용 모델
 */
export interface Gallery {
  id: number;
  title: string;
  description: string | null;
  eventDate: string; // "2024.03.17" 형식
  coverImage: string | null; // coverImageUrl -> coverImage
  viewCount: number;
  imageCount: number;
  isPublished: boolean;
}

/**
 * Gallery 상세 정보 (이미지 포함)
 */
export interface GalleryDetail extends Gallery {
  images: GalleryImage[];
}

/**
 * Gallery 이미지 (Frontend 모델)
 */
export interface GalleryImage {
  id: number;
  imageUrl: string;
  thumbnail: string | null;
  caption: string | null;
  displayOrder: number;
}

/**
 * Gallery 목록 조회 API 응답
 */
export interface GalleriesResponse {
  content: GalleryDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Gallery 목록 조회 파라미터
 */
export interface GetGalleriesParams {
  page?: number;
  size?: number;
  year?: number; // 특정 연도 필터
}

/**
 * Gallery 등록/수정 Request
 */
export interface GalleryRequest {
  title: string;
  description?: string;
  eventDate: string; // ISO 8601 (YYYY-MM-DD)
  coverImageUrl?: string;
  isPublished?: boolean;
}

/**
 * Gallery 이미지 등록 Request
 */
export interface GalleryImageRequest {
  imageUrl: string;
  thumbnailUrl?: string;
  caption?: string;
  displayOrder?: number;
}
