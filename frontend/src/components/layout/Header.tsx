'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { ThemeToggle } from '@/components/ui/ThemeToggle';

interface HeaderProps {
  transparent?: boolean;
}

const NAV_LINKS = [
  { href: '/about', label: '교회소개' },
  { href: '/worship', label: '예배안내' },
  { href: '/sermons', label: '예배' },
  { href: '/ministries', label: '다음세대' },
  { href: '/news', label: '교회소식' },
  { href: '/mission', label: '선교·사역' },
] as const;

export function Header({ transparent = false }: HeaderProps) {
  const pathname = usePathname();
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // 로고 클릭 핸들러: 메인 페이지에서는 최상단으로 스크롤
  const handleLogoClick = (e: React.MouseEvent<HTMLAnchorElement>) => {
    if (pathname === '/') {
      e.preventDefault();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  useEffect(() => {
    if (!transparent) return;

    const handleScroll = () => {
      setScrolled(window.scrollY > 100);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [transparent]);

  // Lock body scroll when mobile menu is open
  useEffect(() => {
    if (mobileMenuOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [mobileMenuOpen]);

  const isTransparent = transparent && !scrolled;

  return (
    <header
      className={cn(
        'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
        isTransparent
          ? 'bg-transparent'
          : 'bg-white dark:bg-gray-900 shadow-sm dark:shadow-gray-800'
      )}
    >
      <div className="container mx-auto px-4">
        {/* Mobile Layout */}
        <div className="flex md:hidden h-20 items-center justify-between">
          {/* Logo */}
          <Link
            href="/"
            onClick={handleLogoClick}
            className={cn(
              'text-xl font-bold transition-colors cursor-pointer',
              isTransparent ? 'text-white' : 'text-primary-500 dark:text-primary-400'
            )}
          >
            성복교회
          </Link>

          {/* Mobile Actions */}
          <div className="flex items-center gap-2">
            <ThemeToggle />
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className={cn(
                'p-2 rounded-lg transition-colors',
                isTransparent
                  ? 'text-white hover:bg-white/10'
                  : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
              )}
              aria-label="메뉴"
            >
              <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                {mobileMenuOpen ? (
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                ) : (
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M4 6h16M4 12h16M4 18h16"
                  />
                )}
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Menu Overlay */}
        {mobileMenuOpen && (
          <div className="md:hidden fixed inset-0 top-20 bg-white dark:bg-gray-900 z-40 overflow-y-auto animate-in slide-in-from-top-4 fade-in duration-300">
            <nav className="container mx-auto px-4 py-6">
              <div className="flex flex-col gap-4">
                {NAV_LINKS.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    onClick={() => setMobileMenuOpen(false)}
                    className="text-lg font-medium text-gray-700 dark:text-gray-300 hover:text-primary-500 dark:hover:text-primary-400 py-3 border-b border-gray-200 dark:border-gray-700 transition-colors cursor-pointer"
                  >
                    {link.label}
                  </Link>
                ))}

                {/* Mobile Auth Buttons */}
                <div className="flex flex-col gap-3 pt-4">
                  <Link
                    href="/login"
                    onClick={() => setMobileMenuOpen(false)}
                    className="text-center px-4 py-3 text-base font-semibold text-gray-700 dark:text-gray-300 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors rounded-lg cursor-pointer"
                  >
                    로그인
                  </Link>
                  <Link
                    href="/register"
                    onClick={() => setMobileMenuOpen(false)}
                    className="text-center px-4 py-3 text-base font-semibold text-white bg-primary-500 hover:bg-primary-600 transition-colors rounded-lg cursor-pointer"
                  >
                    회원가입
                  </Link>
                </div>
              </div>
            </nav>
          </div>
        )}

        {/* Desktop Layout */}
        <div className="hidden md:grid grid-cols-3 h-20 items-center">
          {/* Logo */}
          <Link
            href="/"
            onClick={handleLogoClick}
            className={cn(
              'text-2xl font-bold transition-colors cursor-pointer',
              isTransparent ? 'text-white' : 'text-primary-500 dark:text-primary-400'
            )}
          >
            성복교회
          </Link>

          {/* Navigation - Center aligned */}
          <nav className="flex items-center justify-center gap-8">
            {NAV_LINKS.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={cn(
                  'text-base font-medium transition-colors hover:opacity-75 cursor-pointer',
                  isTransparent ? 'text-white' : 'text-gray-700 dark:text-gray-300'
                )}
              >
                {link.label}
              </Link>
            ))}
          </nav>

          {/* Desktop Actions - Right aligned */}
          <div className="flex items-center justify-end gap-3">
            <ThemeToggle />
            <Link
              href="/login"
              className={cn(
                'px-4 py-2 text-sm font-semibold transition-colors rounded-lg cursor-pointer',
                isTransparent
                  ? 'text-white border border-white/50 bg-white/10 hover:bg-white/20'
                  : 'text-gray-700 dark:text-gray-300 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-800'
              )}
            >
              로그인
            </Link>
            <Link
              href="/register"
              className="px-4 py-2 text-sm font-semibold text-white bg-primary-500 hover:bg-primary-600 transition-colors rounded-lg cursor-pointer"
            >
              회원가입
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
}
