import type { Metadata } from "next";
import { Cormorant_Garamond, Lora } from "next/font/google";
import localFont from "next/font/local";
import { Analytics } from '@vercel/analytics/next';
import { SpeedInsights } from '@vercel/speed-insights/next';
import { QueryProvider } from '@/providers/QueryProvider';
import { ThemeProvider } from '@/providers/ThemeProvider';
import "./globals.css";

// Paperlogy - Main Korean font (Thin ~ Black)
const paperlogy = localFont({
  src: [
    { path: "../../public/fonts/paperlogy/Paperlogy-1Thin.ttf", weight: "100", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-2ExtraLight.ttf", weight: "200", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-3Light.ttf", weight: "300", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-4Regular.ttf", weight: "400", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-5Medium.ttf", weight: "500", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-6SemiBold.ttf", weight: "600", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-7Bold.ttf", weight: "700", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-8ExtraBold.ttf", weight: "800", style: "normal" },
    { path: "../../public/fonts/paperlogy/Paperlogy-9Black.ttf", weight: "900", style: "normal" },
  ],
  variable: "--font-paperlogy",
  display: "swap",
  preload: true,
});

// Serif font for about/intro sections (elegant, trustworthy) - 유지
const cormorantGaramond = Cormorant_Garamond({
  variable: "--font-serif",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  style: ["normal", "italic"],
});

// Serif body font for long-form content - 유지
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
      <body
        className={`${paperlogy.variable} ${cormorantGaramond.variable} ${lora.variable} font-sans antialiased`}
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
