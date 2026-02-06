import Image from 'next/image';
import Link from 'next/link';
import { PlayCircle } from 'lucide-react';

const SERMONS = [
  {
    id: 1,
    date: '2024.03.17',
    title: '부활의 증인이 되라',
    pastor: '이요셉 담임목사',
    verse: '사도행전 1:8',
    videoId: 'NpjUJd1EoJI',
    category: '주일예배',
    duration: '45:23',
  },
  {
    id: 2,
    date: '2024.03.13',
    title: '주님의 십자가를 바라보라',
    pastor: '이요셉 담임목사',
    verse: '누가복음 23:33-34',
    videoId: 'dQw4w9WgXcQ',
    category: '주일예배',
    duration: '42:15',
  },
  {
    id: 3,
    date: '2024.03.10',
    title: '하나님의 사랑',
    pastor: '이요셉 담임목사',
    verse: '요한복음 3:16',
    videoId: '9bZkp7q19f0',
    category: '수요예배',
    duration: '38:47',
  },
  {
    id: 4,
    date: '2024.03.06',
    title: '선한 목자',
    pastor: '이요셉 담임목사',
    verse: '요한복음 10:11',
    videoId: 'kJQP7kiw5Fk',
    category: '주일예배',
    duration: '41:30',
  },
] as const;

export function RecentSermons() {
  return (
    <section className="mt-20">
      <h2 className="text-3xl font-bold text-center text-gray-900 dark:text-white mb-12">
        최근 설교 말씀
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {SERMONS.map((sermon) => (
          <Link
            key={sermon.id}
            href={`/sermons?selected=${sermon.id}`}
            className="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-xl transition-shadow cursor-pointer"
          >
            {/* Thumbnail with Play button */}
            <div className="relative aspect-video overflow-hidden">
              <Image
                src={`https://img.youtube.com/vi/${sermon.videoId}/hqdefault.jpg`}
                alt={sermon.title}
                fill
                className="object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div className="absolute inset-0 bg-black/20 group-hover:bg-black/30 transition-colors flex items-center justify-center">
                <div className="w-16 h-16 bg-white/90 rounded-full flex items-center justify-center group-hover:scale-110 transition-transform">
                  <PlayCircle className="w-8 h-8 text-primary-600" />
                </div>
              </div>
              {/* Duration badge */}
              {sermon.duration && (
                <div className="absolute bottom-2 right-2 bg-black/80 text-white px-2 py-1 rounded text-xs font-semibold">
                  {sermon.duration}
                </div>
              )}
            </div>

            {/* Card content */}
            <div className="p-4">
              {/* Category + Date */}
              <div className="flex items-center gap-2 mb-2">
                <span className="px-2 py-1 bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300 rounded text-xs font-semibold">
                  {sermon.category}
                </span>
                <span className="text-gray-500 dark:text-gray-400 text-xs">{sermon.date}</span>
              </div>

              {/* Title */}
              <h4 className="font-bold text-lg mb-2 line-clamp-2 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors dark:text-white">
                {sermon.title}
              </h4>

              {/* Verse + Pastor */}
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{sermon.verse}</p>
              <p className="text-sm text-gray-700 dark:text-gray-300 font-medium">{sermon.pastor}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}
