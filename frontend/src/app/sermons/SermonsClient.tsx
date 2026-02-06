'use client';

import { useState, useEffect, useRef } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'next/navigation';
import Image from 'next/image';
import { PlayCircle } from 'lucide-react';
import { getSermons, type Sermon } from '@/lib/api/sermons';
import { cn } from '@/lib/utils';
import { AnimatedCard, StaggerContainer, StaggerItem } from '@/components/animations';
import { Pagination } from '@/components/ui/Pagination';

const CATEGORIES = [
  '전체',
  '주일예배',
  '금요생수의강',
  '월삭예배',
  '특별집회',
  '수요예배',
  '새벽예배',
  '주일5부',
] as const;

type Category = (typeof CATEGORIES)[number];

const ITEMS_PER_PAGE = 9;

export function SermonsClient() {
  const searchParams = useSearchParams();
  const selectedId = searchParams.get('selected');
  const playerRef = useRef<HTMLElement>(null);

  const [selectedCategory, setSelectedCategory] = useState<Category>('전체');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedSermon, setSelectedSermon] = useState<Sermon | null>(null);
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  // TanStack Query를 사용한 데이터 fetching
  const { data: sermons = [], isLoading } = useQuery({
    queryKey: ['sermons'],
    queryFn: getSermons,
    staleTime: 1000 * 60 * 5, // 5분
  });

  // 초기 설교 설정 (URL 파라미터 또는 최신 설교)
  useEffect(() => {
    if (sermons.length > 0 && !selectedSermon) {
      if (selectedId) {
        const sermon = sermons.find(s => s.id === parseInt(selectedId));
        setSelectedSermon(sermon || sermons[0]);
      } else {
        setSelectedSermon(sermons[0]);
      }
      setIsInitialLoad(false);
    }
  }, [sermons, selectedId, selectedSermon]);

  // 설교 선택 시 플레이어로 스크롤 (초기 로드 제외)
  useEffect(() => {
    if (!isInitialLoad && selectedSermon && playerRef.current) {
      playerRef.current.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      });
    }
  }, [selectedSermon, isInitialLoad]);

  // 카테고리 변경 시 페이지 리셋
  const handleCategoryChange = (category: Category) => {
    setSelectedCategory(category);
    setCurrentPage(1);
  };

  // 카테고리 필터링
  const filteredSermons = selectedCategory === '전체'
    ? sermons
    : sermons.filter((sermon) => sermon.category === selectedCategory);

  // 페이징 처리
  const totalPages = Math.ceil(filteredSermons.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const paginatedSermons = filteredSermons.slice(startIndex, endIndex);

  // 카테고리별 개수 계산
  const getCategoryCount = (category: Category) => {
    if (category === '전체') return sermons.length;
    return sermons.filter((s) => s.category === category).length;
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-16">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-16">
      {/* Selected Sermon Player */}
      {selectedSermon && (
        <section ref={playerRef} className="mb-16">
          <h2 className="text-3xl font-bold mb-8 dark:text-white">
            {selectedId ? '선택한 설교' : '최신 설교'}
          </h2>
          <div className="bg-white dark:bg-gray-800 rounded-2xl overflow-hidden shadow-lg">
            <div className="aspect-video">
              <iframe
                key={selectedSermon.videoId}
                src={`https://www.youtube.com/embed/${selectedSermon.videoId}?rel=0&autoplay=1`}
                title={selectedSermon.title}
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                className="w-full h-full"
              />
            </div>
            <div className="p-6">
              <div className="flex items-center gap-3 mb-3">
                <span className="px-3 py-1 bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300 rounded-full text-sm font-semibold">
                  {selectedSermon.category}
                </span>
                <span className="text-gray-500 dark:text-gray-400 text-sm">{selectedSermon.date}</span>
              </div>
              <h3 className="text-3xl font-bold mb-2 dark:text-white">{selectedSermon.title}</h3>
              <p className="text-gray-600 dark:text-gray-400 mb-2">{selectedSermon.verse}</p>
              <p className="text-gray-700 dark:text-gray-300 font-medium">{selectedSermon.preacher}</p>
            </div>
          </div>
        </section>
      )}

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
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700'
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

      {/* Sermon Grid */}
      <section>
        <h3 className="text-2xl font-bold mb-6 dark:text-white">
          {selectedCategory} <span className="text-gray-500 dark:text-gray-400">({filteredSermons.length}개)</span>
        </h3>

        {filteredSermons.length === 0 ? (
          <div className="text-center py-16">
            <p className="text-gray-500 text-lg">해당 카테고리의 설교가 없습니다.</p>
          </div>
        ) : (
          <>
            <StaggerContainer
              key={`${selectedCategory}-${currentPage}`}
              className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12"
            >
              {paginatedSermons.map((sermon, index) => (
                <StaggerItem key={sermon.id}>
                  <SermonCard sermon={sermon} onClick={setSelectedSermon} />
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

interface SermonCardProps {
  sermon: Sermon;
  onClick: (sermon: Sermon) => void;
}

function SermonCard({ sermon, onClick }: SermonCardProps) {
  return (
    <button
      onClick={() => onClick(sermon)}
      className="w-full text-left bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-shadow cursor-pointer group"
    >
      <div className="relative aspect-video overflow-hidden">
        <Image
          src={`https://img.youtube.com/vi/${sermon.videoId}/hqdefault.jpg`}
          alt={sermon.title}
          fill
          className="object-cover group-hover:scale-105 transition-transform duration-300"
        />
        {/* Play button overlay */}
        <div className="absolute inset-0 bg-black/20 group-hover:bg-black/30 transition-colors flex items-center justify-center">
          <div className="w-16 h-16 bg-white/90 rounded-full flex items-center justify-center group-hover:scale-110 transition-transform">
            <PlayCircle className="w-8 h-8 text-primary-600" />
          </div>
        </div>
        {sermon.duration && (
          <div className="absolute bottom-2 right-2 bg-black/80 text-white px-2 py-1 rounded text-xs font-semibold">
            {sermon.duration}
          </div>
        )}
      </div>

      <div className="p-4">
        <div className="flex items-center gap-2 mb-2">
          <span className="px-2 py-1 bg-primary-100 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300 rounded text-xs font-semibold">
            {sermon.category}
          </span>
          <span className="text-gray-500 dark:text-gray-400 text-xs">{sermon.date}</span>
        </div>

        <h4 className="font-bold text-lg mb-2 line-clamp-2 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors dark:text-white">
          {sermon.title}
        </h4>

        <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{sermon.verse}</p>
        <p className="text-sm text-gray-700 dark:text-gray-300 font-medium">{sermon.preacher}</p>

        {sermon.views && (
          <div className="mt-3 pt-3 border-t border-gray-100 dark:border-gray-700 flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
            <span className="flex items-center gap-1">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              {sermon.views.toLocaleString()}
            </span>
          </div>
        )}
      </div>
    </button>
  );
}
