/**
 * Cloud Harmony Design System - Design Tokens
 * 성복교회 2026 Color Trends + Church Design
 *
 * TypeScript로 정의된 디자인 토큰
 * Tailwind CSS와 동기화되어 있습니다.
 */

export const colors = {
  // Primary Colors (Cloud Blue)
  primary: {
    50: '#f0f4f8',
    100: '#d9e2ec',
    200: '#bcccdc',
    300: '#9fb3c8',
    400: '#829ab1',
    500: '#6B84A3',  // Main
    600: '#56718f',
    700: '#415a77',
    800: '#2e4357',
    900: '#1a2c3b',
  },

  // Secondary Colors (Soft Cloud Blue)
  secondary: {
    50: '#f5f9fc',
    100: '#e8f2f9',
    200: '#d4e6f3',
    300: '#b8d7ed',
    400: '#a6c5e0',
    500: '#94B3D4',  // Main
    600: '#7a9bc4',
    700: '#5d7fa8',
    800: '#4a6589',
    900: '#364a64',
  },

  // Accent Colors (Deep Sky)
  accent: {
    50: '#e8f0f7',
    100: '#c8ddef',
    200: '#a0c4e0',
    300: '#78aad0',
    400: '#5e8eba',
    500: '#4A6FA5',  // Main
    600: '#3d5c8c',
    700: '#30476b',
    800: '#23354f',
    900: '#162132',
  },

  // Action Colors
  warm: '#D9B88F',      // Golden Sand
  fresh: '#A8C9A8',     // Soft Sage
  energy: '#D4896A',    // Warm Terracotta

  // Gray Scale
  gray: {
    50: '#f8f9fa',
    100: '#f1f3f5',
    200: '#e9ecef',
    300: '#dee2e6',
    400: '#ced4da',
    500: '#adb5bd',
    600: '#868e96',
    700: '#495057',
    800: '#343a40',
    900: '#212529',
  },

  // Semantic Colors
  success: '#A8C9A8',
  warning: '#D9B88F',
  error: '#e63946',
  info: '#94B3D4',
} as const;

export const typography = {
  fontFamily: {
    heading: '"Unbounded", ui-sans-serif, system-ui, sans-serif',
    body: '"Pretendard Variable", ui-sans-serif, system-ui, sans-serif',
    serif: '"Cormorant Garamond", ui-serif, Georgia, serif',
    serifBody: '"Lora", ui-serif, Georgia, serif',
    mono: '"JetBrains Mono", ui-monospace, monospace',
  },

  fontSize: {
    xs: '0.75rem',     // 12px
    sm: '0.875rem',    // 14px
    base: '1rem',      // 16px
    lg: '1.125rem',    // 18px
    xl: '1.25rem',     // 20px
    '2xl': '1.5rem',   // 24px
    '3xl': '1.875rem', // 30px
    '4xl': '2.25rem',  // 36px
    '5xl': '3rem',     // 48px
    '6xl': '3.75rem',  // 60px
    '7xl': '4.5rem',   // 72px
  },

  fontWeight: {
    light: 300,
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
    extrabold: 800,
  },

  lineHeight: {
    none: '1',
    tight: '1.25',
    snug: '1.375',
    normal: '1.5',
    relaxed: '1.625',
    loose: '1.75',
    spacious: '2',
  },

  letterSpacing: {
    tighter: '-0.05em',
    tight: '-0.025em',
    normal: '0',
    wide: '0.025em',
    wider: '0.05em',
    widest: '0.1em',
  },
} as const;

export const spacing = {
  0: '0',
  0.5: '0.125rem',  // 2px
  1: '0.25rem',     // 4px
  1.5: '0.375rem',  // 6px
  2: '0.5rem',      // 8px
  2.5: '0.625rem',  // 10px
  3: '0.75rem',     // 12px
  3.5: '0.875rem',  // 14px
  4: '1rem',        // 16px
  5: '1.25rem',     // 20px
  6: '1.5rem',      // 24px
  7: '1.75rem',     // 28px
  8: '2rem',        // 32px
  9: '2.25rem',     // 36px
  10: '2.5rem',     // 40px
  12: '3rem',       // 48px
  14: '3.5rem',     // 56px
  16: '4rem',       // 64px
  20: '5rem',       // 80px
  24: '6rem',       // 96px
  28: '7rem',       // 112px
  32: '8rem',       // 128px
} as const;

export const borderRadius = {
  none: '0',
  sm: '0.125rem',   // 2px
  base: '0.25rem',  // 4px
  md: '0.375rem',   // 6px
  lg: '0.5rem',     // 8px
  xl: '0.75rem',    // 12px
  '2xl': '1rem',    // 16px
  '3xl': '1.5rem',  // 24px
  full: '9999px',
} as const;

export const boxShadow = {
  sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
  base: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',
  md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
  lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
  xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
  '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
  inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.05)',
  none: 'none',
} as const;

export const transition = {
  duration: {
    75: '75ms',
    100: '100ms',
    150: '150ms',
    200: '200ms',
    300: '300ms',
    500: '500ms',
    700: '700ms',
    1000: '1000ms',
  },

  timingFunction: {
    linear: 'linear',
    in: 'cubic-bezier(0.4, 0, 1, 1)',
    out: 'cubic-bezier(0, 0, 0.2, 1)',
    inOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
  },
} as const;

export const breakpoints = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
} as const;

export const container = {
  center: true,
  padding: '2rem',
  screens: breakpoints,
} as const;

/**
 * 톤별 스타일 프리셋
 */
export const tonePresets = {
  // 메인: 활동적/역동적
  main: {
    fontFamily: typography.fontFamily.heading,
    animation: 'gradient',
  },

  // 부서: 부서별 고유성
  department: {
    fontFamily: typography.fontFamily.body,
  },

  // 소개: 진중한/신뢰감
  about: {
    fontFamily: typography.fontFamily.serif,
    bodyFont: typography.fontFamily.serifBody,
    lineHeight: typography.lineHeight.loose,
  },
} as const;

/**
 * 컴포넌트별 기본 스타일
 */
export const componentStyles = {
  button: {
    primary: {
      bg: colors.primary[500],
      hoverBg: colors.primary[600],
      text: '#ffffff',
      borderRadius: borderRadius.lg,
      padding: `${spacing[2.5]} ${spacing[6]}`,
      fontWeight: typography.fontWeight.semibold,
    },
    secondary: {
      bg: colors.secondary[500],
      hoverBg: colors.secondary[600],
      text: colors.gray[900],
      borderRadius: borderRadius.lg,
      padding: `${spacing[2.5]} ${spacing[6]}`,
      fontWeight: typography.fontWeight.semibold,
    },
  },

  card: {
    default: {
      bg: '#ffffff',
      border: colors.gray[200],
      borderRadius: borderRadius['2xl'],
      padding: spacing[6],
      shadow: boxShadow.md,
    },
  },

  input: {
    default: {
      border: colors.gray[300],
      focusBorder: colors.primary[500],
      borderRadius: borderRadius.lg,
      padding: `${spacing[2.5]} ${spacing[4]}`,
    },
  },
} as const;

// Type exports
export type ColorScale = typeof colors.primary;
export type Color = keyof typeof colors;
export type FontSize = keyof typeof typography.fontSize;
export type FontWeight = keyof typeof typography.fontWeight;
export type Spacing = keyof typeof spacing;
export type BorderRadius = keyof typeof borderRadius;
export type Breakpoint = keyof typeof breakpoints;
