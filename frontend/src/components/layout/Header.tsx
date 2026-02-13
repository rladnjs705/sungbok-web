'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { cn } from '@/lib/utils';
import { ThemeToggle } from '@/components/ui/ThemeToggle';
import { useAuthStore } from '@/store/auth-store';
import { api } from '@/lib/api';

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

interface MeResponse {
  success: boolean;
  email: string;
  name: string;
  role: string;
  provider?: string;
}

export function Header({ transparent = false }: HeaderProps) {
  const pathname = usePathname();
  const router = useRouter();
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  
  // Auth store
  const { user, isAuthenticated, isLoading, login, logout } = useAuthStore();

  // 로그인 상태 확인 (컴포넌트 마운트 시)
  useEffect(() => {
    const checkAuth = async () => {
      try {
        // 이미 인증된 상태면 스킵
        if (useAuthStore.getState().isAuthenticated) {
          useAuthStore.setState({ isLoading: false });
          return;
        }

        const response = await api.get<MeResponse>('/auth/me');
        if (response && response.success) {
          login({
            id: 0,  // 백엔드 응답에 없음
            email: response.email,
            name: response.name,
            role: response.role as 'USER' | 'ADMIN',
            provider: response.provider,
            createdAt: new Date().toISOString(),
          });
        }
      } catch {
        // 인증되지 않은 상태 (401) - 정상적인 상황
        useAuthStore.setState({ isLoading: false, isAuthenticated: false, user: null });
      }
    };

    checkAuth();
  }, [login]);

  // 로그아웃 핸들러
  const handleLogout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      logout();
      router.push('/');
      router.refresh();
    }
  };

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
                  {isLoading ? (
                    // 로딩 중
                    <div className="flex justify-center py-3">
                      <div className="w-6 h-6 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
                    </div>
                  ) : isAuthenticated ? (
                    // 로그인 상태
                    <>
                      <div className="text-center py-2 text-gray-700 dark:text-gray-300">
                        <span className="font-semibold">{user?.name}</span>님 환영합니다
                      </div>
                      <button
                        onClick={() => {
                          handleLogout();
                          setMobileMenuOpen(false);
                        }}
                        className="text-center px-4 py-3 text-base font-semibold text-white bg-red-500 hover:bg-red-600 transition-colors rounded-lg cursor-pointer"
                      >
                        로그아웃
                      </button>
                    </>
                  ) : (
                    // 비로그인 상태
                    <>
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
                    </>
                  )}
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
            
            {isLoading ? (
              // 로딩 중
              <div className="w-6 h-6 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
            ) : isAuthenticated ? (
              // 로그인 상태
              <>
                <span className={cn(
                  'text-sm font-medium hidden lg:inline',
                  isTransparent ? 'text-white' : 'text-gray-700 dark:text-gray-300'
                )}>
                  {user?.name}님
                </span>
                <button
                  onClick={handleLogout}
                  className={cn(
                    'px-4 py-2 text-sm font-semibold transition-colors rounded-lg cursor-pointer',
                    isTransparent
                      ? 'text-white border border-white/50 bg-white/10 hover:bg-white/20'
                      : 'text-red-600 dark:text-red-400 border border-red-300 dark:border-red-600 hover:bg-red-50 dark:hover:bg-red-900/20'
                  )}
                >
                  로그아웃
                </button>
              </>
            ) : (
              // 비로그인 상태
              <>
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
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
