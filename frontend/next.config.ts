import type { NextConfig } from "next";

// Bundle Analyzer 설정
const withBundleAnalyzer = require('@next/bundle-analyzer')({
  enabled: process.env.ANALYZE === 'true',
});

const nextConfig: NextConfig = {

  // 🚀 Partial Prerendering (PPR) - 정적 + 동적 혼합 렌더링
  // Next.js 16의 새로운 기능이지만, 현재 ISR과 충돌
  // 나중에 활성화하려면: cacheComponents: true 설정 후 각 페이지의 revalidate 제거
  // cacheComponents: true,

  // 🖼️ 이미지 최적화 설정
  images: {
    formats: ['image/avif', 'image/webp'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920, 2048, 3840],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
    minimumCacheTTL: 31536000,  // 1년 (immutable images)
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'img.youtube.com',
        pathname: '/vi/**',
      },
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
        pathname: '/**',
      },
    ],
  },

  // 📦 Turbopack은 Next.js 16에서 dev 모드 기본 활성화
  // 별도 설정 불필요

  // 🔒 보안 헤더
  async headers() {
    return [
      {
        source: '/:path*',
        headers: [
          {
            key: 'X-DNS-Prefetch-Control',
            value: 'on',
          },
          {
            key: 'X-Frame-Options',
            value: 'SAMEORIGIN',
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'Referrer-Policy',
            value: 'origin-when-cross-origin',
          },
        ],
      },
    ];
  },

  // ⚡️ 성능 최적화
  reactStrictMode: true,
  poweredByHeader: false,
  compress: true,
};

export default withBundleAnalyzer(nextConfig);
