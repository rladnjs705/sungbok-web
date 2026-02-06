import type { Metadata } from "next";
import { Geist, Geist_Mono, Unbounded, Cormorant_Garamond, Lora } from "next/font/google";
import { Analytics } from '@vercel/analytics/next';
import { SpeedInsights } from '@vercel/speed-insights/next';
import { QueryProvider } from '@/providers/QueryProvider';
import { ThemeProvider } from '@/providers/ThemeProvider';
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

// Display font for hero titles (modern, geometric)
const unbounded = Unbounded({
  variable: "--font-display",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800", "900"],
});

// Serif font for about/intro sections (elegant, trustworthy)
const cormorantGaramond = Cormorant_Garamond({
  variable: "--font-serif",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  style: ["normal", "italic"],
});

// Serif body font for long-form content
const lora = Lora({
  variable: "--font-serif-body",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  style: ["normal", "italic"],
});

export const metadata: Metadata = {
  title: "성복교회 | 함께 예배하고, 배우고, 성장하는 공동체",
  description:
    "성복교회는 하나님의 말씀으로 세워지고, 사랑으로 하나 되며, 세상을 향해 빛과 소금의 역할을 감당하는 교회입니다.",
  keywords: ["성복교회", "교회", "예배", "설교", "온라인 예배", "성경 공부"],
  openGraph: {
    title: "성복교회",
    description: "함께 예배하고, 배우고, 성장하는 공동체",
    type: "website",
    locale: "ko_KR",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <head>
        {/* Pretendard Variable Font (Korean body font) */}
        <link
          rel="stylesheet"
          as="style"
          crossOrigin="anonymous"
          href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/variable/pretendardvariable-dynamic-subset.min.css"
        />
      </head>
      <body
        className={`${geistSans.variable} ${geistMono.variable} ${unbounded.variable} ${cormorantGaramond.variable} ${lora.variable} font-sans antialiased`}
      >
        <ThemeProvider>
          <QueryProvider>
            {children}
          </QueryProvider>
        </ThemeProvider>
        <Analytics />
        <SpeedInsights />
      </body>
    </html>
  );
}
