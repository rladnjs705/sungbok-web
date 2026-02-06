'use client';

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html lang="ko">
      <head>
        <title>서비스 오류 - 성복교회</title>
        <style>{`
          body {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: #ffffff;
            color: #1f2937;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 1rem;
          }
          @media (prefers-color-scheme: dark) {
            body {
              background: #111827;
              color: #f9fafb;
            }
          }
          .container {
            text-align: center;
            max-width: 600px;
          }
          h1 {
            font-size: 2.5rem;
            margin: 0 0 1.5rem 0;
            font-weight: bold;
          }
          p {
            font-size: 1.25rem;
            color: #6b7280;
            margin-bottom: 2.5rem;
            line-height: 1.6;
          }
          @media (prefers-color-scheme: dark) {
            p {
              color: #9ca3af;
            }
          }
          button {
            padding: 0.75rem 2rem;
            font-size: 1rem;
            font-weight: 600;
            color: white;
            background: #3b82f6;
            border: none;
            border-radius: 0.5rem;
            cursor: pointer;
            transition: background 0.2s;
          }
          button:hover {
            background: #2563eb;
          }
        `}</style>
      </head>
      <body>
        <div className="container">
          <h1>서비스 오류가 발생했습니다</h1>
          <p>
            죄송합니다. 서비스에 일시적인 문제가 발생했습니다.<br/>
            페이지를 새로고침하거나 잠시 후 다시 접속해 주세요.
          </p>
          <button onClick={() => window.location.href = '/'}>
            홈으로 이동
          </button>
        </div>
      </body>
    </html>
  );
}
