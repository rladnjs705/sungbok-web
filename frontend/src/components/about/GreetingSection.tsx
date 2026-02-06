import Image from 'next/image';

const PASTORS = [
  {
    id: 1,
    name: '이태희',
    title: '원로목사',
    image: '/images/pastor_chairman.jpg',
  },
  {
    id: 2,
    name: '이요셉',
    title: '담임목사',
    image: '/images/pastor_senior.jpg',
  },
] as const;

const GREETINGS = [
  {
    id: 1,
    title: '원로목사 인사말',
    content: [
      { type: 'heading' as const, text: '할렐루야!' },
      { type: 'paragraph' as const, text: '성복교회 홈페이지에 방문하여 주신 여러분들을 **주님의 이름으로 환영합니다.**' },
      { type: 'paragraph' as const, text: '지금까지 하나님의 인도하심으로 부흥 성장해 온 저희 성복교회는 지친 인생 속에서 잠시 쉬어갈 수 있는 쉼터가 되어드릴 것이며 예수 그리스도로 말미암아 천국에 이르는 구원의 방주가 될 것입니다.' },
      { type: 'paragraph' as const, text: '성복교회는 언제나 열려있습니다.\n성복교회는 여러분을 기다리고 있습니다.\n여러분을 환영합니다.\n여러분들의 심령과 가정과 일터 위에 하나님의 크신 은혜와 축복이 충만하기를 축원합니다.' },
    ],
    author: '성복교회 원로목사 이태희 드림',
  },
  {
    id: 2,
    title: '담임목사 인사말',
    content: [
      { type: 'heading' as const, text: '할렐루야!' },
      { type: 'paragraph' as const, text: '예수님의 이름으로 환영합니다.' },
      { type: 'paragraph' as const, text: '성복교회의 모든 가족들은 예배를 통해 예수님의 은혜를 전 인격적으로 경험하고 말씀 교제를 통해 성령 안에서 삶의 소망을 회복하여 진정한 자유를 누립니다.' },
      { type: 'paragraph' as const, text: '전도와 선교를 통해 하늘의 기쁨과 행복을 세상에 나누고 다음 세대를 거룩하게 세워 세상을 변화시켜 나갑니다. 이러한 아름다운 공동체에 여러분들을 초대합니다.' },
      { type: 'paragraph' as const, text: '언제나 하나님의 축복과 예수님의 은혜와 성령님의 평강이 여러분들의 인생 가운데 온전히 임하시길 진심으로 기원합니다.' },
    ],
    author: '성복교회 담임목사 이요셉 드림',
  },
] as const;

export function GreetingSection() {
  return (
    <div className="space-y-12">
      {GREETINGS.map((greeting, index) => {
        const pastor = PASTORS[index];
        return (
          <article
            key={greeting.id}
            className="bg-white dark:bg-gray-800 rounded-2xl p-8 md:p-12 shadow-md border border-gray-200 dark:border-gray-700"
          >
            <h2 className="text-3xl font-bold text-primary-600 dark:text-primary-400 mb-8 pb-4 border-b-2 border-gray-200 dark:border-gray-700">
              {greeting.title}
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-12 gap-8">
              {/* 목사 카드 (왼쪽) */}
              <div className="md:col-span-4">
                <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg transition-shadow">
                  <div className="relative aspect-[3/4]">
                    <Image
                      src={pastor.image}
                      alt={pastor.name}
                      fill
                      className="object-cover"
                    />
                  </div>
                  <div className="p-6 text-center">
                    <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
                      {pastor.name}
                    </h3>
                    <p className="text-sm text-primary-600 dark:text-primary-400 font-semibold tracking-wider uppercase">
                      {pastor.title}
                    </p>
                  </div>
                </div>
              </div>

              {/* 인사말 (오른쪽) */}
              <div className="md:col-span-8">
                <div className="space-y-6 text-gray-700 dark:text-gray-300 leading-relaxed">
                  {greeting.content.map((item, idx) => {
                    if (item.type === 'heading') {
                      return (
                        <p key={idx} className="text-2xl font-bold text-primary-600 dark:text-primary-400">
                          {item.text}
                        </p>
                      );
                    }
                    return (
                      <p
                        key={idx}
                        className="text-lg whitespace-pre-line"
                        dangerouslySetInnerHTML={{
                          __html: item.text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>'),
                        }}
                      />
                    );
                  })}
                </div>

                <p className="mt-8 text-right font-semibold text-gray-900 dark:text-gray-100">
                  {greeting.author}
                </p>
              </div>
            </div>
          </article>
        );
      })}
    </div>
  );
}
