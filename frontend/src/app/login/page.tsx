import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { LoginForm } from './LoginForm';

export const metadata = {
  title: '로그인 - 성복교회',
  description: '성복교회 회원 로그인',
};

export default function LoginPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="로그인" subtitle="성복교회 온라인 서비스" />

        <div className="container mx-auto px-4 py-16 max-w-md">
          <div className="bg-white rounded-2xl border border-gray-200 p-8 shadow-sm">
            <LoginForm />

            <div className="mt-6 pt-6 border-t border-gray-200 text-center">
              <p className="text-sm text-gray-600">
                계정이 없으신가요?{' '}
                <a
                  href="/register"
                  className="text-primary-600 hover:text-primary-700 font-semibold cursor-pointer"
                >
                  회원가입
                </a>
              </p>
            </div>
          </div>

          <div className="mt-8 text-center">
            <p className="text-sm text-gray-500">
              로그인에 문제가 있으신가요?
              <br />
              교회 사무실(02-1234-5678)로 문의해 주세요.
            </p>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
