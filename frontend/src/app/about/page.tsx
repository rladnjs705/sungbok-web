// ISR: Revalidate every 24 hours (교회 소개는 변경이 거의 없음)
export const revalidate = 86400;

import { PageHero } from '@/components/layout/PageHero';
import { GreetingSection } from '@/components/about/GreetingSection';
import { HistorySection } from '@/components/about/HistorySection';
import { LocationSection } from '@/components/about/LocationSection';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';

export const metadata = {
  title: '교회소개 - 성복교회',
  description: '하나님의 사랑을 전하는 성복교회를 소개합니다',
};

export default function AboutPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="교회소개"
          subtitle="하나님의 사랑을 전하는 성복교회를 소개합니다"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          <GreetingSection />
          <HistorySection />
          <LocationSection />
        </div>
      </main>

      <Footer />
    </>
  );
}
