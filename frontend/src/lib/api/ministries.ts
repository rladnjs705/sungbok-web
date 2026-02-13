/**
 * Ministry API Client (fetch 기반)
 * Backend API와 통신하여 부서 데이터를 가져옴
 */

import type {
  MinistryDTO,
  Ministry,
  MinistriesResponse,
  GetMinistriesParams,
} from '@/types/ministry';
import { transformMinistry, transformMinistries } from '@/lib/utils/transform';
import { ApiError, fetcher, buildQueryString, getApiBaseUrl } from './config';



/**
 * 부서 목록 조회
 * @param params - 조회 파라미터
 * @returns Frontend 표시용 Ministry 배열
 *
 * @example
 * // 기본 조회
 * const ministries = await getMinistries();
 *
 * // 필터링 조회
 * const activeMinistries = await getMinistries({ active: true, size: 10 });
 */
export async function getMinistries(params?: GetMinistriesParams): Promise<Ministry[]> {
  const queryString = params ? buildQueryString(params) : '';
  const url = `${getApiBaseUrl()}/ministries${queryString}`;

  // Backend returns array directly (not paginated)
  const dtos = await fetcher<MinistryDTO[]>(url);
  return transformMinistries(dtos);
}

/**
 * 특정 부서 조회 (ID)
 * @param id - 부서 ID
 * @returns Frontend 표시용 Ministry
 *
 * @example
 * const ministry = await getMinistryById(1);
 */
export async function getMinistryById(id: number): Promise<Ministry> {
  const url = `${getApiBaseUrl()}/ministries/${id}`;
  const dto = await fetcher<MinistryDTO>(url);
  return transformMinistry(dto);
}

/**
 * 특정 부서 조회 (Slug)
 * @param slug - 부서 slug (URL 라우팅용)
 * @returns Frontend 표시용 Ministry
 *
 * @example
 * const ministry = await getMinistryBySlug('youth');
 */
export async function getMinistryBySlug(slug: string): Promise<Ministry> {
  const url = `${getApiBaseUrl()}/ministries/slug/${slug}`;
  const dto = await fetcher<MinistryDTO>(url);
  return transformMinistry(dto);
}

/**
 * 활성화된 부서 목록 조회
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Ministry 배열
 *
 * @example
 * const active = await getActiveMinistries(10);
 */
export async function getActiveMinistries(limit = 20): Promise<Ministry[]> {
  return getMinistries({ active: true, size: limit });
}

/**
 * 카테고리별 부서 목록 조회
 * @param category - 부서 카테고리 (영문, 예: "YOUTH")
 * @param limit - 조회 개수
 * @returns Frontend 표시용 Ministry 배열
 *
 * @example
 * const youthMinistries = await getMinistriesByCategory('YOUTH', 10);
 */
export async function getMinistriesByCategory(
  category: string,
  limit = 20
): Promise<Ministry[]> {
  return getMinistries({ category, size: limit });
}

/**
 * 부서 검색 (백엔드 검색)
 * @param query - 검색어
 * @returns Frontend 표시용 Ministry 배열
 *
 * @example
 * const results = await searchMinistries('청년');
 */
export async function searchMinistries(query: string): Promise<Ministry[]> {
  if (!query.trim()) {
    return [];
  }

  // 🔥 FIX: 백엔드 검색 엔드포인트 사용
  const url = `${getApiBaseUrl()}/ministries/search?keyword=${encodeURIComponent(query)}`;
  const dtos = await fetcher<MinistryDTO[]>(url);
  return transformMinistries(dtos);
}
