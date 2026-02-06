'use client';

import { useState, useEffect, useCallback } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { cn } from '@/lib/utils';

const SLIDES = [
  {
    id: 1,
    type: 'image' as const,
    src: '/images/main01.jpg',
    subtitle: 'SUNGBOK CHURCH',
    title: '성복교회에\n오신 것을 환영합니다',
    description: '함께 예배하고, 말씀을 나누며, 그리스도 안에서 성장하는 공동체',
    cta: { text: '교회 소개 보기', href: '/about' },
  },
  {
    id: 2,
    type: 'image' as const,
    src: '/images/main02.jpg',
    subtitle: 'WORSHIP',
    title: '주일예배\n매주 오전 11시',
    description: '말씀과 찬양으로 하나님을 경배합니다',
    cta: { text: '예배 시간 보기', href: '/worship' },
  },
  {
    id: 3,
    type: 'image' as const,
    src: '/images/main03.jpg',
    subtitle: 'COMMUNITY',
    title: '함께 성장하는\n믿음의 공동체',
    description: '35개 부서와 40여 선교지에서 활동하고 있습니다',
    cta: { text: '부서 안내 보기', href: '/ministries' },
  },
  {
    id: 4,
    type: 'video' as const,
    videoId: 'NpjUJd1EoJI',
    subtitle: 'LIVE WORSHIP',
    title: '온라인 예배\n실시간 방송',
    description: '언제 어디서나 함께하는 예배',
    cta: { text: '설교 말씀 보기', href: '/sermons' },
  },
] as const;

export function HeroSlider() {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [autoPlayKey, setAutoPlayKey] = useState(0);

  // ✅ useCallback for performance optimization
  const nextSlide = useCallback(() => {
    setCurrentSlide((prev) => (prev + 1) % SLIDES.length);
    setAutoPlayKey((prev) => prev + 1); // Reset auto-play timer
  }, []);

  const prevSlide = useCallback(() => {
    setCurrentSlide((prev) => (prev - 1 + SLIDES.length) % SLIDES.length);
    setAutoPlayKey((prev) => prev + 1); // Reset auto-play timer
  }, []);

  const goToSlide = useCallback((index: number) => {
    setCurrentSlide(index);
    setAutoPlayKey((prev) => prev + 1); // Reset auto-play timer
  }, []);

  // Auto-advance slides with reset on user interaction
  useEffect(() => {
    const interval = setInterval(nextSlide, 4000); // 4 seconds auto-advance
    return () => clearInterval(interval);
  }, [nextSlide, autoPlayKey]);

  return (
    <section className="relative h-screen w-full overflow-hidden">
      {/* Slides */}
      {SLIDES.map((slide, index) => (
        <div
          key={slide.id}
          className={cn(
            'absolute inset-0 transition-opacity duration-700',
            index === currentSlide ? 'opacity-100 z-10' : 'opacity-0 z-0'
          )}
        >
          {/* Background */}
          {slide.type === 'image' ? (
            <Image
              src={slide.src}
              alt={slide.title}
              fill
              className="object-cover"
              priority={index === 0}
            />
          ) : (
            <iframe
              src={`https://www.youtube.com/embed/${slide.videoId}?autoplay=1&mute=1&loop=1&playlist=${slide.videoId}&controls=0&playsinline=1&vq=hd2160`}
              className="absolute inset-0 w-[177.77vh] h-[56.25vw] min-w-full min-h-full"
              style={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
              }}
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          )}

          {/* Overlay */}
          <div className="absolute inset-0 bg-gradient-to-br from-primary-900/80 via-primary-800/70 to-accent-500/60 dark:from-primary-950/90 dark:via-primary-900/80 dark:to-accent-600/70" />

          {/* Content */}
          <div className="relative z-20 flex h-full items-center justify-center px-4">
            <div className="container mx-auto text-center text-white">
              <p className="mb-4 text-sm font-semibold tracking-wider opacity-90">
                {slide.subtitle}
              </p>
              <h1 className="mb-6 text-5xl md:text-6xl lg:text-7xl font-bold leading-tight whitespace-pre-line">
                {slide.title}
              </h1>
              <p className="mb-8 text-lg md:text-xl opacity-90 max-w-2xl mx-auto">
                {slide.description}
              </p>
              <Link
                href={slide.cta.href}
                className="inline-block px-8 py-4 bg-white text-primary-600 font-semibold rounded-full hover:bg-white/90 transition-colors cursor-pointer dark:bg-gray-800 dark:text-white dark:hover:bg-gray-700"
              >
                {slide.cta.text}
              </Link>
            </div>
          </div>
        </div>
      ))}

      {/* Navigation Arrows */}
      <button
        onClick={prevSlide}
        className="absolute left-4 top-1/2 -translate-y-1/2 z-30 p-3 bg-white/20 hover:bg-white/30 rounded-full text-white transition-colors cursor-pointer"
        aria-label="Previous slide"
      >
        <ChevronLeft className="w-6 h-6" />
      </button>
      <button
        onClick={nextSlide}
        className="absolute right-4 top-1/2 -translate-y-1/2 z-30 p-3 bg-white/20 hover:bg-white/30 rounded-full text-white transition-colors cursor-pointer"
        aria-label="Next slide"
      >
        <ChevronRight className="w-6 h-6" />
      </button>

      {/* Dots */}
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2 z-30 flex gap-3">
        {SLIDES.map((_, index) => (
          <button
            key={index}
            onClick={() => goToSlide(index)}
            className={cn(
              'w-3 h-3 rounded-full transition-all cursor-pointer',
              index === currentSlide
                ? 'bg-white w-8'
                : 'bg-white/50 hover:bg-white/75'
            )}
            aria-label={`Go to slide ${index + 1}`}
          />
        ))}
      </div>
    </section>
  );
}
