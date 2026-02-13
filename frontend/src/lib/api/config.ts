/**
 * API Base URL Configuration
 * Runtime에서 매번 평가하여 SSR/CSR 환경 정확히 감지
 *
 * Server-side (SSR): 컨테이너 네트워크 사용 (API_URL)
 * Client-side: 브라우저 localhost 사용 (NEXT_PUBLIC_API_URL)
 */

/**
 * API Base URL 가져오기
 * Runtime 체크 - 매 호출마다 평가
 *
 * @returns API Base URL
 */
export function getApiBaseUrl(): string {
  // Runtime 체크 - 매 호출마다 평가
  const isServer = typeof window === 'undefined';

  if (isServer) {
    // Server-side (SSR): 컨테이너 네트워크
    return process.env.API_URL || 'http://backend:8081/api';
  } else {
    // Client-side (브라우저): localhost
    return process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api';
  }
}

/**
 * API Error Class
 */
export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public data?: any
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

/**
 * Fetch wrapper with error handling
 */
export async function fetcher<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new ApiError(
      errorData.message || '요청 처리 중 오류가 발생했습니다.',
      response.status,
      errorData
    );
  }

  return response.json();
}

/**
 * Query parameters를 URL string으로 변환
 */
export function buildQueryString(params: Record<string, any>): string {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      query.append(key, String(value));
    }
  });
  const queryString = query.toString();
  return queryString ? `?${queryString}` : '';
}
