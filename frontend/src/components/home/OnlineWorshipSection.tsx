import Link from 'next/link';

const WORSHIP_SCHEDULES = [
  {
    id: 1,
    icon: '🌅',
    title: '새벽기도회',
    time: '매일 오전 5시 30분',
    href: 'https://www.youtube.com/watch?v=NpjUJd1EoJI',
  },
  {
    id: 2,
    icon: '⛪',
    title: '주일예배',
    time: '매주 일요일 오전 11시',
    href: 'https://www.youtube.com/watch?v=NpjUJd1EoJI',
  },
  {
    id: 3,
    icon: '🕯️',
    title: '수요예배',
    time: '매주 수요일 오후 7시 30분',
    href: 'https://www.youtube.com/watch?v=NpjUJd1EoJI',
  },
] as const;

export function OnlineWorshipSection() {
  return (
    <section className="py-20 bg-gray-50">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="text-center mb-12">
          <p className="text-sm text-primary-500 font-semibold tracking-wider mb-2">
            Online Worship
          </p>
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900">
            온라인 예배
          </h2>
        </div>

        {/* YouTube Player */}
        <div className="max-w-5xl mx-auto mb-12">
          <div className="relative aspect-video rounded-xl overflow-hidden shadow-2xl">
            <iframe
              src="https://www.youtube.com/embed/NpjUJd1EoJI?si=6L8PFNUxr-Q"
              className="absolute inset-0 w-full h-full"
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowFullScreen
            />
          </div>
        </div>

        {/* Schedule Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl mx-auto">
          {WORSHIP_SCHEDULES.map((schedule) => (
            <article
              key={schedule.id}
              className="bg-white rounded-2xl p-8 text-center shadow-md hover:shadow-xl transition-shadow"
            >
              <div className="text-5xl mb-4">{schedule.icon}</div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                {schedule.title}
              </h3>
              <p className="text-gray-600 mb-6">{schedule.time}</p>
              <a
                href={schedule.href}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-block px-6 py-3 bg-primary-500 text-white font-semibold rounded-full hover:bg-primary-600 transition-colors cursor-pointer"
              >
                실시간 시청
              </a>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}
