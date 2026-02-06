import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import Link from 'next/link';

export const metadata = {
  title: '오시는 길 - 교회소개 - 성복교회',
  description: '성복교회 찾아오시는 길과 주차 안내',
};

const TRANSPORTATION = [
  {
    id: 1,
    type: '🚇 지하철',
    title: '2호선 강남역',
    directions: [
      '2호선 강남역 10번 출구에서 도보 5분',
      '출구를 나와 강남대로 방향으로 직진',
      '두 번째 골목에서 우회전',
    ],
  },
  {
    id: 2,
    type: '🚌 버스',
    title: '강남역 정류장',
    directions: [
      '간선: 146, 360, 740',
      '지선: 3011, 4319, 6411',
      '광역: 1100, 1700, 2000',
      '강남역 하차 후 도보 5분',
    ],
  },
  {
    id: 3,
    type: '🚗 자가용',
    title: '네비게이션',
    directions: [
      '주소: 서울시 강남구 테헤란로 123',
      '성복교회 또는 예루살렘성전 검색',
      '교회 지하주차장 이용 가능',
      '주차 공간: 50대 (무료)',
    ],
  },
] as const;

const PARKING_INFO = [
  {
    id: 1,
    title: '교회 지하주차장',
    description: '지하 1층~2층 주차 가능 (50대)',
    time: '예배 시간 무료 개방',
  },
  {
    id: 2,
    title: '인근 공영주차장',
    description: '강남구민회관 주차장 (도보 2분)',
    time: '유료 주차 (시간당 2,000원)',
  },
  {
    id: 3,
    title: '주차 안내원',
    description: '주일 오전 주차 안내원 배치',
    time: '09:30 - 12:30',
  },
] as const;

export default function DirectionsPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="오시는 길" subtitle="성복교회로 오시는 길을 안내합니다" />

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
            <strong className="text-gray-900">오시는 길</strong>
          </div>

          {/* Address Info */}
          <section className="mb-12">
            <div className="bg-gradient-to-br from-primary-50 to-accent-50 rounded-2xl p-8 md:p-12">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div>
                  <h2 className="text-2xl font-bold text-gray-900 mb-6">
                    교회 정보
                  </h2>
                  <div className="space-y-4">
                    <div>
                      <div className="text-sm text-gray-600 mb-1">주소</div>
                      <div className="text-lg font-semibold text-gray-900">
                        서울시 강남구 테헤란로 123
                      </div>
                      <div className="text-sm text-gray-600">
                        (우편번호: 06234)
                      </div>
                    </div>
                    <div>
                      <div className="text-sm text-gray-600 mb-1">전화</div>
                      <a
                        href="tel:02-1234-5678"
                        className="text-lg font-semibold text-gray-900 hover:text-primary-600 cursor-pointer"
                      >
                        02-1234-5678
                      </a>
                    </div>
                    <div>
                      <div className="text-sm text-gray-600 mb-1">팩스</div>
                      <div className="text-lg font-semibold text-gray-900">
                        02-1234-5679
                      </div>
                    </div>
                    <div>
                      <div className="text-sm text-gray-600 mb-1">이메일</div>
                      <a
                        href="mailto:info@sungbok.church"
                        className="text-lg font-semibold text-gray-900 hover:text-primary-600 cursor-pointer"
                      >
                        info@sungbok.church
                      </a>
                    </div>
                  </div>
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-gray-900 mb-6">
                    예배 시간
                  </h2>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-gray-700">주일 1부 예배</span>
                      <span className="font-semibold text-gray-900">
                        오전 09:00
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-700">주일 2부 예배</span>
                      <span className="font-semibold text-gray-900">
                        오전 11:00
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-700">수요예배</span>
                      <span className="font-semibold text-gray-900">
                        오후 07:30
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-700">새벽기도회</span>
                      <span className="font-semibold text-gray-900">
                        오전 05:30
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          {/* Map */}
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
              지도
            </h2>
            <div className="bg-white rounded-2xl border border-gray-200 overflow-hidden shadow-lg">
              {/* Kakao Map Placeholder */}
              <div className="relative aspect-video bg-gray-100">
                <iframe
                  src="https://map.kakao.com/link/map/성복교회,37.4979,127.0276"
                  width="100%"
                  height="100%"
                  style={{ border: 0 }}
                  allowFullScreen
                  loading="lazy"
                  referrerPolicy="no-referrer-when-downgrade"
                  title="성복교회 지도"
                ></iframe>
              </div>
              <div className="p-6 bg-gray-50">
                <div className="flex flex-wrap gap-4">
                  <a
                    href="https://map.kakao.com/link/to/성복교회,37.4979,127.0276"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="px-6 py-3 bg-yellow-400 hover:bg-yellow-500 text-gray-900 font-semibold rounded-lg transition-colors cursor-pointer"
                  >
                    카카오맵 길찾기
                  </a>
                  <a
                    href="https://map.naver.com/v5/directions/-/-/-/transit?c=14135490.3130372,4518350.1462046,15,0,0,0,dh"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="px-6 py-3 bg-green-500 hover:bg-green-600 text-white font-semibold rounded-lg transition-colors cursor-pointer"
                  >
                    네이버지도 길찾기
                  </a>
                  <a
                    href="https://www.google.com/maps/dir//37.4979,127.0276"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white font-semibold rounded-lg transition-colors cursor-pointer"
                  >
                    구글지도 길찾기
                  </a>
                </div>
              </div>
            </div>
          </section>

          {/* Transportation */}
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
              대중교통 이용
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {TRANSPORTATION.map((transport) => (
                <div
                  key={transport.id}
                  className="bg-white rounded-xl border border-gray-200 p-6 shadow-md"
                >
                  <div className="text-3xl mb-3">{transport.type.split(' ')[0]}</div>
                  <h3 className="text-xl font-bold text-gray-900 mb-4">
                    {transport.title}
                  </h3>
                  <ul className="space-y-2">
                    {transport.directions.map((direction, index) => (
                      <li key={index} className="text-sm text-gray-700 flex items-start gap-2">
                        <span className="text-primary-500 mt-1">•</span>
                        <span>{direction}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </section>

          {/* Parking */}
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
              주차 안내
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {PARKING_INFO.map((parking) => (
                <div
                  key={parking.id}
                  className="bg-gradient-to-br from-primary-50 to-accent-50 rounded-xl p-6"
                >
                  <h3 className="text-xl font-bold text-gray-900 mb-3">
                    {parking.title}
                  </h3>
                  <p className="text-gray-700 mb-2">{parking.description}</p>
                  <p className="text-sm text-primary-600 font-semibold">
                    {parking.time}
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* Important Notice */}
          <section className="bg-white rounded-2xl border border-gray-200 p-8">
            <h3 className="text-xl font-bold text-gray-900 mb-4">
              📌 방문 시 참고사항
            </h3>
            <ul className="space-y-3 text-gray-700">
              <li className="flex items-start gap-3">
                <span className="flex-shrink-0 w-6 h-6 bg-primary-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  1
                </span>
                <span>
                  주일 오전에는 교통 혼잡이 예상되오니 대중교통 이용을 권장합니다.
                </span>
              </li>
              <li className="flex items-start gap-3">
                <span className="flex-shrink-0 w-6 h-6 bg-primary-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  2
                </span>
                <span>
                  장애인 전용 주차구역과 엘리베이터가 마련되어 있습니다.
                </span>
              </li>
              <li className="flex items-start gap-3">
                <span className="flex-shrink-0 w-6 h-6 bg-primary-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  3
                </span>
                <span>
                  처음 방문하시는 분들은 1층 안내데스크에서 도움을 받으실 수 있습니다.
                </span>
              </li>
              <li className="flex items-start gap-3">
                <span className="flex-shrink-0 w-6 h-6 bg-primary-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                  4
                </span>
                <span>
                  찾아오시는 길이 어려우시면 전화(02-1234-5678)로 문의해 주세요.
                </span>
              </li>
            </ul>
          </section>
        </div>
      </main>

      <Footer />
    </>
  );
}
