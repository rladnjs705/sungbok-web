/**
 * Notice Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 */
export interface NoticeDTO {
  id: number;
  category: string; // "NEWS" | "EVENT" | "ANNOUNCEMENT" | "BULLETIN"
  title: string;
  content: string;
  author: string;
  isPinned: boolean;
  viewCount: number;
  publishedAt: string; // ISO 8601 format
  createdAt: string;
  updatedAt: string;
  excerpt: string | null; // 카드에 표시할 짧은 요약
  imageUrl: string | null; // 대표 이미지 URL
}

/**
 * Frontend 표시용 모델
 */
export interface Notice {
  id: number;
  category: string; // 한국어 카테고리 ("새소식", "행사", "공지사항", "주보")
  title: string;
  content: string;
  author: string;
  isPinned: boolean;
  viewCount: number;
  date: string; // "2024.03.17" 형식
  createdAt: string;
  updatedAt: string;
  excerpt: string | null; // 짧은 요약
  image: string | null; // imageUrl → image
}

/**
 * Notice 목록 조회 API 응답
 * Spring Data Page 응답 구조
 */
export interface NoticesResponse {
  content: NoticeDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/**
 * Notice 목록 조회 파라미터
 */
export interface GetNoticesParams {
  page?: number;
  size?: number;
  category?: string;
  pinned?: boolean;
  startDate?: string;
  endDate?: string;
}
