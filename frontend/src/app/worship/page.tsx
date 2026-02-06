// ISR: Revalidate every 2 hours
export const revalidate = 7200;

import { PageHero } from '@/components/layout/PageHero';
import { WorshipSchedule } from '@/components/worship/WorshipSchedule';
import { RecentSermons } from '@/components/worship/RecentSermons';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';

export const metadata = {
  title: '예배안내 - 성복교회',
  description: '성복교회의 예배 시간과 설교 말씀을 확인하세요',
};

export default function WorshipPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="예배안내"
          subtitle="하나님께 영광, 성도에게 은혜가 되는 예배"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          <WorshipSchedule />
          <RecentSermons />
        </div>
      </main>

      <Footer />
    </>
  );
}
