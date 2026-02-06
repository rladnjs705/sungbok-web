import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { PageHero } from '@/components/layout/PageHero';
import { notFound } from 'next/navigation';
import Image from 'next/image';
import Link from 'next/link';
import { GalleryClient } from './GalleryClient';

// ISR: Revalidate every 1 hour
export const revalidate = 3600;

const GALLERIES = [
  {
    id: 1,
    title: '2024 부활절 연합예배',
    date: '2024.03.31',
    category: '예배',
    description: '부활절을 맞아 모든 부서가 함께한 연합예배의 은혜로운 순간들입니다.',
    coverImage: '/images/0301_1.jpg',
    images: [
      { id: 1, src: '/images/0301_1.jpg', caption: '부활절 연합예배' },
      { id: 2, src: '/images/0301_2.jpg', caption: '찬양 시간' },
      { id: 3, src: '/images/0301_3.jpg', caption: '예배 후 교제' },
      { id: 4, src: '/images/0302_1.jpg', caption: '성도들의 모습' },
    ],
  },
  {
    id: 2,
    title: '청년부 겨울 수련회',
    date: '2024.02.23',
    category: '수련회',
    description: '2박 3일간 강원도 평창에서 진행된 청년부 수련회의 아름다운 순간들입니다.',
    coverImage: '/images/0302_1.jpg',
    images: [
      { id: 1, src: '/images/0302_1.jpg', caption: '수련회 첫날' },
      { id: 2, src: '/images/0302_2.jpg', caption: '찬양과 기도' },
      { id: 3, src: '/images/0302_3.jpg', caption: '소그룹 나눔' },
      { id: 4, src: '/images/0303_1.jpg', caption: '단체 사진' },
    ],
  },
  {
    id: 3,
    title: '어린이 여름성경학교',
    date: '2024.08.05',
    category: '성경학교',
    description: '여름방학을 맞아 진행된 어린이 성경학교의 즐거운 추억들입니다.',
    coverImage: '/images/0303_1.jpg',
    images: [
      { id: 1, src: '/images/0303_1.jpg', caption: '개막 예배' },
      { id: 2, src: '/images/0303_2.jpg', caption: '성경 공부' },
      { id: 3, src: '/images/0303_3.jpg', caption: '레크리에이션' },
      { id: 4, src: '/images/0301_1.jpg', caption: '폐막 예배' },
    ],
  },
] as const;

interface GalleryDetailPageProps {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: GalleryDetailPageProps) {
  const { id } = await params;
  const gallery = GALLERIES.find((g) => g.id === parseInt(id));

  if (!gallery) {
    return {
      title: '갤러리를 찾을 수 없습니다 - 성복교회',
    };
  }

  return {
    title: `${gallery.title} - 사진갤러리 - 성복교회`,
    description: gallery.description,
  };
}

export default async function GalleryDetailPage({
  params,
}: GalleryDetailPageProps) {
  const { id } = await params;
  const gallery = GALLERIES.find((g) => g.id === parseInt(id));

  if (!gallery) {
    notFound();
  }

  return (
    <>
      <Header />

      <main className="pt-20">
        <PageHero title="사진갤러리" subtitle="성복교회의 아름다운 순간들" />

        <div className="container mx-auto px-4 py-16 max-w-6xl">
          {/* Gallery Info */}
          <div className="mb-12">
            <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400 mb-4">
              <span className="px-3 py-1 bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300 rounded-full font-semibold">
                {gallery.category}
              </span>
              <span>{gallery.date}</span>
            </div>
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
              {gallery.title}
            </h1>
            <p className="text-lg text-gray-700 dark:text-gray-300 leading-relaxed">
              {gallery.description}
            </p>
          </div>

          {/* Image Grid with Lightbox */}
          <div className="mb-12">
            <GalleryClient images={gallery.images} />
          </div>

          {/* Navigation */}
          <div className="flex justify-between items-center pt-8 border-t border-gray-200 dark:border-gray-700">
            <Link
              href="/news"
              className="px-6 py-3 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300 font-semibold rounded-lg transition-colors cursor-pointer"
            >
              목록으로
            </Link>
            <div className="flex gap-4">
              {gallery.id > 1 && (
                <Link
                  href={`/gallery/${gallery.id - 1}`}
                  className="px-6 py-3 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-800 text-gray-700 dark:text-gray-300 font-semibold rounded-lg transition-colors cursor-pointer"
                >
                  이전 갤러리
                </Link>
              )}
              {gallery.id < GALLERIES.length && (
                <Link
                  href={`/gallery/${gallery.id + 1}`}
                  className="px-6 py-3 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-800 text-gray-700 dark:text-gray-300 font-semibold rounded-lg transition-colors cursor-pointer"
                >
                  다음 갤러리
                </Link>
              )}
            </div>
          </div>

          {/* Related Galleries */}
          <div className="mt-16">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-8">
              다른 갤러리
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {GALLERIES.filter((g) => g.id !== gallery.id)
                .slice(0, 3)
                .map((item, index) => (
                  <Link
                    key={item.id}
                    href={`/gallery/${item.id}`}
                    className="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                  >
                    <div className="relative aspect-video">
                      <Image
                        src={item.coverImage}
                        alt={item.title}
                        fill
                        sizes="(max-width: 768px) 100vw, 33vw"
                        loading={index === 0 ? 'eager' : 'lazy'}
                        className="object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                    </div>
                    <div className="p-6">
                      <p className="text-sm text-primary-600 dark:text-primary-400 font-semibold mb-2">
                        {item.date}
                      </p>
                      <h3 className="text-lg font-bold text-gray-900 dark:text-white group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors">
                        {item.title}
                      </h3>
                    </div>
                  </Link>
                ))}
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
