'use client';

import { ChevronDown, PlayCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';

// ✅ Rendering Performance: Hoist static JSX elements
// These elements never change, so extract them outside the component
const ScrollIndicatorContent = () => (
  <div className="flex flex-col items-center gap-2 text-white/80">
    <span className="text-sm font-medium">스크롤</span>
    <ChevronDown className="h-6 w-6" />
  </div>
);

// ✅ Rendering Performance: Extract static objects to prevent re-creation
const FALLBACK_STYLE = {
  backgroundImage: 'url(/images/hero-fallback.jpg)',
} as const;

const GRAIN_STYLE = {
  backgroundImage: 'url(/textures/grain.png)',
  backgroundSize: '200px',
} as const;

// ✅ Rendering Performance: Extract static stat data
const STATS = [
  {
    id: 'members',
    value: '2,000+',
    label: '교인',
    gradientFrom: 'from-white',
    gradientTo: 'to-blue-200',
  },
  {
    id: 'ministries',
    value: '35+',
    label: '부서',
    gradientFrom: 'from-white',
    gradientTo: 'to-violet-200',
  },
  {
    id: 'missions',
    value: '40+',
    label: '선교지',
    gradientFrom: 'from-white',
    gradientTo: 'to-blue-200',
  },
] as const;

// ✅ Rendering Performance: Extract static components
function StatItem({
  value,
  label,
  gradientFrom,
  gradientTo,
}: {
  value: string;
  label: string;
  gradientFrom: string;
  gradientTo: string;
}) {
  return (
    <div className="group cursor-default transition-transform hover:scale-105">
      <div className="mb-1 text-3xl font-bold md:text-4xl">
        <span
          className={`bg-gradient-to-r ${gradientFrom} ${gradientTo} bg-clip-text text-transparent`}
        >
          {value}
        </span>
      </div>
      <div className="text-sm text-white/80 md:text-base">{label}</div>
    </div>
  );
}

export function HeroSection() {
  return (
    <section className="hero relative min-h-screen overflow-hidden bg-slate-900">
      {/* Video Background */}
      <video
        autoPlay
        muted
        loop
        playsInline
        className="absolute inset-0 h-full w-full object-cover opacity-40 md:opacity-60"
      >
        <source src="/videos/church-worship.mp4" type="video/mp4" />
      </video>

      {/* Fallback Image for devices that don't support video */}
      <div
        className="absolute inset-0 bg-cover bg-center md:hidden"
        style={FALLBACK_STYLE}
      />

      {/* Animated Gradient Overlay */}
      <div className="absolute inset-0 bg-gradient-to-br from-blue-500/80 via-blue-600/70 to-violet-500/60 animate-gradient" />

      {/* Subtle Grain Texture */}
      <div className="absolute inset-0 opacity-[0.03] mix-blend-overlay">
        <div className="h-full w-full animate-grain" style={GRAIN_STYLE} />
      </div>

      {/* Content */}
      <div className="relative z-10 flex min-h-screen items-center justify-center px-4">
        <div className="container mx-auto">
          <div className="mx-auto max-w-4xl text-center text-white">
            {/* Title with staggered animation */}
            <h1 className="mb-6 animate-fade-in-up font-display text-5xl font-bold leading-tight sm:text-6xl md:text-7xl lg:text-8xl [animation-delay:0ms]">
              성복교회에
              <br />
              오신 것을 환영합니다
            </h1>

            {/* Subtitle */}
            <p className="mb-8 animate-fade-in-up text-lg font-light text-white/90 sm:text-xl md:text-2xl [animation-delay:200ms]">
              함께 예배하고, 배우고, 성장하는 공동체
            </p>

            {/* CTA Buttons */}
            <div className="flex animate-fade-in-up flex-col gap-4 sm:flex-row sm:justify-center [animation-delay:400ms]">
              <Button
                size="lg"
                className="group relative overflow-hidden bg-white text-blue-600 transition-all hover:bg-white hover:shadow-2xl hover:shadow-white/20"
              >
                <span className="relative z-10 flex items-center gap-2 font-semibold">
                  <PlayCircle className="h-5 w-5 transition-transform group-hover:scale-110" />
                  온라인 예배 보기
                </span>
                {/* Button hover effect */}
                <div className="absolute inset-0 -z-0 bg-gradient-to-r from-blue-50 to-violet-50 opacity-0 transition-opacity group-hover:opacity-100" />
              </Button>

              <Button
                size="lg"
                variant="outline"
                className="border-2 border-white bg-transparent text-white backdrop-blur-sm transition-all hover:bg-white hover:text-blue-600"
              >
                <span className="font-semibold">교회 소개</span>
              </Button>
            </div>

            {/* Quick Stats */}
            <div className="mt-16 grid animate-fade-in-up grid-cols-3 gap-4 border-t border-white/20 pt-8 text-center [animation-delay:600ms]">
              {STATS.map((stat) => (
                <StatItem
                  key={stat.id}
                  value={stat.value}
                  label={stat.label}
                  gradientFrom={stat.gradientFrom}
                  gradientTo={stat.gradientTo}
                />
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Scroll Indicator */}
      <div className="absolute bottom-8 left-1/2 z-10 -translate-x-1/2 animate-bounce">
        <ScrollIndicatorContent />
      </div>

      {/* Decorative Elements */}
      <div className="pointer-events-none absolute inset-0 z-0">
        {/* Top gradient glow */}
        <div className="absolute -top-40 left-1/2 h-80 w-80 -translate-x-1/2 rounded-full bg-blue-400 opacity-20 blur-3xl" />

        {/* Bottom gradient glow */}
        <div className="absolute -bottom-40 right-1/4 h-80 w-80 rounded-full bg-violet-400 opacity-20 blur-3xl" />
      </div>
    </section>
  );
}
