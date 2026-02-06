// ISR: Revalidate every 2 hours
export const revalidate = 7200;

import { PageHero } from '@/components/layout/PageHero';
import { MinistriesSection } from '@/components/home/MinistriesSection';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';

export const metadata = {
  title: '다음세대 - 성복교회',
  description: '성복교회 다음세대 부서를 소개합니다',
};

export default function MinistriesPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="다음세대"
          subtitle="미래를 준비하는 믿음의 세대"
        />

        <div className="container mx-auto px-4 py-16">
          <MinistriesSection showHeader={false} />
        </div>
      </main>

      <Footer />
    </>
  );
}
