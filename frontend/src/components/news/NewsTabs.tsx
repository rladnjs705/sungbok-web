'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { cn } from '@/lib/utils';

const TABS = ['공지사항', '주보', '행사일정', '사진갤러리'] as const;

const NEWS_ITEMS = [
  {
    id: 1,
    category: '공지사항',
    date: '2024.03.15',
    title: '부활절 연합예배 안내',
    content: '부활절을 맞아 모든 부서가 함께하는 연합예배를 드립니다.',
    image: '/images/news01.jpg',
  },
  {
    id: 2,
    category: '공지사항',
    date: '2024.03.10',
    title: '청년부 수련회',
    content: '3월 22-24일, 청년부 겨울 수련회가 진행됩니다.',
    image: '/images/news02.jpg',
  },
  {
    id: 3,
    category: '주보',
    date: '2024.03.17',
    title: '2024년 3월 셋째주 주보',
    content: '이번주 예배 순서와 교회 소식을 확인하세요.',
    image: '/images/bulletin01.jpg',
  },
  {
    id: 4,
    category: '행사일정',
    date: '2024.03.31',
    title: '부활절 특별예배',
    content: '오전 10시, 본당 3층에서 부활절 특별예배가 있습니다.',
    image: '/images/event01.jpg',
  },
  {
    id: 5,
    category: '사진갤러리',
    date: '2024.03.10',
    title: '청년부 겨울 수련회',
    content: '2박 3일간의 은혜로운 수련회 사진입니다.',
    image: '/images/gallery01.jpg',
  },
] as const;

export function NewsTabs() {
  const [activeTab, setActiveTab] = useState(0);

  const filteredNews = NEWS_ITEMS.filter(
    (item) => item.category === TABS[activeTab]
  );

  return (
    <div>
      {/* Tabs */}
      <div className="flex gap-2 mb-8 overflow-x-auto">
        {TABS.map((tab, index) => (
          <button
            key={tab}
            onClick={() => setActiveTab(index)}
            className={cn(
              'px-6 py-3 rounded-lg font-semibold whitespace-nowrap transition-all cursor-pointer',
              activeTab === index
                ? 'bg-primary-500 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            )}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Content */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredNews.length > 0 ? (
          filteredNews.map((item) => (
            <Link
              key={item.id}
              href={`/news/${item.id}`}
              className="group bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-xl transition-shadow"
            >
              <div className="relative h-48 bg-gradient-to-br from-primary-500 to-accent-500">
                {/* Placeholder for image */}
              </div>
              <div className="p-6">
                <p className="text-sm text-primary-600 font-semibold mb-2">
                  {item.date}
                </p>
                <h3 className="text-lg font-bold text-gray-900 mb-2 group-hover:text-primary-600 transition-colors">
                  {item.title}
                </h3>
                <p className="text-sm text-gray-600 line-clamp-2">
                  {item.content}
                </p>
              </div>
            </Link>
          ))
        ) : (
          <div className="col-span-full text-center py-12 text-gray-500">
            등록된 {TABS[activeTab]}이(가) 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}
