/**
 * Transform Utilities
 * Backend DTO → Frontend Model 변환
 */

import type { SermonDTO, Sermon } from '@/types/sermon';
import type { NoticeDTO, Notice } from '@/types/notice';
import type { MinistryDTO, Ministry } from '@/types/ministry';
import { formatDuration, formatDate } from './format';
import { WORSHIP_TYPE_MAP, NOTICE_CATEGORY_MAP, MINISTRY_CATEGORY_MAP } from '../constants/enums';

/**
 * Sermon DTO → Frontend Model 변환
 * @param dto - Backend API 응답 DTO
 * @returns Frontend 표시용 모델
 *
 * @example
 * const dto = {
 *   id: 1,
 *   title: '부활의 증인이 되라',
 *   bibleVerse: '사도행전 1:8',
 *   preacher: '이요셉 담임목사',
 *   sermonDate: '2024-03-17',
 *   youtubeVideoId: 'NpjUJd1EoJI',
 *   duration: 2732,
 *   viewCount: 150,
 *   isFeatured: true,
 *   worshipName: 'SUNDAY_MORNING'
 * };
 * transformSermon(dto);
 * // {
 * //   id: 1,
 * //   title: '부활의 증인이 되라',
 * //   verse: '사도행전 1:8',
 * //   pastor: '이요셉 담임목사',
 * //   date: '2024.03.17',
 * //   videoId: 'NpjUJd1EoJI',
 * //   duration: '45:32',
 * //   viewCount: 150,
 * //   isFeatured: true,
 * //   category: '주일예배'
 * // }
 */
export function transformSermon(dto: SermonDTO): Sermon {
  return {
    id: dto.id,
    title: dto.title,
    verse: dto.bibleVerse,
    pastor: dto.preacher,
    date: formatDate(dto.sermonDate),
    videoId: dto.youtubeVideoId,
    duration: dto.duration ? formatDuration(dto.duration) : null,
    viewCount: dto.viewCount,
    isFeatured: dto.isFeatured,
    category: dto.worshipType ? WORSHIP_TYPE_MAP[dto.worshipType] : undefined,
  };
}

/**
 * Sermon DTO 배열 → Frontend Model 배열 변환
 * @param dtos - Backend API 응답 DTO 배열
 * @returns Frontend 표시용 모델 배열
 */
export function transformSermons(dtos: SermonDTO[]): Sermon[] {
  return dtos.map(transformSermon);
}

/**
 * Notice DTO → Frontend Model 변환
 * @param dto - Backend API 응답 DTO
 * @returns Frontend 표시용 모델
 *
 * @example
 * const dto = {
 *   id: 1,
 *   category: 'NEWS',
 *   title: '부활절 연합예배 안내',
 *   publishedAt: '2024-03-15T10:00:00',
 *   excerpt: '부활절을 맞아 모든 부서가 함께하는...',
 *   imageUrl: '/images/news01.jpg'
 * };
 * transformNotice(dto);
 * // {
 * //   id: 1,
 * //   category: '새소식',
 * //   title: '부활절 연합예배 안내',
 * //   date: '2024.03.15',
 * //   excerpt: '부활절을 맞아 모든 부서가 함께하는...',
 * //   image: '/images/news01.jpg'
 * // }
 */
export function transformNotice(dto: NoticeDTO): Notice {
  return {
    id: dto.id,
    category: NOTICE_CATEGORY_MAP[dto.category] || dto.category,
    title: dto.title,
    content: dto.content,
    author: dto.author,
    isPinned: dto.isPinned,
    viewCount: dto.viewCount,
    date: formatDate(dto.publishedAt.split('T')[0]), // ISO 8601 → "2024.03.17"
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
    excerpt: dto.excerpt,
    image: dto.imageUrl,
  };
}

/**
 * Notice DTO 배열 → Frontend Model 배열 변환
 * @param dtos - Backend API 응답 DTO 배열
 * @returns Frontend 표시용 모델 배열
 */
export function transformNotices(dtos: NoticeDTO[]): Notice[] {
  return dtos.map(transformNotice);
}

/**
 * Ministry DTO → Frontend Model 변환
 * @param dto - Backend API 응답 DTO
 * @returns Frontend 표시용 모델
 *
 * @example
 * const dto = {
 *   id: 1,
 *   name: '청년부',
 *   slug: 'youth',
 *   category: 'YOUTH',
 *   description: '청년들을 위한 부서',
 *   photoUrl: '/images/ministry-youth.jpg'
 * };
 * transformMinistry(dto);
 * // {
 * //   id: 1,
 * //   name: '청년부',
 * //   slug: 'youth',
 * //   category: '청소년부',
 * //   description: '청년들을 위한 부서',
 * //   photo: '/images/ministry-youth.jpg'
 * // }
 */
export function transformMinistry(dto: MinistryDTO): Ministry {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    category: MINISTRY_CATEGORY_MAP[dto.category] || dto.category,
    description: dto.description,
    targetAge: dto.targetAge,
    schedule: dto.schedule,
    location: dto.location,
    leader: dto.leader,
    contact: dto.contact,
    photo: dto.photoUrl,
    isActive: dto.isActive,
    displayOrder: dto.displayOrder,
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
  };
}

/**
 * Ministry DTO 배열 → Frontend Model 배열 변환
 * @param dtos - Backend API 응답 DTO 배열
 * @returns Frontend 표시용 모델 배열
 */
export function transformMinistries(dtos: MinistryDTO[]): Ministry[] {
  return dtos.map(transformMinistry);
}
