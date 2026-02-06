import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import Image from 'next/image';
import Link from 'next/link';

export const metadata = {
  title: '섬기는이들 - 교회소개 - 성복교회',
  description: '성복교회를 섬기는 목회자와 교역자를 소개합니다.',
};

// StaffSection에서 가져온 실제 데이터
const STAFF_MEMBERS = [
  { name: '이원효 목사', role: '수석목사/3교구', image: '/images/pro_c01.jpg' },
  { name: '조정한 목사', role: '4교구/미디어', image: '/images/pro_c02.jpg' },
  { name: '김정한 목사', role: '교육부 총괄/청년부', image: '/images/pro_c03.jpg' },
  { name: '황창조 목사', role: '2교구/초등부', image: '/images/pro_c04.jpg' },
  { name: '박건호 목사', role: '5/6교구', image: '/images/pro_c05.jpg' },
  { name: '김민기 목사', role: '1교구/중등부', image: '/images/pro_c06.jpg' },
  { name: '임경일 목사', role: '찬양 디렉터/엘림가족부', image: '/images/pro_c07.jpg' },
  { name: '최미정 전도사', role: '1-6교구', image: '/images/pro_c08.jpg' },
  { name: '최주찬 전도사', role: '고등부/영어예배부', image: '/images/pro_c09.jpg' },
  { name: '신미자 간사', role: '1교구', image: '/images/pro_c10.jpg' },
  { name: '이옥근 간사', role: '2교구', image: '/images/pro_c11.jpg' },
  { name: '양영복 간사', role: '4교구', image: '/images/pro_c12.jpg' },
  { name: '심흥숙 간사', role: '5교구', image: '/images/pro_c13.jpg' },
  { name: '이진희 간사', role: '영아부', image: '/images/pro_c14.jpg' },
  { name: '이명화 간사', role: '유치부', image: '/images/pro_c15.jpg' },
  { name: '천지연 간사', role: '유년부', image: '/images/pro_c16.jpg' },
] as const;

// 역할별 필터링
const pastors = STAFF_MEMBERS.filter((member) => member.name.includes('목사'));
const evangelists = STAFF_MEMBERS.filter((member) =>
  member.name.includes('전도사')
);
const ministers = STAFF_MEMBERS.filter((member) => member.name.includes('간사'));

export default function StaffPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero
          title="섬기는이들"
          subtitle="하나님과 성도님들을 섬기는 사역자들"
        />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Breadcrumb */}
          <div className="mb-12 text-sm text-gray-600 dark:text-gray-400">
            <Link href="/" className="hover:text-primary-600 cursor-pointer">
              홈
            </Link>
            {' > '}
            <Link
              href="/about"
              className="hover:text-primary-600 cursor-pointer"
            >
              교회소개
            </Link>
            {' > '}
            <strong className="text-gray-900 dark:text-gray-100">
              섬기는이들
            </strong>
          </div>

          {/* 부목사 섹션 */}
          <section className="mb-20">
            <h2 className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">
              부목사
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              {pastors.map((member, index) => (
                <div key={index} className="text-center">
                  <div className="relative aspect-[3/4] mb-3 rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow">
                    <Image
                      src={member.image}
                      alt={member.name}
                      fill
                      className="object-cover"
                    />
                  </div>
                  <h4 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">
                    {member.name}
                  </h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    {member.role}
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* 전도사 섹션 */}
          <section className="mb-20">
            <h2 className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">
              전도사
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              {evangelists.map((member, index) => (
                <div key={index} className="text-center">
                  <div className="relative aspect-[3/4] mb-3 rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow">
                    <Image
                      src={member.image}
                      alt={member.name}
                      fill
                      className="object-cover"
                    />
                  </div>
                  <h4 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">
                    {member.name}
                  </h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    {member.role}
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* 간사 섹션 */}
          <section className="mb-20">
            <h2 className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">
              간사
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              {ministers.map((member, index) => (
                <div key={index} className="text-center">
                  <div className="relative aspect-[3/4] mb-3 rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow">
                    <Image
                      src={member.image}
                      alt={member.name}
                      fill
                      className="object-cover"
                    />
                  </div>
                  <h4 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">
                    {member.name}
                  </h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    {member.role}
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* Call to Action */}
          <section className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-12 text-center">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">
              함께 섬기실 분을 찾습니다
            </h2>
            <p className="text-gray-700 dark:text-gray-300 mb-8">
              성복교회에서 하나님과 성도님들을 섬기는 일에 동참하실 분들을
              환영합니다.
            </p>
            <Link
              href="/contact"
              className="inline-block px-8 py-4 bg-primary-500 hover:bg-primary-600 dark:bg-primary-600 dark:hover:bg-primary-700 text-white font-semibold rounded-lg transition-colors cursor-pointer"
            >
              문의하기
            </Link>
          </section>
        </div>
      </main>

      <Footer />
    </>
  );
}
