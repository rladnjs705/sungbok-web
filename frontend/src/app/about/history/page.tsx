import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import Link from 'next/link';

export const metadata = {
  title: '교회 연혁 - 교회소개 - 성복교회',
  description: '성복교회의 역사와 주요 사건을 소개합니다.',
};

const HISTORY_DATA = [
  {
    year: '2005',
    events: [
      { month: '1월', title: '성복교회 창립', description: '50명의 성도와 함께 첫 예배' },
      { month: '3월', title: '교회 법인 등록', description: '종교단체 정식 등록 완료' },
      { month: '9월', title: '영아부/유치부 개설', description: '다음세대 교육 시작' },
    ],
  },
  {
    year: '2007',
    events: [
      { month: '2월', title: '청년부 창단', description: '20-30대 청년 사역 시작' },
      { month: '7월', title: '여름성경학교', description: '첫 어린이 성경학교 개최' },
      { month: '12월', title: '성도 100명 돌파', description: '창립 2년 만에 100명 돌파' },
    ],
  },
  {
    year: '2010',
    events: [
      { month: '3월', title: '새 예배당 건축', description: '예루살렘성전 착공' },
      { month: '10월', title: '초등부/중등부 분반', description: '연령별 맞춤 교육 체계 구축' },
      { month: '12월', title: '첫 해외 선교', description: '필리핀 단기선교 파송' },
    ],
  },
  {
    year: '2012',
    events: [
      { month: '5월', title: '예루살렘성전 봉헌', description: '지하 1층, 지상 4층 예배당 완공' },
      { month: '8월', title: '고등부 개설', description: '중고등부 분리 및 청소년 사역 강화' },
      { month: '11월', title: '성도 200명 돌파', description: '새 성전에서 부흥의 역사' },
    ],
  },
  {
    year: '2015',
    events: [
      { month: '3월', title: '영어예배부 개설', description: '국제 예배 사역 시작' },
      { month: '6월', title: '교회학교 확장', description: '영아부부터 청년부까지 8부서 체제' },
      { month: '9월', title: '온라인 예배 시작', description: 'YouTube 라이브 스트리밍 개시' },
    ],
  },
  {
    year: '2018',
    events: [
      { month: '4월', title: '선교센터 개관', description: '국내외 선교 지원 본부 설립' },
      { month: '7월', title: '성도 300명 돌파', description: '창립 13년 만에 300명 돌파' },
      { month: '10월', title: '지역 봉사 시작', description: '지역아동센터 지원 사역' },
    ],
  },
  {
    year: '2020',
    events: [
      { month: '2월', title: '코로나19 대응', description: '온라인 예배 전환 및 방역 체계 구축' },
      { month: '8월', title: '드라이브 스루 예배', description: '비대면 예배 혁신' },
      { month: '12월', title: '교회 앱 출시', description: '온라인 헌금 및 소통 플랫폼' },
    ],
  },
  {
    year: '2022',
    events: [
      { month: '1월', title: '대면 예배 재개', description: '단계적 정상화 시작' },
      { month: '5월', title: '하이브리드 예배', description: '현장+온라인 동시 진행 체제' },
      { month: '11월', title: '성도 400명 돌파', description: '창립 17년 만에 400명 돌파' },
    ],
  },
  {
    year: '2024',
    events: [
      { month: '1월', title: '20주년 감사예배', description: '창립 20주년 기념 예배' },
      { month: '3월', title: '교육관 증축 착공', description: '다음세대를 위한 새 건물' },
      { month: '현재', title: '지속적인 성장', description: '하나님 나라 확장을 위한 발걸음' },
    ],
  },
] as const;

export default function HistoryPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="교회 연혁" subtitle="하나님의 은혜로 이루어온 역사" />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Breadcrumb */}
          <div className="mb-12 text-sm text-gray-600">
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
            <strong className="text-gray-900">교회 연혁</strong>
          </div>

          {/* Introduction */}
          <div className="text-center mb-16 max-w-3xl mx-auto">
            <p className="text-lg text-gray-700 leading-relaxed">
              2005년 창립 이래, 성복교회는 하나님의 은혜 가운데 지속적으로 성장해 왔습니다.
              <br />
              말씀과 기도로 세워진 교회, 사랑으로 하나 되는 공동체를 이루어가고 있습니다.
            </p>
          </div>

          {/* Timeline */}
          <div className="relative">
            {/* Center Line */}
            <div className="absolute left-1/2 transform -translate-x-1/2 h-full w-1 bg-gradient-to-b from-primary-300 via-accent-300 to-primary-300 hidden lg:block"></div>

            {/* Timeline Items */}
            <div className="space-y-16">
              {HISTORY_DATA.map((yearData, yearIndex) => (
                <div key={yearData.year} className="relative">
                  {/* Year Badge */}
                  <div className="flex justify-center mb-8">
                    <div className="relative z-10 px-8 py-3 bg-gradient-to-r from-primary-500 to-accent-500 text-white rounded-full shadow-lg">
                      <span className="text-2xl font-bold">{yearData.year}</span>
                    </div>
                  </div>

                  {/* Events */}
                  <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-16">
                    {yearData.events.map((event, eventIndex) => {
                      const isLeft = eventIndex % 2 === 0;
                      return (
                        <div
                          key={eventIndex}
                          className={`relative ${
                            isLeft ? 'lg:text-right lg:pr-8' : 'lg:col-start-2 lg:pl-8'
                          }`}
                        >
                          {/* Timeline Dot */}
                          <div
                            className={`hidden lg:block absolute top-6 w-4 h-4 bg-primary-500 rounded-full border-4 border-white shadow-lg ${
                              isLeft ? '-right-10' : '-left-10'
                            }`}
                          ></div>

                          {/* Event Card */}
                          <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-md hover:shadow-lg transition-shadow">
                            <div className="text-sm text-primary-600 font-semibold mb-2">
                              {event.month}
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">
                              {event.title}
                            </h3>
                            <p className="text-gray-700">{event.description}</p>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Statistics */}
          <section className="mt-20 bg-gradient-to-br from-primary-50 to-accent-50 rounded-2xl p-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
              성복교회 현황
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div className="text-center">
                <div className="text-5xl font-bold text-primary-600 mb-2">
                  20+
                </div>
                <div className="text-gray-700">창립 연수</div>
              </div>
              <div className="text-center">
                <div className="text-5xl font-bold text-primary-600 mb-2">
                  400+
                </div>
                <div className="text-gray-700">교인 수</div>
              </div>
              <div className="text-center">
                <div className="text-5xl font-bold text-primary-600 mb-2">
                  8
                </div>
                <div className="text-gray-700">부서</div>
              </div>
              <div className="text-center">
                <div className="text-5xl font-bold text-primary-600 mb-2">
                  15+
                </div>
                <div className="text-gray-700">선교 파트너</div>
              </div>
            </div>
          </section>

          {/* Vision */}
          <section className="mt-20 text-center">
            <h2 className="text-3xl font-bold text-gray-900 mb-6">
              앞으로의 비전
            </h2>
            <p className="text-lg text-gray-700 leading-relaxed max-w-3xl mx-auto mb-8">
              성복교회는 앞으로도 말씀 위에 굳게 서서, 다음 세대를 세우며,
              지역 사회와 세계를 섬기는 교회로 나아갈 것입니다.
              하나님의 영광을 위해, 그리고 영혼 구원을 위해 최선을 다하겠습니다.
            </p>
            <div className="inline-block px-8 py-4 bg-gradient-to-r from-primary-500 to-accent-500 text-white font-bold rounded-full">
              하나님께 영광, 이웃에게 사랑
            </div>
          </section>
        </div>
      </main>

      <Footer />
    </>
  );
}
