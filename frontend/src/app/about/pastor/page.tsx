import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import Image from 'next/image';
import Link from 'next/link';

export const metadata = {
  title: '담임목사 - 교회소개 - 성복교회',
  description: '성복교회 담임목사 김성복 목사님을 소개합니다.',
};

export default function PastorPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="담임목사" subtitle="하나님의 말씀을 전하는 종" />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Breadcrumb */}
          <div className="mb-8 text-sm text-gray-600">
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
            <strong className="text-gray-900">담임목사</strong>
          </div>

          {/* Pastor Profile */}
          <div className="bg-white rounded-2xl border border-gray-200 overflow-hidden mb-12">
            <div className="grid grid-cols-1 lg:grid-cols-5 gap-0">
              {/* Image Section */}
              <div className="lg:col-span-2 relative aspect-[3/4] lg:aspect-auto lg:min-h-[600px]">
                <Image
                  src="/images/pastor_senior.jpg"
                  alt="김성복 담임목사"
                  fill
                  className="object-cover"
                />
              </div>

              {/* Info Section */}
              <div className="lg:col-span-3 p-8 lg:p-12">
                <div className="mb-8">
                  <p className="text-sm text-primary-600 font-semibold mb-2">
                    Senior Pastor
                  </p>
                  <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
                    김성복 목사
                  </h1>
                  <p className="text-lg text-gray-600">
                    성복교회 담임목사
                  </p>
                </div>

                {/* Bio */}
                <div className="space-y-6 text-gray-700 leading-relaxed">
                  <p>
                    하나님의 은혜로 성복교회를 섬기고 있는 김성복 목사입니다.
                    2005년부터 성복교회를 담임하며 하나님의 말씀을 전하고
                    성도들을 섬기는 일에 최선을 다하고 있습니다.
                  </p>
                  <p>
                    우리 교회는 "말씀 위에 세워진 교회, 사랑으로 하나 되는 교회,
                    세상을 섬기는 교회"라는 비전을 가지고 있습니다.
                    모든 성도님들이 하나님의 말씀 위에 굳게 서서,
                    서로 사랑하며, 세상을 섬기는 빛과 소금의 역할을 감당하기를
                    소망합니다.
                  </p>
                  <p>
                    특별히 다음 세대를 세우는 일에 힘쓰며, 영아부부터 청년부까지
                    모든 세대가 하나님을 만나고 믿음 안에서 성장할 수 있도록
                    최선을 다하고 있습니다.
                  </p>
                </div>

                {/* Divider */}
                <div className="my-8 border-t border-gray-200"></div>

                {/* Education & Career */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div>
                    <h3 className="text-lg font-bold text-gray-900 mb-4">
                      학력
                    </h3>
                    <ul className="space-y-2 text-sm text-gray-700">
                      <li>• 총신대학교 신학과 졸업</li>
                      <li>• 총신대학교 신학대학원 M.Div</li>
                      <li>• 풀러신학교 목회학 박사 D.Min</li>
                    </ul>
                  </div>
                  <div>
                    <h3 className="text-lg font-bold text-gray-900 mb-4">
                      경력
                    </h3>
                    <ul className="space-y-2 text-sm text-gray-700">
                      <li>• 2005 - 현재: 성복교회 담임목사</li>
                      <li>• 2000 - 2005: 은혜교회 부목사</li>
                      <li>• 1998 - 2000: 사랑교회 전도사</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Ministry Vision */}
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
              목회 비전
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-gradient-to-br from-primary-50 to-accent-50 rounded-xl p-8 text-center">
                <div className="text-4xl mb-4">📖</div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">
                  말씀 위에 세워진 교회
                </h3>
                <p className="text-gray-700">
                  성경적 진리 위에 굳게 서서 하나님의 말씀을 삶의 기준으로
                  삼는 교회
                </p>
              </div>
              <div className="bg-gradient-to-br from-accent-50 to-primary-50 rounded-xl p-8 text-center">
                <div className="text-4xl mb-4">❤️</div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">
                  사랑으로 하나 되는 교회
                </h3>
                <p className="text-gray-700">
                  모든 세대가 그리스도의 사랑 안에서 하나 되어 서로 섬기는
                  교회
                </p>
              </div>
              <div className="bg-gradient-to-br from-primary-50 to-accent-50 rounded-xl p-8 text-center">
                <div className="text-4xl mb-4">🌍</div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">
                  세상을 섬기는 교회
                </h3>
                <p className="text-gray-700">
                  복음으로 세상을 변화시키며 이웃을 사랑으로 섬기는 교회
                </p>
              </div>
            </div>
          </section>

          {/* Message */}
          <section className="bg-white rounded-2xl border border-gray-200 p-8 lg:p-12">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              성도님들께 드리는 말씀
            </h2>
            <div className="prose prose-lg max-w-none">
              <p className="text-gray-700 leading-relaxed mb-4">
                사랑하는 성복교회 성도 여러분,
              </p>
              <p className="text-gray-700 leading-relaxed mb-4">
                우리 교회는 하나님께서 세우신 교회입니다. 이 땅에 하나님 나라를
                이루어가는 거룩한 사명을 가진 공동체입니다. 모든 성도님들이
                그리스도의 제자로서, 말씀과 기도로 무장하여 세상 속에서 빛과
                소금의 역할을 감당하시기를 소망합니다.
              </p>
              <p className="text-gray-700 leading-relaxed mb-4">
                특별히 다음 세대를 위해 함께 기도하고 헌신해 주시기 바랍니다.
                우리의 자녀들이 믿음의 세대로 자라나 이 시대의 리더가 될 수
                있도록 함께 노력합시다.
              </p>
              <p className="text-gray-700 leading-relaxed mb-4">
                성복교회를 방문하시는 모든 분들을 환영합니다. 이곳에서 하나님을
                만나고, 위로와 평안을 얻으시기를 기도합니다.
              </p>
              <p className="text-gray-700 leading-relaxed font-semibold">
                주님의 이름으로 축복합니다.
                <br />
                담임목사 김성복
              </p>
            </div>
          </section>
        </div>
      </main>

      <Footer />
    </>
  );
}
