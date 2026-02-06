import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { RegisterForm } from './RegisterForm';

export const metadata = {
  title: '회원가입 - 성복교회',
  description: '성복교회 회원 가입',
};

export default function RegisterPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="회원가입" subtitle="성복교회 온라인 서비스" />

        <div className="container mx-auto px-4 py-16 max-w-md">
          <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-8 shadow-sm">
            <RegisterForm />

            <div className="mt-6 pt-6 border-t border-gray-200 text-center">
              <p className="text-sm text-gray-600">
                이미 계정이 있으신가요?{' '}
                <a
                  href="/login"
                  className="text-primary-600 hover:text-primary-700 font-semibold cursor-pointer"
                >
                  로그인
                </a>
              </p>
            </div>
          </div>

          <div className="mt-8 text-center">
            <p className="text-sm text-gray-500">
              가입 문의: 교회 사무실 02-1234-5678
            </p>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
