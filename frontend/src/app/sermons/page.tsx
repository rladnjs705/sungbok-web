import { dehydrate, HydrationBoundary } from '@tanstack/react-query';
import { getQueryClient } from '@/lib/query-client';
import { getSermons } from '@/lib/api/sermons';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { SermonsClient } from './SermonsClient';

export const metadata = {
  title: '예배 - 성복교회',
  description: '성복교회 설교 영상 및 예배 다시보기',
};

// ISR: 1시간마다 재생성
export const revalidate = 3600;

export default async function SermonsPage() {
  const queryClient = getQueryClient();

  // Server-side prefetch
  await queryClient.prefetchQuery({
    queryKey: ['sermons'],
    queryFn: getSermons,
  });

  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="예배"
          subtitle="말씀으로 은혜받는 시간"
        />

        {/* Hydrate prefetched data to client */}
        <HydrationBoundary state={dehydrate(queryClient)}>
          <SermonsClient />
        </HydrationBoundary>
      </main>

      <Footer />
    </>
  );
}
