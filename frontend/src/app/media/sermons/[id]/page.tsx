import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { notFound } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { PlayCircle } from 'lucide-react';

// ISR: Revalidate every 5 minutes
export const revalidate = 300;

const SERMONS = [
  {
    id: 1,
    date: '2024.03.17',
    title: '부활의 증인이 되라',
    pastor: '이요셉 담임목사',
    verse: '사도행전 1:8',
    videoId: 'NpjUJd1EoJI',
    category: '주일예배',
    description: `부활하신 예수 그리스도의 증인으로 살아가는 삶에 대한 말씀입니다.

우리는 예수님의 부활을 직접 본 증인은 아니지만, 성령의 능력으로 부활의 증인이 될 수 있습니다.
이 시대에 그리스도인으로 산다는 것은 무엇을 의미하는지 함께 생각해봅시다.`,
    duration: '45:23',
  },
  {
    id: 2,
    date: '2024.03.10',
    title: '믿음으로 사는 삶',
    pastor: '이요셉 담임목사',
    verse: '히브리서 11:1-6',
    videoId: 'dQw4w9WgXcQ',
    category: '주일예배',
    description: `믿음이란 무엇이며, 어떻게 믿음으로 살아갈 수 있는지에 대한 말씀입니다.

히브리서 11장에 나오는 믿음의 선진들을 통해 진정한 믿음의 모습을 발견하고,
우리의 삶에 적용하는 방법을 배웁니다.`,
    duration: '42:15',
  },
  {
    id: 3,
    date: '2024.03.03',
    title: '사랑의 실천',
    pastor: '이요셉 담임목사',
    verse: '요한일서 4:7-12',
    videoId: '9bZkp7q19f0',
    category: '수요예배',
    description: `하나님의 사랑을 받은 우리가 어떻게 그 사랑을 실천할 수 있는지에 대한 말씀입니다.

사랑은 단순한 감정이 아니라 행동으로 나타나야 합니다.
일상에서 하나님의 사랑을 나누는 구체적인 방법들을 함께 나눕니다.`,
    duration: '38:47',
  },
  {
    id: 4,
    date: '2024.02.25',
    title: '기도의 능력',
    pastor: '이요셉 담임목사',
    verse: '야고보서 5:13-18',
    videoId: 'kJQP7kiw5Fk',
    category: '주일예배',
    description: `기도의 능력과 효과적인 기도 생활에 대한 말씀입니다.

성경은 기도에 대해 무엇이라 말하고 있으며, 우리의 기도가 응답받기 위해 어떤 자세가 필요한지 배웁니다.`,
    duration: '41:30',
  },
] as const;

interface SermonDetailPageProps {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: SermonDetailPageProps) {
  const { id } = await params;
  const sermon = SERMONS.find((item) => item.id === parseInt(id));

  if (!sermon) {
    return {
      title: '설교를 찾을 수 없습니다 - 성복교회',
    };
  }

  return {
    title: `${sermon.title} - ${sermon.pastor} - 성복교회`,
    description: sermon.description,
  };
}

export default async function SermonDetailPage({
  params,
}: SermonDetailPageProps) {
  const { id } = await params;
  const sermon = SERMONS.find((item) => item.id === parseInt(id));

  if (!sermon) {
    notFound();
  }

  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="설교 말씀" subtitle="하나님의 말씀을 듣고 배우는 시간" />

        <div className="container mx-auto px-4 py-16 max-w-5xl">
          <article>
            {/* Video Player */}
            <div className="aspect-video bg-gray-900 rounded-2xl overflow-hidden mb-8">
              <iframe
                key={sermon.videoId}
                width="100%"
                height="100%"
                src={`https://www.youtube.com/embed/${sermon.videoId}`}
                title={sermon.title}
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                className="w-full h-full"
              ></iframe>
            </div>

            {/* Sermon Info */}
            <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-8 md:p-12">
              <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400 mb-6">
                <span className="px-3 py-1 bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300 rounded-full font-semibold">
                  {sermon.category}
                </span>
                <span>{sermon.date}</span>
                <span>{sermon.duration}</span>
              </div>

              <h1 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
                {sermon.title}
              </h1>

              <div className="flex items-center gap-6 text-gray-600 dark:text-gray-400 mb-8">
                <div>
                  <span className="text-sm text-gray-500 dark:text-gray-400">설교자</span>
                  <p className="font-semibold text-gray-900 dark:text-white">{sermon.pastor}</p>
                </div>
                <div>
                  <span className="text-sm text-gray-500 dark:text-gray-400">본문</span>
                  <p className="font-semibold text-gray-900 dark:text-white">{sermon.verse}</p>
                </div>
              </div>

              <div className="prose prose-lg max-w-none mb-8">
                {sermon.description.split('\n\n').map((paragraph, index) => (
                  <p key={index} className="mb-4 text-gray-700 dark:text-gray-300 leading-relaxed">
                    {paragraph}
                  </p>
                ))}
              </div>

              {/* Actions */}
              <div className="flex flex-wrap gap-4 pt-8 border-t border-gray-200 dark:border-gray-700">
                <Link
                  href="/worship"
                  className="px-6 py-3 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-300 font-semibold rounded-lg transition-colors cursor-pointer"
                >
                  설교 목록
                </Link>
                <button className="px-6 py-3 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors cursor-pointer">
                  공유하기
                </button>
                <button className="px-6 py-3 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300 font-semibold rounded-lg transition-colors cursor-pointer">
                  다운로드
                </button>
              </div>
            </div>
          </article>

          {/* Related Sermons */}
          <div className="mt-12">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
              최근 설교
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {SERMONS.filter((item) => item.id !== sermon.id)
                .slice(0, 3)
                .map((item) => (
                  <Link
                    key={item.id}
                    href={`/media/sermons/${item.id}`}
                    className="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                  >
                    <div className="relative aspect-video overflow-hidden">
                      <Image
                        src={`https://img.youtube.com/vi/${item.videoId}/hqdefault.jpg`}
                        alt={item.title}
                        fill
                        className="object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                      {/* Play button overlay */}
                      <div className="absolute inset-0 bg-black/20 group-hover:bg-black/30 transition-colors flex items-center justify-center">
                        <div className="w-16 h-16 bg-white/90 rounded-full flex items-center justify-center group-hover:scale-110 transition-transform">
                          <PlayCircle className="w-8 h-8 text-primary-600" />
                        </div>
                      </div>
                      {/* Duration badge */}
                      {item.duration && (
                        <div className="absolute bottom-2 right-2 bg-black/80 text-white px-2 py-1 rounded text-xs font-semibold">
                          {item.duration}
                        </div>
                      )}
                    </div>
                    <div className="p-4">
                      {/* Category + Date */}
                      <div className="flex items-center gap-2 mb-2">
                        <span className="px-2 py-1 bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300 rounded text-xs font-semibold">
                          {item.category}
                        </span>
                        <span className="text-gray-500 dark:text-gray-400 text-xs">{item.date}</span>
                      </div>
                      <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2 line-clamp-2 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors">
                        {item.title}
                      </h3>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{item.verse}</p>
                      <p className="text-sm text-gray-700 dark:text-gray-300 font-medium">{item.pastor}</p>
                    </div>
                  </Link>
                ))}
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
