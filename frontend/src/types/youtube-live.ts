/**
 * YouTube Live 관련 타입 정의
 */

export interface YouTubeLive {
  id: number;
  youtubeVideoId: string;
  title: string;
  worshipName: string | null;
  status: LiveStatus;
  viewerCount: number;
}

export type LiveStatus = 'LIVE' | 'UPCOMING' | 'COMPLETED';
