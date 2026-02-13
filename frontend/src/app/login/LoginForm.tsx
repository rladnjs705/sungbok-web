'use client';

import { useForm } from '@tanstack/react-form';
import { zodValidator } from '@tanstack/zod-form-adapter';
import { z } from 'zod';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { api, ApiError } from '@/lib/api';
import { useAuthStore } from '@/store/auth-store';

interface LoginRequest {
  username: string;  // 백엔드는 username 필드 사용
  password: string;
}

interface LoginResponse {
  success: boolean;
  message: string;
  email: string;
  name: string;
}

const loginSchema = z.object({
  email: z.string().email('올바른 이메일 주소를 입력해주세요'),
  password: z.string().min(8, '비밀번호는 최소 8자 이상이어야 합니다'),
  rememberMe: z.boolean().optional(),
});

export function LoginForm() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  const form = useForm({
    defaultValues: {
      email: '',
      password: '',
      rememberMe: false,
    },
    onSubmit: async ({ value }) => {
      setIsSubmitting(true);
      setErrorMessage(null);

      try {
        const response = await api.post<LoginResponse>('/auth/login', {
          username: value.email,  // 백엔드는 username 필드 사용 (email과 동일)
          password: value.password,
        } as LoginRequest);

        // 스토어에 사용자 정보 저장 (백엔드 응답에서 id는 없으므로 임시로 0)
        login({
          id: 0,  // 백엔드 응답에 없음
          email: response.email,
          name: response.name,
          role: 'USER',  // 백엔드 응답에 없으면 기본값
          createdAt: new Date().toISOString(),
        });

        // 로그인 성공 후 홈으로 이동
        router.push('/');
        router.refresh();
      } catch (error) {
        console.error('Login error:', error);
        if (error instanceof ApiError) {
          if (error.status === 401) {
            setErrorMessage('이메일 또는 비밀번호가 올바르지 않습니다.');
          } else if (error.status === 403) {
            setErrorMessage('계정이 비활성화되었습니다. 관리자에게 문의하세요.');
          } else {
            setErrorMessage('로그인에 실패했습니다. 잠시 후 다시 시도해주세요.');
          }
        } else {
          setErrorMessage('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        }
      } finally {
        setIsSubmitting(false);
      }
    },
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        form.handleSubmit();
      }}
      className="space-y-6"
    >
      {/* Email Field */}
      <form.Field
        name="email"
        validators={{
          onChange: ({ value }) => {
            const result = z.string().email('올바른 이메일 주소를 입력해주세요').safeParse(value);
            if (!result.success) {
              return result.error.issues[0].message;
            }
            return undefined;
          },
        }}
      >
        {(field) => (
          <div>
            <label
              htmlFor={field.name}
              className="block text-sm font-semibold text-gray-700 mb-2"
            >
              이메일
            </label>
            <input
              id={field.name}
              type="email"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="email@example.com"
            />
            {field.state.meta.errors.length > 0 && (
              <p className="mt-1 text-sm text-red-600">
                {field.state.meta.errors[0]}
              </p>
            )}
          </div>
        )}
      </form.Field>

      {/* Password Field */}
      <form.Field
        name="password"
        validators={{
          onChange: ({ value }) => {
            const result = z.string().min(8, '비밀번호는 최소 8자 이상이어야 합니다').safeParse(value);
            if (!result.success) {
              return result.error.issues[0].message;
            }
            return undefined;
          },
        }}
      >
        {(field) => (
          <div>
            <label
              htmlFor={field.name}
              className="block text-sm font-semibold text-gray-700 mb-2"
            >
              비밀번호
            </label>
            <input
              id={field.name}
              type="password"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="••••••••"
            />
            {field.state.meta.errors.length > 0 && (
              <p className="mt-1 text-sm text-red-600">
                {field.state.meta.errors[0]}
              </p>
            )}
          </div>
        )}
      </form.Field>

      {/* Remember Me & Forgot Password */}
      <div className="flex items-center justify-between text-sm">
        <form.Field name="rememberMe">
          {(field) => (
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={field.state.value}
                onChange={(e) => field.handleChange(e.target.checked)}
                className="w-4 h-4 rounded border-gray-300 text-primary-500 focus:ring-primary-500"
              />
              <span className="text-gray-600">로그인 상태 유지</span>
            </label>
          )}
        </form.Field>
        <button
          type="button"
          onClick={() => alert('비밀번호 찾기 기능은 개발 중입니다.\n교회 사무실(02-1234-5678)로 문의해주세요.')}
          className="text-primary-600 hover:text-primary-700 font-semibold cursor-pointer"
        >
          비밀번호 찾기
        </button>
      </div>

      {/* Error Message */}
      {errorMessage && (
        <div className="p-3 rounded-lg bg-red-50 border border-red-200">
          <p className="text-sm text-red-600 text-center">{errorMessage}</p>
        </div>
      )}

      {/* Submit Button */}
      <form.Subscribe
        selector={(state) => [state.canSubmit, state.isSubmitting]}
      >
        {([canSubmit, isFormSubmitting]) => (
          <button
            type="submit"
            disabled={!canSubmit || isSubmitting || isFormSubmitting}
            className="w-full bg-primary-500 hover:bg-primary-600 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-bold py-3 rounded-lg transition-colors cursor-pointer"
          >
            {isSubmitting || isFormSubmitting ? '로그인 중...' : '로그인'}
          </button>
        )}
      </form.Subscribe>
    </form>
  );
}
