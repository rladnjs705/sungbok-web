// ISR: Revalidate every 2 hours
export const revalidate = 7200;

import { PageHero } from '@/components/layout/PageHero';
import { MissionSection } from '@/components/mission/MissionSection';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';

export const metadata = {
  title: '선교·사역 - 성복교회',
  description: '성복교회의 국내외 선교 사역을 소개합니다',
};

export default function MissionPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="선교·사역"
          subtitle="복음으로 세상을 변화시키는 교회"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          <MissionSection />
        </div>
      </main>

      <Footer />
    </>
  );
}
