import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { notFound } from 'next/navigation';

// ISR: Revalidate every 10 minutes
export const revalidate = 600;

const NEWS_ITEMS = [
  {
    id: 1,
    category: '공지사항',
    date: '2024.03.15',
    title: '부활절 연합예배 안내',
    content: `부활절을 맞아 모든 부서가 함께하는 연합예배를 드립니다.

**일시**: 2024년 3월 31일 (일) 오전 10:00
**장소**: 본당 3층
**설교**: 이요셉 담임목사

부활절은 예수 그리스도의 부활을 기념하는 가장 중요한 기독교 절기입니다.
올해는 모든 부서가 함께 모여 연합예배로 드리며, 부활의 기쁨과 소망을 나누고자 합니다.

예배 후에는 간단한 다과회가 준비되어 있습니다. 많은 참석 부탁드립니다.`,
    author: '교회 사무국',
  },
  {
    id: 2,
    category: '공지사항',
    date: '2024.03.10',
    title: '청년부 수련회',
    content: `3월 22-24일, 청년부 겨울 수련회가 진행됩니다.

**일정**: 2024년 3월 22일(금) ~ 24일(일) 2박 3일
**장소**: 강원도 평창 수양관
**참가비**: 15만원 (교통비, 숙박비, 식비 포함)
**신청 마감**: 3월 17일(일)

이번 수련회는 "하나님 나라의 청년"이라는 주제로 진행됩니다.
말씀과 찬양, 그리고 교제를 통해 하나님께 더 가까이 나아가는 은혜로운 시간이 되길 기대합니다.

신청은 청년부 총무(010-1234-5678)에게 문자 또는 카톡으로 부탁드립니다.`,
    author: '청년부',
  },
  {
    id: 3,
    category: '주보',
    date: '2024.03.17',
    title: '2024년 3월 셋째주 주보',
    content: `이번주 예배 순서와 교회 소식을 확인하세요.

**주일 1부 예배** (오전 9:00)
- 예배 인도: 김철수 목사
- 설교: 이요셉 담임목사
- 설교 제목: "믿음으로 사는 삶"
- 본문: 히브리서 11:1-6

**주일 2부 예배** (오전 11:00)
- 예배 인도: 박영희 전도사
- 설교: 이요셉 담임목사
- 설교 제목: "믿음으로 사는 삶"
- 본문: 히브리서 11:1-6

**이번 주 교회 소식**
- 3/20(수) 수요예배 오후 7:30
- 3/22(금) 청년부 수련회 출발
- 3/24(일) 장년부 성경공부 오후 2:00`,
    author: '교회 사무국',
  },
  {
    id: 4,
    category: '행사일정',
    date: '2024.03.31',
    title: '부활절 특별예배',
    content: `오전 10시, 본당 3층에서 부활절 특별예배가 있습니다.

부활절을 맞이하여 모든 성도님들을 초대합니다.
예수 그리스도의 부활을 기념하며 함께 예배드리고 기쁨을 나누는 시간이 되기를 소망합니다.

**일시**: 2024년 3월 31일 (일) 오전 10:00
**장소**: 본당 3층
**특순**: 성가대 특별 찬양
**설교**: 이요셉 담임목사

예배 후 교제의 시간이 준비되어 있습니다.`,
    author: '예배부',
  },
  {
    id: 5,
    category: '사진갤러리',
    date: '2024.03.10',
    title: '청년부 겨울 수련회',
    content: `2박 3일간의 은혜로운 수련회 사진입니다.

2024년 2월 23일부터 25일까지 강원도 평창에서 진행된 청년부 겨울 수련회의 아름다운 순간들을 담았습니다.

"하나님 나라의 청년"이라는 주제로 진행된 이번 수련회에서 청년들은 말씀과 찬양, 그리고 깊은 교제를 통해 하나님의 사랑을 경험하는 시간을 가졌습니다.

총 45명의 청년이 함께했으며, 모두가 은혜 가운데 회복되고 새롭게 되는 시간이었습니다.`,
    author: '청년부',
  },
] as const;

interface NewsDetailPageProps {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: NewsDetailPageProps) {
  const { id } = await params;
  const news = NEWS_ITEMS.find((item) => item.id === parseInt(id));

  if (!news) {
    return {
      title: '소식을 찾을 수 없습니다 - 성복교회',
    };
  }

  return {
    title: `${news.title} - 성복교회`,
    description: news.content.substring(0, 100),
  };
}

export default async function NewsDetailPage({ params }: NewsDetailPageProps) {
  const { id } = await params;
  const news = NEWS_ITEMS.find((item) => item.id === parseInt(id));

  if (!news) {
    notFound();
  }

  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title={news.category} subtitle="성복교회 소식" />

        <div className="container mx-auto px-4 py-16 max-w-4xl">
          <article className="bg-white rounded-2xl border border-gray-200 overflow-hidden">
            {/* Header */}
            <div className="bg-gradient-to-br from-primary-500 to-accent-500 h-64"></div>

            {/* Content */}
            <div className="p-8 md:p-12">
              <div className="flex items-center gap-4 text-sm text-gray-500 mb-6">
                <span className="px-3 py-1 bg-primary-100 text-primary-700 rounded-full font-semibold">
                  {news.category}
                </span>
                <span>{news.date}</span>
                <span>작성자: {news.author}</span>
              </div>

              <h1 className="text-3xl md:text-4xl font-bold text-gray-900 mb-8">
                {news.title}
              </h1>

              <div className="prose prose-lg max-w-none">
                {news.content.split('\n').map((line, index) => (
                  <p key={index} className="mb-4 text-gray-700 leading-relaxed">
                    {line}
                  </p>
                ))}
              </div>

              {/* Actions */}
              <div className="mt-12 pt-8 border-t border-gray-200 flex gap-4">
                <a
                  href="/news"
                  className="px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors cursor-pointer"
                >
                  목록으로
                </a>
                <button className="px-6 py-3 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors cursor-pointer">
                  공유하기
                </button>
              </div>
            </div>
          </article>

          {/* Related News */}
          <div className="mt-12">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              관련 소식
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {NEWS_ITEMS.filter((item) => item.category === news.category && item.id !== news.id)
                .slice(0, 2)
                .map((item) => (
                  <a
                    key={item.id}
                    href={`/news/${item.id}`}
                    className="group bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                  >
                    <div className="h-32 bg-gradient-to-br from-primary-400 to-accent-400"></div>
                    <div className="p-6">
                      <p className="text-sm text-primary-600 font-semibold mb-2">
                        {item.date}
                      </p>
                      <h3 className="text-lg font-bold text-gray-900 group-hover:text-primary-600 transition-colors">
                        {item.title}
                      </h3>
                    </div>
                  </a>
                ))}
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
