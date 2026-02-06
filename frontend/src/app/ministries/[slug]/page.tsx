import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { notFound } from 'next/navigation';
import Image from 'next/image';
import Link from 'next/link';

// ISR: Revalidate every 2 hours
export const revalidate = 7200;

const MINISTRIES_DATA = {
  infant: {
    slug: 'infant',
    title: '영아부',
    subtitle: '0-2세 영아들을 위한 예배',
    gradient: 'from-pink-400 to-blue-400 dark:from-pink-800 dark:to-blue-800',
    location: '예루살렘성전 [1F]',
    pastor: '이진희 간사',
    image: '/images/dept_infant.jpg',
    description: '영아부는 0-2세 영아들을 위한 부서입니다. 하나님의 사랑 안에서 건강하게 자라나도록 돕습니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  kindergarten: {
    slug: 'kindergarten',
    title: '유치부',
    subtitle: '3-5세 유아들을 위한 예배',
    gradient: 'from-yellow-400 to-pink-400 dark:from-yellow-800 dark:to-pink-800',
    location: '예루살렘성전 [1F]',
    pastor: '이명화 간사',
    image: '/images/dept_kindergarten.jpg',
    description: '유치부는 3-5세 유아들을 위한 부서입니다. 말씀과 찬양을 통해 하나님을 알아가는 시간을 갖습니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  'elementary-lower': {
    slug: 'elementary-lower',
    title: '유년부',
    subtitle: '초등 1-3학년',
    gradient: 'from-green-400 to-teal-400 dark:from-green-800 dark:to-teal-800',
    location: '소망성전 [2F]',
    pastor: '천지연 간사',
    image: '/images/dept_elementary_lower.jpg',
    description: '유년부는 초등학교 1-3학년 어린이들을 위한 부서입니다. 성경 말씀을 배우고 실천하는 믿음의 어린이로 자라납니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  elementary: {
    slug: 'elementary',
    title: '초등부',
    subtitle: '초등 4-6학년',
    gradient: 'from-blue-400 to-purple-400 dark:from-blue-800 dark:to-purple-800',
    location: '화평성전 [4F]',
    pastor: '황창조 목사',
    image: '/images/dept_elementary.jpg',
    description: '초등부는 초등학교 4-6학년 어린이들을 위한 부서입니다. 하나님의 말씀을 깊이 있게 배우고 적용합니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  middle: {
    slug: 'middle',
    title: '중등부',
    subtitle: '중학교 1-3학년',
    gradient: 'from-purple-400 to-pink-400 dark:from-purple-800 dark:to-pink-800',
    location: '벧엘성전 [4F]',
    pastor: '김민기 목사',
    image: '/images/dept_middleschool.jpg',
    description: '중등부는 중학생들을 위한 부서입니다. 사춘기를 믿음으로 이겨내고 하나님의 비전을 발견합니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  high: {
    slug: 'high',
    title: '고등부',
    subtitle: '고등학교 1-3학년',
    gradient: 'from-indigo-400 to-purple-400 dark:from-indigo-800 dark:to-purple-800',
    location: '벧엘성전 [4F]',
    pastor: '최주찬 전도사',
    image: '/images/dept_highschool.jpg',
    description: '고등부는 고등학생들을 위한 부서입니다. 세상을 변화시킬 다음 세대 리더로 성장합니다.',
    schedule: '매주 일요일 오전 11:00',
  },
  youth: {
    slug: 'youth',
    title: '청년부',
    subtitle: '대학생 및 직장인',
    gradient: 'from-cyan-400 to-blue-400 dark:from-cyan-800 dark:to-blue-800',
    location: '예루살렘성전 [3F]',
    pastor: '김정한 목사',
    image: '/images/dept_youth.jpg',
    description: '청년부는 대학생과 직장인들을 위한 부서입니다. 하나님의 부르심에 응답하며 세상을 섬깁니다.',
    schedule: '매주 일요일 오후 2:00',
  },
  english: {
    slug: 'english',
    title: '영어예배부',
    subtitle: 'English Worship',
    gradient: 'from-teal-400 to-green-400 dark:from-teal-800 dark:to-green-800',
    location: '벧엘성전 [4F]',
    pastor: 'Rev. Joo-Chan Choi',
    image: '/images/dept_english.jpg',
    description: '영어예배부는 영어권 성도들을 위한 부서입니다. English worship service for international congregation.',
    schedule: 'Every Sunday 2:00 PM',
  },
} as const;

interface MinistryDetailPageProps {
  params: Promise<{ slug: string }>;
}

export async function generateMetadata({ params }: MinistryDetailPageProps) {
  const { slug } = await params;
  const ministry = MINISTRIES_DATA[slug as keyof typeof MINISTRIES_DATA];

  if (!ministry) {
    return {
      title: '부서를 찾을 수 없습니다 - 성복교회',
    };
  }

  return {
    title: `${ministry.title} - 성복교회`,
    description: ministry.description,
  };
}

export default async function MinistryDetailPage({
  params,
}: MinistryDetailPageProps) {
  const { slug } = await params;
  const ministry = MINISTRIES_DATA[slug as keyof typeof MINISTRIES_DATA];

  if (!ministry) {
    notFound();
  }

  return (
    <>
      <Header />

      <main className="pt-20">
        {/* Breadcrumb */}
        <div className="container mx-auto px-4 py-6">
          <div className="text-sm text-gray-600 dark:text-gray-400">
            <Link href="/" className="hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer">
              홈
            </Link>
            {' > '}
            <Link
              href="/ministries"
              className="hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer"
            >
              다음세대
            </Link>
            {' > '}
            <strong className="text-gray-900 dark:text-white">{ministry.title}</strong>
          </div>
        </div>

        {/* Hero */}
        <section
          className={`bg-gradient-to-br ${ministry.gradient} py-16 text-center text-white`}
        >
          <div className="container mx-auto px-4">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              {ministry.title}
            </h1>
            <p className="text-lg md:text-xl opacity-95">{ministry.subtitle}</p>
          </div>
        </section>

        {/* Content */}
        <div className="container mx-auto px-4 py-16 max-w-5xl">
          {/* Ministry Image */}
          <div className="mb-12">
            <div className="relative aspect-video rounded-2xl overflow-hidden shadow-xl">
              <Image
                src={ministry.image}
                alt={ministry.title}
                fill
                className="object-cover"
              />
            </div>
          </div>

          {/* Ministry Info */}
          <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-8 mb-8">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6 pb-4 border-b-2 border-gray-200 dark:border-gray-300">
              부서 소개
            </h2>
            <p className="text-gray-700 dark:text-gray-300 leading-relaxed mb-6 text-lg">
              {ministry.description}
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-6">
                <strong className="block text-sm text-primary-600 dark:text-primary-400 mb-2 font-semibold">
                  예배 시간
                </strong>
                <span className="text-lg text-gray-900 dark:text-white">{ministry.schedule}</span>
              </div>
              <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-6">
                <strong className="block text-sm text-primary-600 dark:text-primary-400 mb-2 font-semibold">
                  장소
                </strong>
                <span className="text-lg text-gray-900 dark:text-white">{ministry.location}</span>
              </div>
              <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-6 md:col-span-2">
                <strong className="block text-sm text-primary-600 dark:text-primary-400 mb-2 font-semibold">
                  담당 교역자
                </strong>
                <span className="text-lg text-gray-900 dark:text-white">{ministry.pastor}</span>
              </div>
            </div>
          </div>

          {/* Back Button */}
          <div className="text-center mt-12">
            <Link
              href="/ministries"
              className="inline-block px-8 py-4 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors cursor-pointer"
            >
              목록으로
            </Link>
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
}
