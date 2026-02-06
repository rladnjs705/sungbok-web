import { Suspense } from 'react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { SearchResults } from './SearchResults';

export const metadata = {
  title: '검색 - 성복교회',
  description: '설교 및 교회소식 검색',
};

export default function SearchPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="검색"
          subtitle="설교와 교회소식을 검색하세요"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          <Suspense
            fallback={
              <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
              </div>
            }
          >
            <SearchResults />
          </Suspense>
        </div>
      </main>

      <Footer />
    </>
  );
}
