'use client';

import { ReactNode } from 'react';
import { cn } from '@/lib/utils';

interface MasonryGridProps {
  children: ReactNode;
  columns?: number;
  gap?: number;
  className?: string;
}

export function MasonryGrid({
  children,
  columns = 3,
  gap = 4,
  className,
}: MasonryGridProps) {
  return (
    <div
      className={cn('grid', className)}
      style={{
        gridTemplateColumns: `repeat(${columns}, 1fr)`,
        gap: `${gap * 0.25}rem`,
      }}
    >
      {children}
    </div>
  );
}

export function ResponsiveMasonryGrid({
  children,
  className,
}: {
  children: ReactNode;
  className?: string;
}) {
  return (
    <div
      className={cn(
        'grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4',
        className
      )}
    >
      {children}
    </div>
  );
}
