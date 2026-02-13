/**
 * Format Utilities
 * Backend 데이터를 Frontend 표시 형식으로 변환
 */

/**
 * 초를 MM:SS 형식으로 변환
 * @param seconds - 초 단위 시간 (예: 2732)
 * @returns "MM:SS" 형식 문자열 (예: "45:32")
 *
 * @example
 * formatDuration(2732) // "45:32"
 * formatDuration(59)   // "00:59"
 * formatDuration(3600) // "60:00"
 */
export function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}

/**
 * ISO 8601 날짜를 한국 형식으로 변환
 * @param isoDate - ISO 8601 형식 날짜 (예: "2024-03-17")
 * @returns 점(.) 구분자 형식 (예: "2024.03.17")
 *
 * @example
 * formatDate("2024-03-17") // "2024.03.17"
 * formatDate("2023-12-25") // "2023.12.25"
 */
export function formatDate(isoDate: string): string {
  return isoDate.replace(/-/g, '.');
}

/**
 * ISO 8601 날짜를 한국어 형식으로 변환
 * @param isoDate - ISO 8601 형식 날짜
 * @returns "YYYY년 M월 D일" 형식
 *
 * @example
 * formatDateKorean("2024-03-17") // "2024년 3월 17일"
 */
export function formatDateKorean(isoDate: string): string {
  const date = new Date(isoDate);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}년 ${month}월 ${day}일`;
}

/**
 * 조회수를 한국어 형식으로 변환
 * @param count - 조회수
 * @returns "N회" 또는 "N.Nk회" 형식
 *
 * @example
 * formatViewCount(1234)   // "1,234회"
 * formatViewCount(12345)  // "12.3k회"
 * formatViewCount(1234567) // "1.2M회"
 */
export function formatViewCount(count: number): string {
  if (count >= 1000000) {
    return `${(count / 1000000).toFixed(1)}M회`;
  }
  if (count >= 1000) {
    return `${(count / 1000).toFixed(1)}k회`;
  }
  return `${count.toLocaleString('ko-KR')}회`;
}
