'use client';

import { useState, useCallback } from 'react';

// ✅ Re-render Optimization: Type definition outside component
interface VideoPlayerProps {
  videoId: string;
  title?: string;
  autoplay?: boolean;
}

// ✅ Rendering Performance: Hoist static objects
const PLAYER_PARAMS = new URLSearchParams({
  rel: '0',
  modestbranding: '1',
  controls: '1',
}).toString();

export function VideoPlayer({
  videoId,
  title = 'YouTube Video',
  autoplay = false,
}: VideoPlayerProps) {
  // ✅ Re-render Optimization: Use functional setState
  const [isLoaded, setIsLoaded] = useState(false);

  // ✅ Re-render Optimization: useCallback for stable event handler
  const handleLoad = useCallback(() => {
    setIsLoaded(true);
  }, []);

  // ✅ Rendering Performance: Build URL once
  const embedUrl = `https://www.youtube.com/embed/${videoId}?${PLAYER_PARAMS}${
    autoplay ? '&autoplay=1' : ''
  }`;

  return (
    <div className="relative aspect-video w-full overflow-hidden rounded-lg bg-gray-900 shadow-2xl">
      {/* Loading State */}
      {!isLoaded && (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-800">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-gray-600 border-t-white" />
        </div>
      )}

      {/* YouTube iframe */}
      <iframe
        src={embedUrl}
        title={title}
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowFullScreen
        onLoad={handleLoad}
        className="absolute inset-0 h-full w-full"
      />
    </div>
  );
}
