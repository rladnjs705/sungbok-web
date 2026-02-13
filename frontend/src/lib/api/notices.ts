/**
 * Notice API Client (fetch 기반)
 * Backend API와 통신하여 공지사항 데이터를 가져옴
 */

import type {
  NoticeDTO,
  Notice,
  NoticesResponse,
  GetNoticesParams,
} from '@/types/notice';
import { transformNotice, transformNotices } from '@/lib/utils/transform';
import { NOTICE_CATEGORY_REVERSE_MAP } from '@/lib/constants/enums';
import { ApiError, fetcher, buildQueryString, getApiBaseUrl } from './config';



/**
 * 공지사항 목록 조회
 * @param params - 조회 파라미터
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * // 기본 조회
 * const notices = await getNotices();
 *
 * // 필터링 조회
 * const pinnedNotices = await getNotices({ pinned: true, size: 10 });
 */
export async function getNotices(params?: GetNoticesParams): Promise<Notice[]> {
  const queryString = params ? buildQueryString(params) : '';
  const url = `${getApiBaseUrl()}/notices${queryString}`;

  const response = await fetcher<NoticesResponse>(url);
  return transformNotices(response.content);
}

/**
 * 특정 공지사항 조회
 * @param id - 공지사항 ID
 * @returns Frontend 표시용 Notice
 *
 * @example
 * const notice = await getNoticeById(1);
 */
export async function getNoticeById(id: number): Promise<Notice> {
  const url = `${getApiBaseUrl()}/notices/${id}`;
  const dto = await fetcher<NoticeDTO>(url);
  return transformNotice(dto);
}

/**
 * 고정된 공지사항 목록 조회
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * const pinned = await getPinnedNotices(3);
 */
export async function getPinnedNotices(limit = 10): Promise<Notice[]> {
  return getNotices({ pinned: true, size: limit });
}

/**
 * 최신 공지사항 목록 조회
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * const recent = await getRecentNotices(5);
 */
export async function getRecentNotices(limit = 10): Promise<Notice[]> {
  return getNotices({ size: limit, page: 0 });
}

/**
 * 카테고리별 공지사항 목록 조회 (백엔드 필터링)
 * @param category - 공지사항 카테고리 (한국어, 예: "새소식")
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * const newsNotices = await getNoticesByCategory('새소식', 20);
 */
export async function getNoticesByCategory(
  category: string,
  limit = 20
): Promise<Notice[]> {
  // "전체" 카테고리는 필터링하지 않음
  if (category === '전체') {
    return getNotices({ size: limit });
  }

  // 🔥 FIX: 백엔드 카테고리 엔드포인트 사용
  const categoryEnum = NOTICE_CATEGORY_REVERSE_MAP[category];
  if (!categoryEnum) {
    console.warn(`Unknown category: ${category}, falling back to all notices`);
    return getNotices({ size: limit });
  }

  const url = `${getApiBaseUrl()}/notices/category/${categoryEnum}?page=0&size=${limit}`;
  const response = await fetcher<NoticesResponse>(url);
  return transformNotices(response.content);
}

/**
 * Featured 공지사항 조회 (홈 화면용)
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * const featured = await getFeaturedNotices(3);
 */
export async function getFeaturedNotices(limit = 3): Promise<Notice[]> {
  return getPinnedNotices(limit);
}

/**
 * 공지사항 검색 (백엔드 검색)
 * @param query - 검색어
 * @returns Frontend 표시용 Notice 배열
 *
 * @example
 * const results = await searchNotices('부활절');
 */
export async function searchNotices(query: string): Promise<Notice[]> {
  if (!query.trim()) {
    return [];
  }

  // 🔥 FIX: 백엔드 검색 엔드포인트 사용
  const url = `${getApiBaseUrl()}/notices/search?keyword=${encodeURIComponent(query)}&size=20`;
  const response = await fetcher<NoticesResponse>(url);
  return transformNotices(response.content);
}
