import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';

export const metadata = {
  title: '헌금 안내 - 성복교회',
  description: '성복교회 헌금 방법 및 계좌 안내',
};

const DONATION_TYPES = [
  {
    id: 1,
    icon: '💰',
    title: '십일조',
    description: '소득의 십분의 일을 하나님께 드리는 헌금',
    account: '국민은행 123-456-789012',
  },
  {
    id: 2,
    icon: '🙏',
    title: '감사헌금',
    description: '하나님의 은혜에 감사하며 드리는 헌금',
    account: '국민은행 123-456-789012',
  },
  {
    id: 3,
    icon: '🌍',
    title: '선교헌금',
    description: '국내외 선교 사역을 위한 헌금',
    account: '국민은행 123-456-789013',
  },
  {
    id: 4,
    icon: '⛪',
    title: '건축헌금',
    description: '교회 건축 및 시설을 위한 헌금',
    account: '국민은행 123-456-789014',
  },
] as const;

const DONATION_METHODS = [
  {
    id: 1,
    title: '계좌 이체',
    description: '은행 앱 또는 ATM을 통해 직접 이체',
    steps: [
      '위 계좌번호를 확인하세요',
      '은행 앱 또는 ATM에서 이체를 진행하세요',
      '예금주: 성복교회',
    ],
  },
  {
    id: 2,
    title: '온라인 헌금',
    description: '신용카드 또는 계좌이체로 간편하게',
    steps: [
      '아래 온라인 헌금 버튼을 클릭하세요',
      '헌금 종류와 금액을 선택하세요',
      '결제 정보를 입력하고 완료하세요',
    ],
  },
  {
    id: 3,
    title: '정기 헌금',
    description: '매월 자동으로 이체되는 정기 헌금',
    steps: [
      '교회 사무실로 연락주세요 (02-1234-5678)',
      '정기 이체 신청서를 작성하세요',
      '매월 지정일에 자동 이체됩니다',
    ],
  },
] as const;

export default function DonationPage() {
  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="헌금 안내" subtitle="하나님께 드리는 정성" />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Introduction */}
          <div className="text-center mb-16 max-w-3xl mx-auto">
            <p className="text-lg text-gray-700 leading-relaxed">
              헌금은 하나님께 드리는 예배의 한 부분입니다.
              <br />
              여러분의 귀한 헌금은 복음 전파와 이웃 사랑을 실천하는 데 사용됩니다.
            </p>
          </div>

          {/* Donation Types */}
          <section className="mb-20">
            <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
              헌금 종류
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {DONATION_TYPES.map((type) => (
                <div
                  key={type.id}
                  className="bg-white rounded-xl border border-gray-200 p-8 hover:shadow-lg transition-shadow"
                >
                  <div className="text-5xl mb-4">{type.icon}</div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">
                    {type.title}
                  </h3>
                  <p className="text-gray-600 mb-4">{type.description}</p>
                  <div className="bg-gray-50 rounded-lg p-4">
                    <p className="text-sm text-gray-500 mb-1">입금 계좌</p>
                    <p className="text-lg font-semibold text-gray-900">
                      {type.account}
                    </p>
                    <p className="text-sm text-gray-600 mt-2">
                      예금주: 성복교회
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Donation Methods */}
          <section className="mb-20">
            <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
              헌금 방법
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              {DONATION_METHODS.map((method) => (
                <div
                  key={method.id}
                  className="bg-gradient-to-br from-primary-50 to-accent-50 rounded-xl p-8"
                >
                  <h3 className="text-xl font-bold text-gray-900 mb-3">
                    {method.title}
                  </h3>
                  <p className="text-gray-600 mb-6">{method.description}</p>
                  <div className="space-y-3">
                    {method.steps.map((step, index) => (
                      <div key={index} className="flex items-start gap-3">
                        <span className="flex-shrink-0 w-6 h-6 bg-primary-500 text-white rounded-full flex items-center justify-center text-sm font-semibold">
                          {index + 1}
                        </span>
                        <p className="text-sm text-gray-700">{step}</p>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Online Donation Button */}
          <section className="bg-gradient-to-br from-primary-500 to-accent-500 rounded-2xl p-12 text-center text-white mb-20">
            <h2 className="text-3xl font-bold mb-4">온라인 헌금</h2>
            <p className="text-lg opacity-90 mb-8">
              신용카드, 계좌이체로 간편하게 헌금하실 수 있습니다
            </p>
            <button className="px-8 py-4 bg-white text-primary-600 font-bold rounded-full hover:bg-white/90 transition-colors cursor-pointer">
              온라인 헌금하기
            </button>
          </section>

          {/* Contact */}
          <section className="bg-white rounded-2xl border border-gray-200 p-8 text-center">
            <h3 className="text-2xl font-bold text-gray-900 mb-4">
              헌금 관련 문의
            </h3>
            <p className="text-gray-600 mb-6">
              헌금과 관련하여 궁금하신 사항은 교회 사무실로 연락 주시기 바랍니다.
            </p>
            <div className="flex flex-col md:flex-row gap-6 justify-center items-center">
              <div>
                <p className="text-sm text-gray-500">전화</p>
                <p className="text-xl font-semibold text-gray-900">
                  02-1234-5678
                </p>
              </div>
              <div className="hidden md:block w-px h-12 bg-gray-300"></div>
              <div>
                <p className="text-sm text-gray-500">이메일</p>
                <p className="text-xl font-semibold text-gray-900">
                  donation@sungbok.church
                </p>
              </div>
              <div className="hidden md:block w-px h-12 bg-gray-300"></div>
              <div>
                <p className="text-sm text-gray-500">업무 시간</p>
                <p className="text-xl font-semibold text-gray-900">
                  월-금 09:00-17:00
                </p>
              </div>
            </div>
          </section>
        </div>
      </main>

      <Footer />
    </>
  );
}
