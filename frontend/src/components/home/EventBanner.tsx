import Link from 'next/link';

export function EventBanner() {
  return (
    <section
      className="relative py-20 my-16 md:my-20 text-center text-white overflow-hidden"
      style={{
        backgroundImage: 'url(/images/main01.jpg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }}
    >
      {/* Gradient Overlay */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-400/90 to-slate-600/90 dark:from-slate-800/90 dark:to-slate-900/90" />

      <div className="container mx-auto px-4 relative z-10">
        <div className="max-w-3xl mx-auto">
          {/* Badge */}
          <div className="inline-block px-6 py-2 mb-6 bg-white/20 border-2 border-white/50 rounded-full text-sm font-semibold">
            2024 부활절 특별예배
          </div>

          {/* Title */}
          <h2 className="text-4xl md:text-5xl lg:text-6xl font-bold leading-tight mb-6">
            부활의 주님을
            <br />
            경배합니다
          </h2>

          {/* Date & Location */}
          <p className="text-2xl md:text-3xl font-semibold mb-2">
            2024년 3월 31일 (주일) 오전 10:00
          </p>
          <p className="text-lg md:text-xl mb-8">예루살렘성전 [3F]</p>

          {/* Details */}
          <div className="flex flex-col md:flex-row gap-8 justify-center mb-8">
            <div className="flex flex-col gap-2">
              <span className="text-sm opacity-80">설교</span>
              <span className="text-lg font-semibold">이요셉 담임목사</span>
            </div>
            <div className="flex flex-col gap-2">
              <span className="text-sm opacity-80">특송</span>
              <span className="text-lg font-semibold">성복교회 찬양팀</span>
            </div>
          </div>

          {/* CTA */}
          <Link
            href="/news/easter-worship"
            className="inline-block px-12 py-4 bg-white text-primary-600 font-semibold text-lg rounded-full shadow-2xl hover:bg-white/90 transition-colors cursor-pointer dark:bg-gray-800 dark:text-white dark:hover:bg-gray-700"
          >
            자세히 보기
          </Link>
        </div>
      </div>
    </section>
  );
}
