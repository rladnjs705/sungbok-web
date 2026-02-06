'use client';

import { useState, useCallback, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { motion, AnimatePresence } from 'framer-motion';
import { cn } from '@/lib/utils';

interface SearchBarProps {
  placeholder?: string;
  onSearch?: (query: string) => void;
  autoFocus?: boolean;
  className?: string;
}

export function SearchBar({
  placeholder = '검색어를 입력하세요',
  onSearch,
  autoFocus = false,
  className,
}: SearchBarProps) {
  const [query, setQuery] = useState('');
  const [isFocused, setIsFocused] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const router = useRouter();

  useEffect(() => {
    if (autoFocus && inputRef.current) {
      inputRef.current.focus();
    }
  }, [autoFocus]);

  const handleSearch = useCallback(() => {
    if (query.trim()) {
      if (onSearch) {
        onSearch(query);
      } else {
        router.push(`/search?q=${encodeURIComponent(query)}`);
      }
    }
  }, [query, onSearch, router]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleClear = () => {
    setQuery('');
    inputRef.current?.focus();
  };

  return (
    <div className={cn('relative', className)}>
      <div
        className={cn(
          'relative flex items-center bg-white dark:bg-gray-800 rounded-lg border transition-all duration-200',
          isFocused
            ? 'border-primary-500 ring-2 ring-primary-500/20'
            : 'border-gray-300 dark:border-gray-600'
        )}
      >
        {/* Search Icon */}
        <div className="pl-4 text-gray-400 dark:text-gray-500">
          <svg
            className="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </div>

        {/* Input */}
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="flex-1 px-4 py-3 bg-transparent border-none focus:outline-none text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400"
        />

        {/* Clear Button */}
        <AnimatePresence>
          {query && (
            <motion.button
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.8 }}
              onClick={handleClear}
              className="mr-2 p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors cursor-pointer"
              aria-label="검색어 지우기"
            >
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                  clipRule="evenodd"
                />
              </svg>
            </motion.button>
          )}
        </AnimatePresence>

        {/* Search Button */}
        <button
          onClick={handleSearch}
          disabled={!query.trim()}
          className={cn(
            'px-6 py-3 rounded-r-lg font-semibold transition-colors cursor-pointer',
            query.trim()
              ? 'bg-primary-500 text-white hover:bg-primary-600'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-400 cursor-not-allowed'
          )}
        >
          검색
        </button>
      </div>
    </div>
  );
}

export function SearchBarCompact({ placeholder, onSearch, className }: SearchBarProps) {
  const [query, setQuery] = useState('');
  const [isExpanded, setIsExpanded] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleSearch = () => {
    if (query.trim() && onSearch) {
      onSearch(query);
    }
  };

  const handleToggle = () => {
    setIsExpanded(!isExpanded);
    if (!isExpanded) {
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  };

  return (
    <div className={cn('relative', className)}>
      <AnimatePresence>
        {isExpanded ? (
          <motion.div
            initial={{ width: 40 }}
            animate={{ width: 300 }}
            exit={{ width: 40 }}
            className="flex items-center bg-white dark:bg-gray-800 rounded-lg border border-gray-300 dark:border-gray-600"
          >
            <input
              ref={inputRef}
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              onBlur={() => !query && setIsExpanded(false)}
              placeholder={placeholder}
              className="flex-1 px-3 py-2 bg-transparent border-none focus:outline-none text-sm text-gray-900 dark:text-white placeholder-gray-500"
            />
            {query && (
              <button
                onClick={() => setQuery('')}
                className="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 cursor-pointer"
              >
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fillRule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                    clipRule="evenodd"
                  />
                </svg>
              </button>
            )}
          </motion.div>
        ) : (
          <button
            onClick={handleToggle}
            className="p-2 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg cursor-pointer"
            aria-label="검색"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </button>
        )}
      </AnimatePresence>
    </div>
  );
}
