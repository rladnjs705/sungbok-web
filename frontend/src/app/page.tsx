// ISR: Revalidate every 1 hour (메인 페이지는 최신 설교 노출 위해 1시간마다 재생성)
export const revalidate = 3600;

import { HeroSlider } from '@/components/home/HeroSlider';
import { WorshipSection } from '@/components/home/WorshipSection';
import { MinistriesSection } from '@/components/home/MinistriesSection';
import { NewsSection } from '@/components/home/NewsSection';
import { OnlineWorshipSection } from '@/components/home/OnlineWorshipSection';
import { EventBanner } from '@/components/home/EventBanner';
import { QuickActions } from '@/components/home/QuickActions';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';

export default async function HomePage() {
  // TODO: Phase 6 - Backend API 연동
  // const [sermons, news, ministries] = await Promise.all([
  //   fetch('http://localhost:8080/api/sermons/latest').then(r => r.json()),
  //   fetch('http://localhost:8080/api/news/recent').then(r => r.json()),
  //   fetch('http://localhost:8080/api/ministries').then(r => r.json()),
  // ]);

  return (
    <>
      <Header transparent />

      <main>
        <HeroSlider />
        <WorshipSection />
        <MinistriesSection />
        <NewsSection />
        <OnlineWorshipSection />
        <EventBanner />
        <QuickActions />
      </main>

      <Footer />
    </>
  );
}
