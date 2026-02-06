'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getNews, type NewsItem } from '@/lib/api/news';
import { cn } from '@/lib/utils';
import Link from 'next/link';
import Image from 'next/image';
import { StaggerContainer, StaggerItem } from '@/components/animations';
import { Pagination } from '@/components/ui/Pagination';

const CATEGORIES = ['전체', '공지사항', '주보', '새소식', '행사'] as const;
type Category = (typeof CATEGORIES)[number];

const ITEMS_PER_PAGE = 9;

export function NewsClient() {
  const [selectedCategory, setSelectedCategory] = useState<Category>('전체');
  const [currentPage, setCurrentPage] = useState(1);

  // TanStack Query를 사용한 데이터 fetching
  const { data: news = [], isLoading } = useQuery({
    queryKey: ['news'],
    queryFn: getNews,
    staleTime: 1000 * 60 * 5, // 5분
  });

  // 카테고리 변경 시 페이지 리셋
  const handleCategoryChange = (category: Category) => {
    setSelectedCategory(category);
    setCurrentPage(1);
  };

  // 카테고리 필터링
  const filteredNews =
    selectedCategory === '전체'
      ? news
      : news.filter((item) => item.category === selectedCategory);

  // 페이징 처리
  const totalPages = Math.ceil(filteredNews.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const paginatedNews = filteredNews.slice(startIndex, endIndex);

  // 카테고리별 개수 계산
  const getCategoryCount = (category: Category) => {
    if (category === '전체') return news.length;
    return news.filter((n) => n.category === category).length;
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
      </div>
    );
  }

  return (
    <div>
      {/* Category Tabs */}
      <section className="mb-12">
        <div className="flex flex-wrap justify-center gap-3">
          {CATEGORIES.map((category) => (
            <button
              key={category}
              onClick={() => handleCategoryChange(category)}
              className={cn(
                'px-6 py-3 rounded-full font-semibold transition-all cursor-pointer',
                selectedCategory === category
                  ? 'bg-primary-500 text-white shadow-md'
                  : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'
              )}
            >
              {category}
              <span className="ml-2 text-sm opacity-75">
                ({getCategoryCount(category)})
              </span>
            </button>
          ))}
        </div>
      </section>

      {/* News Grid */}
      <section>
        <h3 className="text-2xl font-bold mb-6 dark:text-white">
          {selectedCategory}{' '}
          <span className="text-gray-500 dark:text-gray-400">
            ({filteredNews.length}개)
          </span>
        </h3>

        {filteredNews.length === 0 ? (
          <div className="text-center py-16">
            <p className="text-gray-500 dark:text-gray-400 text-lg">
              해당 카테고리의 소식이 없습니다.
            </p>
          </div>
        ) : (
          <>
            <StaggerContainer
              key={`${selectedCategory}-${currentPage}`}
              className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12"
            >
              {paginatedNews.map((item) => (
                <StaggerItem key={item.id}>
                  <NewsCard news={item} />
                </StaggerItem>
              ))}
            </StaggerContainer>

            {/* Pagination */}
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
            />
          </>
        )}
      </section>
    </div>
  );
}

interface NewsCardProps {
  news: NewsItem;
}

function NewsCard({ news }: NewsCardProps) {
  return (
    <Link
      href={`/news/${news.id}`}
      className="block group bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-md hover:shadow-xl hover:-translate-y-2 transition-all duration-300 cursor-pointer"
    >
      {news.image && (
        <div className="relative h-48 overflow-hidden">
          <Image
            src={news.image}
            alt={news.title}
            fill
            sizes="(max-width: 768px) 100vw, (max-width: 1024px) 50vw, 33vw"
            className="object-cover group-hover:scale-105 transition-transform duration-300"
          />
          {news.featured && (
            <div className="absolute top-3 right-3 bg-primary-500 text-white px-3 py-1 rounded-full text-xs font-semibold">
              Featured
            </div>
          )}
        </div>
      )}

      <div className="p-5">
        <div className="flex items-center gap-2 mb-3">
          <span
            className={cn(
              'px-3 py-1 rounded-full text-xs font-semibold',
              news.category === '공지사항' &&
                'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300',
              news.category === '주보' &&
                'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300',
              news.category === '새소식' &&
                'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300',
              news.category === '행사' &&
                'bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300'
            )}
          >
            {news.category}
          </span>
          <span className="text-sm text-gray-500 dark:text-gray-400">
            {news.date}
          </span>
        </div>

        <h4 className="font-bold text-lg mb-2 line-clamp-2 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors dark:text-white">
          {news.title}
        </h4>

        <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-3">
          {news.excerpt}
        </p>

        <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400 pt-3 border-t border-gray-100 dark:border-gray-700">
          <span>{news.author}</span>
          {news.views && (
            <span className="flex items-center gap-1">
              <svg
                className="w-4 h-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                />
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                />
              </svg>
              {news.views.toLocaleString()}
            </span>
          )}
        </div>
      </div>
    </Link>
  );
}
