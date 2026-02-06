import { HeroSection } from '@/components/home/HeroSection';

export default function MockupPage() {
  return (
    <main className="min-h-screen">
      <HeroSection />

      {/* Temporary content to show scrolling */}
      <section className="bg-white py-20">
        <div className="container mx-auto px-4 text-center">
          <h2 className="mb-4 text-4xl font-bold text-slate-800">
            Hero Section 목업
          </h2>
          <p className="text-lg text-slate-600">
            위의 Hero Section은 frontend-design 스킬의 원칙을 적용하여 제작되었습니다.
          </p>

          <div className="mt-12 grid gap-6 md:grid-cols-3">
            <div className="rounded-lg bg-blue-50 p-6">
              <h3 className="mb-2 text-xl font-bold text-blue-900">
                활동적 디자인
              </h3>
              <p className="text-blue-700">
                Video background, gradient overlay, staggered animations로
                역동성 표현
              </p>
            </div>

            <div className="rounded-lg bg-violet-50 p-6">
              <h3 className="mb-2 text-xl font-bold text-violet-900">
                독특한 Typography
              </h3>
              <p className="text-violet-700">
                Unbounded 폰트로 모던하고 기하학적인 느낌 제공 (Inter, Arial 지양)
              </p>
            </div>

            <div className="rounded-lg bg-orange-50 p-6">
              <h3 className="mb-2 text-xl font-bold text-orange-900">
                반응형 최적화
              </h3>
              <p className="text-orange-700">
                Mobile-first approach, video → image fallback
              </p>
            </div>
          </div>

          <div className="mt-12">
            <h3 className="mb-6 text-2xl font-bold text-slate-800">
              기술 스택
            </h3>
            <div className="flex flex-wrap justify-center gap-3">
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                Next.js 16.1
              </span>
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                Tailwind CSS v4
              </span>
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                shadcn/ui
              </span>
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                TypeScript
              </span>
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                Unbounded Font
              </span>
              <span className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">
                Pretendard Variable
              </span>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
