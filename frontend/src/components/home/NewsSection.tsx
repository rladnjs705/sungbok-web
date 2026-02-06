'use client';

import Image from 'next/image';
import Link from 'next/link';
import { StaggerContainer, StaggerItem, ScrollReveal } from '@/components/animations';

const NEWS_ITEMS = [
  {
    id: 1,
    image: '/images/news01.jpg',
    date: '2024.03.15',
    title: '부활절 연합예배 안내',
    description: '부활절을 맞아 모든 부서가 함께하는 연합예배를 드립니다.',
    href: '/news/1',
  },
  {
    id: 2,
    image: '/images/news02.jpg',
    date: '2024.03.10',
    title: '청년부 수련회',
    description: '3월 22-24일, 청년부 겨울 수련회가 진행됩니다.',
    href: '/news/2',
  },
  {
    id: 3,
    image: '/images/news03.jpg',
    date: '2024.03.05',
    title: '새가족 환영',
    description: '3월 첫째 주 새가족을 진심으로 환영합니다.',
    href: '/news/3',
  },
] as const;

export function NewsSection() {
  return (
    <section className="py-20 bg-white">
      <div className="container mx-auto px-4">
        {/* Header */}
        <ScrollReveal className="text-center mb-12">
          <p className="text-sm text-primary-500 font-semibold tracking-wider mb-2">
            News & Events
          </p>
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900">
            교회 소식
          </h2>
        </ScrollReveal>

        {/* Grid */}
        <StaggerContainer className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {NEWS_ITEMS.map((news) => (
            <StaggerItem key={news.id}>
              <Link
                href={news.href}
                className="block group bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-xl hover:-translate-y-2 transition-all duration-300 cursor-pointer"
              >
                <div className="relative h-56">
                  <Image
                    src={news.image}
                    alt={news.title}
                    fill
                    className="object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                </div>
                <div className="p-6">
                  <p className="text-sm text-gray-500 mb-2">{news.date}</p>
                  <h3 className="text-xl font-bold text-gray-900 mb-3 group-hover:text-primary-600 transition-colors">
                    {news.title}
                  </h3>
                  <p className="text-gray-700 line-clamp-2">{news.description}</p>
                </div>
              </Link>
            </StaggerItem>
          ))}
        </StaggerContainer>
      </div>
    </section>
  );
}
