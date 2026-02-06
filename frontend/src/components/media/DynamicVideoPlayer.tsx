'use client';

// ✅ Bundle Size Optimization: Dynamic import for heavy component
// This component is only loaded when actually needed
import dynamic from 'next/dynamic';
import { Skeleton } from '@/components/ui/skeleton';

// ✅ Dynamic import with loading state
const VideoPlayer = dynamic(() => import('./VideoPlayer').then((mod) => ({ default: mod.VideoPlayer })), {
  loading: () => (
    <div className="aspect-video w-full overflow-hidden rounded-lg bg-gray-900">
      <Skeleton className="h-full w-full" />
    </div>
  ),
  ssr: false, // Don't render on server (YouTube embed)
});

interface DynamicVideoPlayerProps {
  videoId: string;
  title?: string;
  autoplay?: boolean;
}

// ✅ This is the component you should use in your pages
export function DynamicVideoPlayer({
  videoId,
  title,
  autoplay = false,
}: DynamicVideoPlayerProps) {
  return <VideoPlayer videoId={videoId} title={title} autoplay={autoplay} />;
}
