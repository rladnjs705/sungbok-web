import { QueryClient } from '@tanstack/react-query';

export const getQueryClient = () => {
  return new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: 1000 * 60 * 5, // 5분
        gcTime: 1000 * 60 * 10, // 10분
        refetchOnWindowFocus: false,
        retry: 1,
      },
    },
  });
};
