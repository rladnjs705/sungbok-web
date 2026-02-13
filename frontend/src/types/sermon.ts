/**
 * Sermon Domain Types
 * Backend DTO와 Frontend 모델 간 변환을 위한 타입 정의
 */

/**
 * Backend API 응답 DTO
 * - duration: 초 단위 (Integer)
 * - sermonDate: ISO 8601 형식 ("2024-03-17")
 * - youtubeVideoId: YouTube 동영상 ID
 * - bibleVerse: 성경 구절
 */
export interface SermonDTO {
  id: number;
  title: string;
  bibleVerse: string | null;
  preacher: string;
  sermonDate: string; // ISO 8601 format
  youtubeVideoId: string | null;
  duration: number | null; // 초 단위
  viewCount: number;
  isFeatured: boolean;
  worshipName?: string; // 예배 유형 (한글 제목)
  worshipType?: string; // 예배 유형 (Enum name)
  createdAt: string;
  updatedAt: string;
}

/**
 * Frontend 표시용 모델
 * - duration: "MM:SS" 형식
 * - date: "2024.03.17" 형식 (점 구분자)
 * - videoId: youtubeVideoId 단축명
 * - verse: bibleVerse 단축명
 */
export interface Sermon {
  id: number;
  title: string;
  verse: string | null; // bibleVerse → verse
  pastor: string; // preacher → pastor
  date: string; // "2024.03.17" 형식
  videoId: string | null; // youtubeVideoId → videoId
  duration: string | null; // "45:32" 형식
  viewCount: number;
  isFeatured: boolean;
  category?: string; // worshipName의 한국어 표시
}

/**
 * Sermon 목록 조회 API 응답
 * Spring Data Page 응답 구조
 */
export interface SermonsResponse {
  content: SermonDTO[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/**
 * Sermon 목록 조회 파라미터
 */
export interface GetSermonsParams {
  page?: number;
  size?: number;
  featured?: boolean;
  worshipName?: string;
  startDate?: string;
  endDate?: string;
}
