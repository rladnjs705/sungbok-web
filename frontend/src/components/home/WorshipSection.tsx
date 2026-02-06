import Image from 'next/image';
import Link from 'next/link';

const WORSHIP_CARDS = [
  {
    id: 1,
    image: '/images/main_con2_img1.png',
    title: '금주의 말씀',
    link: '/worship',
    content: (
      <>
        "내가 선한 목자라 선한 목자는 양들을 위하여 목숨을 버리거니와"
        <br />
        <strong>- 요한복음 10:11</strong>
      </>
    ),
  },
  {
    id: 2,
    image: '/images/main_con2_img2.png',
    title: '예배 시간 안내',
    link: '/worship',
    content: (
      <>
        <strong>주일 1부 예배</strong> 오전 9시
        <br />
        <strong>주일 2부 예배</strong> 오전 11시
        <br />
        <strong>수요예배</strong> 오후 7시 30분
        <br />
        <strong>새벽기도회</strong> 매일 오전 5시 30분
      </>
    ),
  },
] as const;

export function WorshipSection() {
  return (
    <section className="py-20 bg-white">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="text-center mb-12">
          <p className="text-sm text-primary-500 font-semibold tracking-wider mb-2">
            Worship & Word
          </p>
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900">
            말씀과 예배
          </h2>
        </div>

        {/* Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-5xl mx-auto">
          {WORSHIP_CARDS.map((card) => (
            <Link
              key={card.id}
              href={card.link}
              className="block bg-white dark:bg-gray-800 rounded-2xl shadow-md overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer"
            >
              <div className="relative h-64">
                <Image
                  src={card.image}
                  alt={card.title}
                  fill
                  sizes="(max-width: 768px) 100vw, 50vw"
                  className="object-cover"
                />
              </div>
              <div className="p-6">
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
                  {card.title}
                </h3>
                <p className="text-gray-700 dark:text-gray-300 leading-relaxed">{card.content}</p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}
