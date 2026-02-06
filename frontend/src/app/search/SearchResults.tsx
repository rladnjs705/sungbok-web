'use client';

import { useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { searchNews } from '@/lib/api/news';
import { getSermons } from '@/lib/api/sermons';
import { SearchBar } from '@/components/ui/SearchBar';
import Link from 'next/link';
import Image from 'next/image';
import { useState } from 'react';
import { cn } from '@/lib/utils';

export function SearchResults() {
  const searchParams = useSearchParams();
  const query = searchParams.get('q') || '';
  const [activeTab, setActiveTab] = useState<'all' | 'sermons' | 'news'>('all');

  // 설교 검색
  const { data: sermons = [] } = useQuery({
    queryKey: ['sermons'],
    queryFn: getSermons,
    select: (data) => {
      if (!query) return [];
      const lowerQuery = query.toLowerCase();
      return data.filter(
        (s) =>
          s.title.toLowerCase().includes(lowerQuery) ||
          s.preacher.toLowerCase().includes(lowerQuery) ||
          s.verse.toLowerCase().includes(lowerQuery)
      );
    },
  });

  // 교회소식 검색
  const { data: news = [], isLoading } = useQuery({
    queryKey: ['search-news', query],
    queryFn: () => searchNews(query),
    enabled: query.length > 0,
  });

  const totalResults = sermons.length + news.length;

  const filteredSermons = activeTab === 'all' || activeTab === 'sermons' ? sermons : [];
  const filteredNews = activeTab === 'all' || activeTab === 'news' ? news : [];

  return (
    <div>
      {/* Search Bar */}
      <div className="mb-8">
        <SearchBar placeholder="설교 제목, 목사님 이름, 성경 구절 등을 검색하세요" autoFocus />
      </div>

      {/* Query Info */}
      {query && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold mb-2 dark:text-white">
            "{query}" 검색 결과
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            총 {totalResults}개의 결과를 찾았습니다.
          </p>
        </div>
      )}

      {/* Tabs */}
      {query && totalResults > 0 && (
        <div className="flex gap-3 mb-8 border-b border-gray-200 dark:border-gray-700">
          <button
            onClick={() => setActiveTab('all')}
            className={cn(
              'px-4 py-2 font-semibold transition-colors cursor-pointer border-b-2',
              activeTab === 'all'
                ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
            )}
          >
            전체 ({totalResults})
          </button>
          <button
            onClick={() => setActiveTab('sermons')}
            className={cn(
              'px-4 py-2 font-semibold transition-colors cursor-pointer border-b-2',
              activeTab === 'sermons'
                ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
            )}
          >
            설교 ({sermons.length})
          </button>
          <button
            onClick={() => setActiveTab('news')}
            className={cn(
              'px-4 py-2 font-semibold transition-colors cursor-pointer border-b-2',
              activeTab === 'news'
                ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
            )}
          >
            교회소식 ({news.length})
          </button>
        </div>
      )}

      {/* Loading */}
      {isLoading && (
        <div className="flex items-center justify-center py-16">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
        </div>
      )}

      {/* Empty State */}
      {!isLoading && query && totalResults === 0 && (
        <div className="text-center py-16">
          <svg
            className="w-16 h-16 mx-auto mb-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
          <p className="text-gray-600 dark:text-gray-400 text-lg mb-2">
            검색 결과가 없습니다.
          </p>
          <p className="text-gray-500 dark:text-gray-500 text-sm">
            다른 검색어로 시도해보세요.
          </p>
        </div>
      )}

      {/* Results */}
      {!isLoading && query && (
        <div className="space-y-8">
          {/* Sermons */}
          {filteredSermons.length > 0 && (
            <section>
              {activeTab === 'all' && (
                <h3 className="text-xl font-bold mb-4 dark:text-white">
                  설교 ({sermons.length})
                </h3>
              )}
              <div className="space-y-4">
                {filteredSermons.map((sermon) => (
                  <Link
                    key={sermon.id}
                    href={`/media/sermons/${sermon.id}`}
                    className="block p-4 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 hover:border-primary-500 dark:hover:border-primary-500 hover:shadow-md transition-all cursor-pointer"
                  >
                    <div className="flex gap-4">
                      {sermon.thumbnail && (
                        <div className="relative w-32 h-20 flex-shrink-0 rounded overflow-hidden">
                          <Image
                            src={sermon.thumbnail}
                            alt={sermon.title}
                            fill
                            className="object-cover"
                          />
                        </div>
                      )}
                      <div className="flex-1">
                        <h4 className="font-bold text-lg mb-1 dark:text-white">
                          {highlightQuery(sermon.title, query)}
                        </h4>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                          {sermon.verse} · {sermon.preacher} · {sermon.date}
                        </p>
                        <span className="inline-block px-2 py-1 bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300 rounded text-xs font-semibold">
                          {sermon.category}
                        </span>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}

          {/* News */}
          {filteredNews.length > 0 && (
            <section>
              {activeTab === 'all' && filteredSermons.length > 0 && (
                <h3 className="text-xl font-bold mb-4 dark:text-white">
                  교회소식 ({news.length})
                </h3>
              )}
              <div className="space-y-4">
                {filteredNews.map((item) => (
                  <Link
                    key={item.id}
                    href={`/news/${item.id}`}
                    className="block p-4 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 hover:border-primary-500 dark:hover:border-primary-500 hover:shadow-md transition-all cursor-pointer"
                  >
                    <div className="flex gap-4">
                      {item.image && (
                        <div className="relative w-32 h-20 flex-shrink-0 rounded overflow-hidden">
                          <Image
                            src={item.image}
                            alt={item.title}
                            fill
                            className="object-cover"
                          />
                        </div>
                      )}
                      <div className="flex-1">
                        <h4 className="font-bold text-lg mb-1 dark:text-white">
                          {highlightQuery(item.title, query)}
                        </h4>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-2 line-clamp-2">
                          {highlightQuery(item.excerpt, query)}
                        </p>
                        <div className="flex items-center gap-2">
                          <span className="inline-block px-2 py-1 bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300 rounded text-xs font-semibold">
                            {item.category}
                          </span>
                          <span className="text-xs text-gray-500 dark:text-gray-400">
                            {item.date}
                          </span>
                        </div>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </div>
      )}
    </div>
  );
}

// 검색어 하이라이트 함수
function highlightQuery(text: string, query: string): React.ReactNode {
  if (!query) return text;

  const parts = text.split(new RegExp(`(${query})`, 'gi'));
  return parts.map((part, i) =>
    part.toLowerCase() === query.toLowerCase() ? (
      <mark key={i} className="bg-yellow-200 dark:bg-yellow-600 text-gray-900 dark:text-white">
        {part}
      </mark>
    ) : (
      part
    )
  );
}
