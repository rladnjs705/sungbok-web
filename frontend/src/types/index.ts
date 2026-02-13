/**
 * Common Types for Sungbok Church Website
 */

// Pagination
export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Base Entity (Spring Data JPA)
export interface BaseEntity {
  id: number;
  createdAt: string;
  updatedAt: string;
}

// API Response
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

// Re-export all domain types
export * from './notice';
export * from './sermon';
export * from './ministry';
export * from './youtube-live';
export * from './event';
export * from './gallery';
export * from './testimony';
export * from './prayer-request';
export * from './video-gallery';
export * from './bulletin';
export * from './worship';
export * from './upload';
