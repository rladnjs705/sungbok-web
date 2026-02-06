import Link from 'next/link';

const QUICK_ACTIONS = [
  {
    id: 1,
    icon: '📍',
    title: '찾아오시는 길',
    description: (
      <>
        서울특별시 강남구 테헤란로 123
        <br />
        지하철 2호선 강남역 3번 출구
        <br />
        도보 5분 거리
      </>
    ),
    buttonText: '지도 보기',
    href: '/about#directions',
    bgClass: 'bg-gradient-to-br from-green-400 to-lime-500 dark:from-green-800 dark:to-lime-800',
  },
  {
    id: 2,
    icon: '💝',
    title: '온라인 헌금',
    description: (
      <>
        신한은행 110-123-456789
        <br />
        예금주: 성복교회
        <br />
        계좌이체 및 온라인 헌금
      </>
    ),
    buttonText: '자세히 보기',
    href: '/about',
    bgClass: 'bg-gradient-to-br from-orange-300 to-orange-500 dark:from-orange-800 dark:to-orange-900',
  },
  {
    id: 3,
    icon: '✨',
    title: '새가족 등록',
    description: (
      <>
        성복교회에 오신 것을 환영합니다
        <br />
        새가족 등록 및 환영 선물
        <br />
        담임목사 면담 신청
      </>
    ),
    buttonText: '자세히 보기',
    href: '/news/3',
    bgClass: 'bg-gradient-to-br from-blue-400 to-blue-600 dark:from-blue-800 dark:to-blue-900',
  },
] as const;

export function QuickActions() {
  return (
    <section className="grid grid-cols-1 md:grid-cols-3 mt-16 md:mt-20">
      {QUICK_ACTIONS.map((action) => (
        <div
          key={action.id}
          className={`py-16 px-8 text-center text-white ${action.bgClass}`}
        >
          {/* Icon */}
          <div className="w-20 h-20 mx-auto mb-6 flex items-center justify-center bg-white/20 rounded-full text-4xl">
            {action.icon}
          </div>

          {/* Title */}
          <h3 className="text-3xl font-bold mb-6">{action.title}</h3>

          {/* Description */}
          <p className="text-base leading-relaxed mb-8 opacity-95">
            {action.description}
          </p>

          {/* CTA */}
          <Link
            href={action.href}
            className="inline-block px-10 py-3 bg-white/20 border-2 border-white/50 rounded-full font-semibold hover:bg-white/30 transition-colors cursor-pointer dark:bg-white/30 dark:border-white/70 dark:hover:bg-white/40"
          >
            {action.buttonText}
          </Link>
        </div>
      ))}
    </section>
  );
}
