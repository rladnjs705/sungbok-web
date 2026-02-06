import Image from 'next/image';
import Link from 'next/link';

const MINISTRIES = [
  {
    id: 'infant',
    image: '/images/dept_infant.jpg',
    title: '영아부',
    description: '0-2세 영아들을 위한 예배',
    href: '/ministries/infant',
  },
  {
    id: 'kindergarten',
    image: '/images/dept_kindergarten.jpg',
    title: '유치부',
    description: '3-5세 유아들을 위한 예배',
    href: '/ministries/kindergarten',
  },
  {
    id: 'elementary-lower',
    image: '/images/dept_elementary_lower.jpg',
    title: '유년부',
    description: '초등 1-3학년',
    href: '/ministries/elementary-lower',
  },
  {
    id: 'elementary',
    image: '/images/dept_elementary.jpg',
    title: '초등부',
    description: '초등 4-6학년',
    href: '/ministries/elementary',
  },
  {
    id: 'middle',
    image: '/images/dept_middleschool.jpg',
    title: '중등부',
    description: '중학교 1-3학년',
    href: '/ministries/middle',
  },
  {
    id: 'high',
    image: '/images/dept_highschool.jpg',
    title: '고등부',
    description: '고등학교 1-3학년',
    href: '/ministries/high',
  },
  {
    id: 'youth',
    image: '/images/dept_youth.jpg',
    title: '청년부',
    description: '대학생 및 직장인',
    href: '/ministries/youth',
  },
  {
    id: 'english',
    image: '/images/dept_english.jpg',
    title: '영어예배부',
    description: 'English Worship',
    href: '/ministries/english',
  },
] as const;

interface MinistriesSectionProps {
  showHeader?: boolean;
}

export function MinistriesSection({ showHeader = true }: MinistriesSectionProps) {
  return (
    <section className="py-20 bg-gray-50">
      <div className="container mx-auto px-4">
        {/* Header */}
        {showHeader && (
          <div className="text-center mb-12">
            <p className="text-sm text-primary-500 font-semibold tracking-wider mb-2">
              Next Generation
            </p>
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900">
              다음세대
            </h2>
          </div>
        )}

        {/* Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {MINISTRIES.map((ministry) => (
            <Link
              key={ministry.id}
              href={ministry.href}
              className="group relative h-64 rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-shadow cursor-pointer"
            >
              <Image
                src={ministry.image}
                alt={ministry.title}
                fill
                className="object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent" />
              <div className="absolute bottom-0 left-0 right-0 p-6 text-white">
                <h3 className="text-2xl font-bold mb-2">{ministry.title}</h3>
                <p className="text-sm opacity-90">{ministry.description}</p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}
