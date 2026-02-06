'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  const [showDetails, setShowDetails] = useState(false);

  useEffect(() => {
    // 에러 로깅
    console.error('Runtime error:', error);

    // TODO: Phase 6 - Backend API 연동 시 에러 전송
    // logError({ type: 'runtime', message: error.message, stack: error.stack });
  }, [error]);

  const isDev = process.env.NODE_ENV === 'development';

  return (
    <div className="min-h-screen flex items-center justify-center bg-white dark:bg-gray-900 px-4 py-16">
      <div className="text-center max-w-2xl">
        {/* 제목 */}
        <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-gray-900 dark:text-white mb-6">
          일시적인 오류가 발생했습니다
        </h1>

        {/* 설명 */}
        <p className="text-lg md:text-xl text-gray-600 dark:text-gray-400 mb-10 leading-relaxed">
          죄송합니다. 일시적인 문제가 발생했습니다.<br/>
          잠시 후 다시 시도해 주세요.
        </p>

        {/* CTA 버튼 */}
        <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center mb-8">
          <button
            onClick={reset}
            className="inline-flex items-center justify-center px-6 md:px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors dark:bg-blue-700 dark:hover:bg-blue-800"
          >
            다시 시도
          </button>
          <Link
            href="/"
            className="inline-flex items-center justify-center px-6 md:px-8 py-3 border-2 border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 font-semibold rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
          >
            홈으로 돌아가기
          </Link>
        </div>

        {/* 개발 환경: 에러 상세 정보 */}
        {isDev && (
          <div className="mt-8 text-left">
            <button
              onClick={() => setShowDetails(!showDetails)}
              className="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 underline"
            >
              {showDetails ? '에러 상세 정보 숨기기' : '에러 상세 정보 보기'}
            </button>
            {showDetails && (
              <div className="mt-4 p-4 bg-gray-100 dark:bg-gray-800 rounded-lg overflow-auto max-h-96">
                <p className="text-sm font-mono text-red-600 dark:text-red-400 mb-2">
                  {error.message}
                </p>
                <pre className="text-xs text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
                  {error.stack}
                </pre>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
