'use client';

import { useForm } from '@tanstack/react-form';
import { zodValidator } from '@tanstack/zod-form-adapter';
import { z } from 'zod';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { api, ApiError } from '@/lib/api';

interface RegisterRequest {
  username: string;  // 백엔드는 username 필드 사용 (email과 동일하게 사용)
  password: string;
  name: string;
  email: string;
  phone?: string;
}

interface RegisterResponse {
  success: boolean;
  message: string;
  email: string;
  name: string;
}

const registerSchema = z.object({
  name: z.string().min(2, '이름은 최소 2자 이상이어야 합니다'),
  email: z.string().email('올바른 이메일 주소를 입력해주세요'),
  password: z
    .string()
    .min(8, '비밀번호는 최소 8자 이상이어야 합니다')
    .regex(/[A-Z]/, '대문자를 최소 1개 포함해야 합니다')
    .regex(/[a-z]/, '소문자를 최소 1개 포함해야 합니다')
    .regex(/[0-9]/, '숫자를 최소 1개 포함해야 합니다'),
  passwordConfirm: z.string(),
  phone: z.string().optional(),
  agreeTerms: z.boolean().refine((val) => val === true, {
    message: '이용약관에 동의해주세요',
  }),
  agreePrivacy: z.boolean().refine((val) => val === true, {
    message: '개인정보 처리방침에 동의해주세요',
  }),
  agreeMarketing: z.boolean().optional(),
});

export function RegisterForm() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const router = useRouter();

  const form = useForm({
    defaultValues: {
      name: '',
      email: '',
      password: '',
      passwordConfirm: '',
      phone: '',
      agreeTerms: false,
      agreePrivacy: false,
      agreeMarketing: false,
    },
    onSubmit: async ({ value }) => {
      setIsSubmitting(true);
      setErrorMessage(null);
      setSuccessMessage(null);

      try {
        // 비밀번호 확인 검증
        if (value.password !== value.passwordConfirm) {
          setErrorMessage('비밀번호가 일치하지 않습니다.');
          setIsSubmitting(false);
          return;
        }

        // username은 email과 동일하게 사용 (백엔드 요구사항)
        const response = await api.post<RegisterResponse>('/auth/register', {
          username: value.email,
          password: value.password,
          name: value.name,
          email: value.email,
          phone: value.phone || undefined,
        } as RegisterRequest);

        setSuccessMessage('회원가입이 완료되었습니다! 로그인 페이지로 이동합니다...');
        
        // 2초 후 로그인 페이지로 이동
        setTimeout(() => {
          router.push('/login');
        }, 2000);
      } catch (error) {
        console.error('Register error:', error);
        if (error instanceof ApiError) {
          if (error.status === 409) {
            setErrorMessage('이미 등록된 이메일 주소입니다.');
          } else if (error.status === 400) {
            setErrorMessage('입력하신 정보를 다시 확인해주세요.');
          } else {
            setErrorMessage('회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.');
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
      {/* Name Field */}
      <form.Field
        name="name"
        validators={{
          onChange: ({ value }) => {
            const result = z.string().min(2, '이름은 최소 2자 이상이어야 합니다').safeParse(value);
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
              className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2"
            >
              이름 <span className="text-red-500">*</span>
            </label>
            <input
              id={field.name}
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="홍길동"
            />
            {field.state.meta.errors.length > 0 && (
              <p className="mt-1 text-sm text-red-600">
                {field.state.meta.errors[0]}
              </p>
            )}
          </div>
        )}
      </form.Field>

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
              className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2"
            >
              이메일 <span className="text-red-500">*</span>
            </label>
            <input
              id={field.name}
              type="email"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
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
            const result = z
              .string()
              .min(8, '비밀번호는 최소 8자 이상이어야 합니다')
              .regex(/[A-Z]/, '대문자를 최소 1개 포함해야 합니다')
              .regex(/[a-z]/, '소문자를 최소 1개 포함해야 합니다')
              .regex(/[0-9]/, '숫자를 최소 1개 포함해야 합니다')
              .safeParse(value);
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
              className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2"
            >
              비밀번호 <span className="text-red-500">*</span>
            </label>
            <input
              id={field.name}
              type="password"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="••••••••"
            />
            {field.state.meta.errors.length > 0 && (
              <p className="mt-1 text-sm text-red-600">
                {field.state.meta.errors[0]}
              </p>
            )}
            <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">
              8자 이상, 대문자, 소문자, 숫자 포함
            </p>
          </div>
        )}
      </form.Field>

      {/* Password Confirm Field */}
      <form.Field name="passwordConfirm">
        {(field) => (
          <div>
            <label
              htmlFor={field.name}
              className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2"
            >
              비밀번호 확인 <span className="text-red-500">*</span>
            </label>
            <input
              id={field.name}
              type="password"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="••••••••"
            />
          </div>
        )}
      </form.Field>

      {/* Phone Field (Optional) */}
      <form.Field name="phone">
        {(field) => (
          <div>
            <label
              htmlFor={field.name}
              className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2"
            >
              전화번호 <span className="text-gray-400 text-xs">(선택)</span>
            </label>
            <input
              id={field.name}
              type="tel"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              placeholder="010-1234-5678"
            />
          </div>
        )}
      </form.Field>

      {/* Terms Agreement */}
      <div className="space-y-3 pt-4 border-t border-gray-200 dark:border-gray-700">
        <form.Field
          name="agreeTerms"
          validators={{
            onChange: ({ value }) => {
              if (!value) {
                return '이용약관에 동의해주세요';
              }
              return undefined;
            },
          }}
        >
          {(field) => (
            <div>
              <label className="flex items-start gap-2 cursor-pointer group">
                <input
                  type="checkbox"
                  checked={field.state.value}
                  onChange={(e) => field.handleChange(e.target.checked)}
                  className="w-5 h-5 mt-0.5 rounded border-gray-300 text-primary-500 focus:ring-primary-500 cursor-pointer"
                />
                <span className="text-sm text-gray-700 dark:text-gray-300 group-hover:text-gray-900 dark:group-hover:text-white">
                  <span className="text-red-500">* </span>
                  이용약관에 동의합니다
                </span>
              </label>
              {field.state.meta.errors.length > 0 && (
                <p className="mt-1 ml-7 text-sm text-red-600">
                  {field.state.meta.errors[0]}
                </p>
              )}
            </div>
          )}
        </form.Field>

        <form.Field
          name="agreePrivacy"
          validators={{
            onChange: ({ value }) => {
              if (!value) {
                return '개인정보 처리방침에 동의해주세요';
              }
              return undefined;
            },
          }}
        >
          {(field) => (
            <div>
              <label className="flex items-start gap-2 cursor-pointer group">
                <input
                  type="checkbox"
                  checked={field.state.value}
                  onChange={(e) => field.handleChange(e.target.checked)}
                  className="w-5 h-5 mt-0.5 rounded border-gray-300 text-primary-500 focus:ring-primary-500 cursor-pointer"
                />
                <span className="text-sm text-gray-700 dark:text-gray-300 group-hover:text-gray-900 dark:group-hover:text-white">
                  <span className="text-red-500">* </span>
                  개인정보 처리방침에 동의합니다
                </span>
              </label>
              {field.state.meta.errors.length > 0 && (
                <p className="mt-1 ml-7 text-sm text-red-600">
                  {field.state.meta.errors[0]}
                </p>
              )}
            </div>
          )}
        </form.Field>

        <form.Field name="agreeMarketing">
          {(field) => (
            <label className="flex items-start gap-2 cursor-pointer group">
              <input
                type="checkbox"
                checked={field.state.value}
                onChange={(e) => field.handleChange(e.target.checked)}
                className="w-5 h-5 mt-0.5 rounded border-gray-300 text-primary-500 focus:ring-primary-500 cursor-pointer"
              />
              <span className="text-sm text-gray-700 dark:text-gray-300 group-hover:text-gray-900 dark:group-hover:text-white">
                마케팅 정보 수신에 동의합니다 (선택)
              </span>
            </label>
          )}
        </form.Field>
      </div>

      {/* Error Message */}
      {errorMessage && (
        <div className="p-3 rounded-lg bg-red-50 border border-red-200">
          <p className="text-sm text-red-600 text-center">{errorMessage}</p>
        </div>
      )}

      {/* Success Message */}
      {successMessage && (
        <div className="p-3 rounded-lg bg-green-50 border border-green-200">
          <p className="text-sm text-green-600 text-center">{successMessage}</p>
        </div>
      )}

      {/* Submit Button */}
      <form.Subscribe
        selector={(state) => [state.canSubmit, state.isSubmitting]}
      >
        {([canSubmit, isFormSubmitting]) => (
          <button
            type="submit"
            disabled={!canSubmit || isSubmitting || isFormSubmitting || !!successMessage}
            className="w-full bg-primary-500 hover:bg-primary-600 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-bold py-3 rounded-lg transition-colors cursor-pointer"
          >
            {isSubmitting || isFormSubmitting ? '회원가입 중...' : '회원가입'}
          </button>
        )}
      </form.Subscribe>
    </form>
  );
}
