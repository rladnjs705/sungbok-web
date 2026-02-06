import { SermonCard } from './SermonCard';
import { Suspense } from 'react';
import { Skeleton } from '@/components/ui/skeleton';

// ✅ Server-Side Performance: Type definition
interface Sermon {
  id: number;
  title: string;
  description: string;
  date: string;
  thumbnail: string;
  viewCount: number;
  likeCount?: number;
  preacher?: string;
}

// ✅ Eliminating Waterfalls: Parallel data fetching
// This is a Server Component by default
async function getSermons(): Promise<Sermon[]> {
  // In production, this would be:
  // const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/sermons`);
  // return res.json();

  // Mock data for demonstration
  return [
    {
      id: 1,
      title: '하나님의 은혜와 사랑',
      description: '우리가 받은 하나님의 무한한 은혜와 사랑에 대한 말씀',
      date: '2026-02-02',
      thumbnail: '/images/sermons/sermon-1.jpg',
      viewCount: 1234,
      likeCount: 89,
      preacher: '홍길동 목사',
    },
    {
      id: 2,
      title: '믿음의 여정',
      description: '믿음으로 살아가는 그리스도인의 삶',
      date: '2026-01-26',
      thumbnail: '/images/sermons/sermon-2.jpg',
      viewCount: 2156,
      likeCount: 142,
      preacher: '홍길동 목사',
    },
    {
      id: 3,
      title: '소망 가운데',
      description: '어려운 상황 속에서도 잃지 않는 소망',
      date: '2026-01-19',
      thumbnail: '/images/sermons/sermon-3.jpg',
      viewCount: 1876,
      likeCount: 95,
      preacher: '홍길동 목사',
    },
  ];
}

// ✅ Rendering Performance: Loading skeleton
function SermonCardSkeleton() {
  return (
    <div className="overflow-hidden rounded-2xl shadow-lg">
      <Skeleton className="aspect-video w-full" />
      <div className="p-6">
        <Skeleton className="mb-2 h-4 w-32" />
        <Skeleton className="mb-2 h-6 w-full" />
        <Skeleton className="mb-4 h-4 w-full" />
        <div className="flex gap-4">
          <Skeleton className="h-4 w-16" />
          <Skeleton className="h-4 w-16" />
        </div>
      </div>
    </div>
  );
}

// ✅ Server Component: Default export
export async function SermonList() {
  // ✅ Eliminating Waterfalls: Fetch data in parallel (if multiple sources)
  const sermons = await getSermons();

  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="mb-12 text-center">
          <h2 className="mb-4 text-4xl font-bold text-gray-900">최신 설교</h2>
          <p className="text-lg text-gray-600">
            하나님의 말씀으로 은혜받으세요
          </p>
        </div>

        {/* Sermon Grid */}
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          {sermons.map((sermon) => (
            <Suspense key={sermon.id} fallback={<SermonCardSkeleton />}>
              <SermonCard sermon={sermon} />
            </Suspense>
          ))}
        </div>

        {/* Load More Button (optional) */}
        <div className="mt-12 text-center">
          <button className="rounded-lg bg-blue-600 px-8 py-3 font-semibold text-white transition-colors hover:bg-blue-700">
            더 보기
          </button>
        </div>
      </div>
    </section>
  );
}
