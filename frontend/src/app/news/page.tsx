import { dehydrate, HydrationBoundary } from '@tanstack/react-query';
import { getQueryClient } from '@/lib/query-client';
import { getNews } from '@/lib/api/news';
import { PageHero } from '@/components/layout/PageHero';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { NewsClient } from './NewsClient';

export const metadata = {
  title: '교회소식 - 성복교회',
  description: '성복교회의 새로운 소식과 행사 정보를 확인하세요',
};

// ISR: Revalidate every 10 minutes
export const revalidate = 600;

export default async function NewsPage() {
  const queryClient = getQueryClient();

  // Server-side prefetch
  await queryClient.prefetchQuery({
    queryKey: ['news'],
    queryFn: getNews,
  });

  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="교회소식"
          subtitle="성복교회의 새로운 소식을 전합니다"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Hydrate prefetched data to client */}
          <HydrationBoundary state={dehydrate(queryClient)}>
            <NewsClient />
          </HydrationBoundary>
        </div>
      </main>

      <Footer />
    </>
  );
}
