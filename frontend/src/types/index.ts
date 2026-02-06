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
