/**
 * Enum Mapping Constants
 * Backend Enum → Frontend 한국어 표시 매핑
 */

/**
 * 예배 유형 매핑
 * Backend: worshipName (String) → Frontend: category (Korean)
 */
export const WORSHIP_TYPE_MAP: Record<string, string> = {
  SUNDAY: '주일예배',
  WEDNESDAY: '수요예배',
  DAWN: '새벽예배',
  SPECIAL: '특별예배',
} as const;

/**
 * 공지사항 카테고리 매핑
 * Backend: NoticeCategory (Enum) → Frontend: category (Korean)
 */
export const NOTICE_CATEGORY_MAP: Record<string, string> = {
  NEWS: '새소식',
  EVENT: '행사',
  ANNOUNCEMENT: '공지사항',
  BULLETIN: '주보',
} as const;

/**
 * 부서 카테고리 매핑
 * Backend: MinistryCategory (Enum) → Frontend: category (Korean)
 */
export const MINISTRY_CATEGORY_MAP: Record<string, string> = {
  SUNDAY_SCHOOL: '주일학교',
  YOUTH: '청년부',
  ADULT: '장년부',
  SENIOR: '노년부',
  NEWCOMER: '새가족부',
  BIBLE_STUDY: '성경공부',
  DISCIPLE_TRAINING: '제자훈련',
  WORSHIP: '찬양부',
  MEDIA: '미디어부',
} as const;

/**
 * 예배 유형 역매핑 (Frontend → Backend)
 * API 요청 시 사용
 */
export const WORSHIP_TYPE_REVERSE_MAP: Record<string, string> = Object.entries(
  WORSHIP_TYPE_MAP
).reduce((acc, [key, value]) => {
  acc[value] = key;
  return acc;
}, {} as Record<string, string>);

/**
 * 공지사항 카테고리 역매핑 (Frontend → Backend)
 * API 요청 시 사용
 */
export const NOTICE_CATEGORY_REVERSE_MAP: Record<string, string> = Object.entries(
  NOTICE_CATEGORY_MAP
).reduce((acc, [key, value]) => {
  acc[value] = key;
  return acc;
}, {} as Record<string, string>);

/**
 * 부서 카테고리 역매핑 (Frontend → Backend)
 * API 요청 시 사용
 */
export const MINISTRY_CATEGORY_REVERSE_MAP: Record<string, string> = Object.entries(
  MINISTRY_CATEGORY_MAP
).reduce((acc, [key, value]) => {
  acc[value] = key;
  return acc;
}, {} as Record<string, string>);
