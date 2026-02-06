import Link from 'next/link';

const CHURCH_MENU = [
  { href: '/about', label: '교회소개' },
  { href: '/worship', label: '예배안내' },
  { href: '/media/sermons', label: '설교말씀' },
] as const;

const COMMUNITY_MENU = [
  { href: '/ministries', label: '다음세대' },
  { href: '/news', label: '교회소식' },
  { href: '/gallery', label: '사진갤러리' },
  { href: '/mission', label: '선교' },
] as const;

export function Footer() {
  return (
    <footer className="bg-gray-800 dark:bg-gray-900 text-gray-300 mt-16 md:mt-20">
      <div className="container mx-auto px-4 py-6 md:py-8">
        {/* Main Footer Content */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6 md:gap-8 mb-4 md:mb-6">
          {/* Church Info */}
          <div>
            <h3 className="text-sm md:text-base font-bold text-white mb-2 md:mb-3">성복교회</h3>
            <div className="space-y-1 md:space-y-1.5 text-xs md:text-sm">
              <p className="flex items-start gap-2">
                <span className="text-gray-500 min-w-[3rem]">주소</span>
                <span>서울특별시 강남구 테헤란로 123</span>
              </p>
              <p className="flex items-start gap-2">
                <span className="text-gray-500 min-w-[3rem]">전화</span>
                <span>02-1234-5678</span>
              </p>
              <p className="flex items-start gap-2">
                <span className="text-gray-500 min-w-[3rem]">팩스</span>
                <span>02-1234-5679</span>
              </p>
              <p className="flex items-start gap-2">
                <span className="text-gray-500 min-w-[3rem]">이메일</span>
                <span>info@sungbok.church</span>
              </p>
            </div>

            {/* Social Links */}
            <div className="flex gap-2 md:gap-3 mt-3 md:mt-4">
              <a
                href="#"
                className="w-8 h-8 flex items-center justify-center bg-gray-800 hover:bg-primary-600 rounded-full transition-colors"
                aria-label="YouTube"
              >
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z" />
                </svg>
              </a>
              <a
                href="#"
                className="w-8 h-8 flex items-center justify-center bg-gray-800 hover:bg-primary-600 rounded-full transition-colors"
                aria-label="Facebook"
              >
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                </svg>
              </a>
              <a
                href="#"
                className="w-8 h-8 flex items-center justify-center bg-gray-800 hover:bg-primary-600 rounded-full transition-colors"
                aria-label="Instagram"
              >
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 0C8.74 0 8.333.015 7.053.072 5.775.132 4.905.333 4.14.63c-.789.306-1.459.717-2.126 1.384S.935 3.35.63 4.14C.333 4.905.131 5.775.072 7.053.012 8.333 0 8.74 0 12s.015 3.667.072 4.947c.06 1.277.261 2.148.558 2.913.306.788.717 1.459 1.384 2.126.667.666 1.336 1.079 2.126 1.384.766.296 1.636.499 2.913.558C8.333 23.988 8.74 24 12 24s3.667-.015 4.947-.072c1.277-.06 2.148-.262 2.913-.558.788-.306 1.459-.718 2.126-1.384.666-.667 1.079-1.335 1.384-2.126.296-.765.499-1.636.558-2.913.06-1.28.072-1.687.072-4.947s-.015-3.667-.072-4.947c-.06-1.277-.262-2.149-.558-2.913-.306-.789-.718-1.459-1.384-2.126C21.319 1.347 20.651.935 19.86.63c-.765-.297-1.636-.499-2.913-.558C15.667.012 15.26 0 12 0zm0 2.16c3.203 0 3.585.016 4.85.071 1.17.055 1.805.249 2.227.415.562.217.96.477 1.382.896.419.42.679.819.896 1.381.164.422.36 1.057.413 2.227.057 1.266.07 1.646.07 4.85s-.015 3.585-.074 4.85c-.061 1.17-.256 1.805-.421 2.227-.224.562-.479.96-.899 1.382-.419.419-.824.679-1.38.896-.42.164-1.065.36-2.235.413-1.274.057-1.649.07-4.859.07-3.211 0-3.586-.015-4.859-.074-1.171-.061-1.816-.256-2.236-.421-.569-.224-.96-.479-1.379-.899-.421-.419-.69-.824-.9-1.38-.165-.42-.359-1.065-.42-2.235-.045-1.26-.061-1.649-.061-4.844 0-3.196.016-3.586.061-4.861.061-1.17.255-1.814.42-2.234.21-.57.479-.96.9-1.381.419-.419.81-.689 1.379-.898.42-.166 1.051-.361 2.221-.421 1.275-.045 1.65-.06 4.859-.06l.045.03zm0 3.678c-3.405 0-6.162 2.76-6.162 6.162 0 3.405 2.76 6.162 6.162 6.162 3.405 0 6.162-2.76 6.162-6.162 0-3.405-2.76-6.162-6.162-6.162zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm7.846-10.405c0 .795-.646 1.44-1.44 1.44-.795 0-1.44-.646-1.44-1.44 0-.794.646-1.439 1.44-1.439.793-.001 1.44.645 1.44 1.439z" />
                </svg>
              </a>
            </div>
          </div>

          {/* Church Menu - Hidden on mobile */}
          <div className="hidden md:block">
            <h4 className="text-base font-semibold text-white mb-3">교회안내</h4>
            <ul className="space-y-1.5 text-sm">
              {CHURCH_MENU.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="hover:text-white hover:translate-x-1 inline-block transition-all"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Community Menu - Hidden on mobile */}
          <div className="hidden md:block">
            <h4 className="text-base font-semibold text-white mb-3">커뮤니티</h4>
            <ul className="space-y-1.5 text-sm">
              {COMMUNITY_MENU.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="hover:text-white hover:translate-x-1 inline-block transition-all"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Worship Schedule */}
          <div>
            <h4 className="text-sm md:text-base font-semibold text-white mb-2 md:mb-3">예배시간</h4>
            <ul className="space-y-1 md:space-y-1.5 text-xs md:text-sm">
              <li className="flex items-center gap-3">
                <span className="text-gray-500 min-w-[4rem]">주일 1부</span>
                <span>오전 9:00</span>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-gray-500 min-w-[4rem]">주일 2부</span>
                <span>오전 11:00</span>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-gray-500 min-w-[4rem]">수요예배</span>
                <span>오후 7:30</span>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-gray-500 min-w-[4rem]">금요기도</span>
                <span>오후 8:00</span>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-gray-500 min-w-[4rem]">새벽기도</span>
                <span>오전 5:30</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-800 pt-3 md:pt-4 flex flex-col md:flex-row justify-between items-center gap-2 text-xs text-gray-500">
          <p>&copy; 성복교회. All rights reserved.</p>
          <div className="flex gap-4">
            <Link href="/about" className="hover:text-white transition-colors">
              개인정보처리방침
            </Link>
            <Link href="/about" className="hover:text-white transition-colors">
              이용약관
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
