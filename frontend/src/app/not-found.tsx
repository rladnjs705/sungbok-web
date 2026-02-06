import type { Metadata } from 'next';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import Link from 'next/link';

export const metadata: Metadata = {
  title: '페이지를 찾을 수 없습니다 - 성복교회',
  description: '요청하신 페이지를 찾을 수 없습니다.',
  // Next.js가 자동으로 noindex 주입하므로 명시 불필요
};

export default function NotFound() {
  return (
    <>
      <Header />
      <main className="min-h-screen flex items-center justify-center bg-white dark:bg-gray-900 px-4 py-16">
        <div className="text-center max-w-2xl">
          {/* 제목 */}
          <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-gray-900 dark:text-white mb-6">
            페이지를 찾을 수 없습니다
          </h1>

          {/* 설명 */}
          <p className="text-lg md:text-xl text-gray-600 dark:text-gray-400 mb-10 leading-relaxed">
            요청하신 페이지가 존재하지 않거나 이동되었습니다.<br/>
            홈으로 돌아가시거나 예배 안내를 확인해 보세요.
          </p>

          {/* CTA 버튼 */}
          <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center">
            <Link
              href="/"
              className="inline-flex items-center justify-center px-6 md:px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors dark:bg-blue-700 dark:hover:bg-blue-800"
            >
              홈으로 돌아가기
            </Link>
            <Link
              href="/worship"
              className="inline-flex items-center justify-center px-6 md:px-8 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 font-semibold rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
            >
              예배안내 보기
            </Link>
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
}
