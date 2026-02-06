import Link from 'next/link';

const HISTORY = [
  {
    year: '2020년 - 현재',
    events: [
      '코로나19 대응 온라인 예배 시스템 구축',
      '하이브리드 예배 정착 및 디지털 사역 강화',
      '성도 수 2,000명 돌파',
    ],
  },
  {
    year: '2015년',
    events: [
      '제7대 김성복 목사 담임목사 부임',
      '교회 성장 및 부흥 운동 전개',
      '35개 부서 조직 완성',
    ],
  },
  {
    year: '2010년',
    events: [
      '새 예배당 건축 및 헌당',
      '3층 규모 1,500석 예배당',
      '교육관 및 주차장 완공',
    ],
  },
  {
    year: '2000년',
    events: [
      '해외 선교 본격 시작',
      '15개국 40여 선교지 지원',
      '선교사 파송 및 후원',
    ],
  },
  {
    year: '1995년',
    events: [
      '청년부 및 다음세대 사역 강화',
      '주일학교 8개 부서 확립',
      '성도 수 1,000명 돌파',
    ],
  },
  {
    year: '1985년',
    events: [
      '성복교회 창립',
      '초대 담임목사 취임',
      '30여 명의 성도와 함께 첫 예배',
    ],
  },
] as const;

export function HistorySection() {
  return (
    <section className="mt-16">
      <article className="bg-white dark:bg-gray-800 rounded-2xl p-8 md:p-12 shadow-md border border-gray-200 dark:border-gray-700">
        <h2 className="text-3xl font-bold text-primary-600 dark:text-primary-400 mb-8 pb-4 border-b-2 border-gray-200 dark:border-gray-700">
          교회 연혁
        </h2>

        <div className="relative pl-8 border-l-4 border-primary-500">
          {HISTORY.map((item, index) => (
            <div key={index} className="mb-10 relative">
              {/* Timeline Dot */}
              <div className="absolute -left-[2.875rem] top-2 w-6 h-6 rounded-full bg-primary-500 border-4 border-white dark:border-gray-800 shadow-lg" />

              {/* Content */}
              <div>
                <h3 className="text-xl font-bold text-primary-600 dark:text-primary-400 mb-3">
                  {item.year}
                </h3>
                <ul className="space-y-2 text-gray-700 dark:text-gray-300 leading-relaxed">
                  {item.events.map((event, eventIndex) => (
                    <li key={eventIndex}>{event}</li>
                  ))}
                </ul>
              </div>
            </div>
          ))}
        </div>

        {/* 섬기는 이들 더 알아보기 버튼 */}
        <div className="mt-12 text-center border-t border-gray-200 dark:border-gray-700 pt-8">
          <p className="text-gray-700 dark:text-gray-300 mb-6">
            성복교회를 섬기는 부목사 및 전도사님들을 소개합니다
          </p>
          <Link
            href="/about/staff"
            className="inline-block px-8 py-4 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors cursor-pointer"
          >
            섬기는 이들 더 알아보기 →
          </Link>
        </div>
      </article>
    </section>
  );
}
