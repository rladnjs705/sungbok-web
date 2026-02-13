/**
 * Authentication Store using Zustand
 * 
 * HttpOnly Cookie 기반 인증 상태 관리
 * Access Token은 Cookie에 저장되므로 JS에서 접근 불가
 * 로그인 상태는 백엔드 API (/api/auth/me)로 확인
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface User {
  id: number;
  email: string;
  name: string;
  phone?: string;
  role: 'USER' | 'ADMIN';
  provider?: string;
  createdAt: string;
}

interface AuthState {
  // 상태
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // 액션
  setUser: (user: User | null) => void;
  setAuthenticated: (value: boolean) => void;
  setLoading: (value: boolean) => void;
  login: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      // 초기 상태
      user: null,
      isAuthenticated: false,
      isLoading: true,
      
      // 액션
      setUser: (user) => set({ user }),
      setAuthenticated: (value) => set({ isAuthenticated: value }),
      setLoading: (value) => set({ isLoading: value }),
      
      login: (user) => set({
        user,
        isAuthenticated: true,
        isLoading: false,
      }),
      
      logout: () => set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
      }),
    }),
    {
      name: 'auth-storage',
      // localStorage에 저장할 필드 (user 정볼만 저장, 인증 상태는 서버에서 확인)
      partialize: (state) => ({ user: state.user }),
    }
  )
);
