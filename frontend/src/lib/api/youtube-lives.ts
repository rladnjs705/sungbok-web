/**
 * YouTube Live API Client
 * 라이브 스트리밍 정보를 가져옴
 */

import { fetcher, getApiBaseUrl } from './config';

export interface YouTubeLive {
  id: number;
  youtubeVideoId: string;
  title: string;
  worshipName: string | null;
  status: 'LIVE' | 'UPCOMING' | 'COMPLETED';
  viewerCount: number;
}

/**
 * 현재 진행 중인 라이브 스트림 조회
 * @returns 현재 라이브 방송 목록
 *
 * @example
 * const liveStreams = await getCurrentLiveStreams();
 */
export async function getCurrentLiveStreams(): Promise<YouTubeLive[]> {
  const url = `${getApiBaseUrl()}/youtube-lives/current`;

  try {
    return await fetcher<YouTubeLive[]>(url);
  } catch (error) {
    console.error('Failed to fetch current live streams:', error);
    return [];
  }
}
