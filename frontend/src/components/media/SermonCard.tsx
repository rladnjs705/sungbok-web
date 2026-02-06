'use client';

import { Calendar, Eye, ThumbsUp, PlayCircle } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import Image from 'next/image';
import Link from 'next/link';

// ✅ Rendering Performance: Define type outside component
interface SermonCardProps {
  sermon: {
    id: number;
    title: string;
    description: string;
    date: string;
    thumbnail: string;
    viewCount: number;
    likeCount?: number;
    preacher?: string;
  };
}

// ✅ Re-render Optimization: Extract static icon components
function StatIcon({ icon: Icon, value }: { icon: any; value: number }) {
  return (
    <span className="flex items-center gap-1 text-sm text-gray-500">
      <Icon className="h-4 w-4" />
      {value.toLocaleString()}
    </span>
  );
}

export function SermonCard({ sermon }: SermonCardProps) {
  return (
    <Link href={`/media/sermons/${sermon.id}`}>
      <Card className="group relative overflow-hidden rounded-2xl shadow-lg transition-all duration-300 hover:-translate-y-2 hover:shadow-2xl">
        {/* Thumbnail with Overlay */}
        <div className="relative aspect-video overflow-hidden">
          <Image
            src={sermon.thumbnail}
            alt={sermon.title}
            fill
            className="object-cover transition-transform duration-500 group-hover:scale-110"
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          />

          {/* Play Button Overlay */}
          <div className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 transition-opacity group-hover:opacity-100">
            <PlayCircle className="h-16 w-16 text-white" />
          </div>
        </div>

        {/* Content */}
        <CardContent className="p-6">
          <div className="mb-2 flex items-center gap-2 text-sm text-gray-500">
            <Calendar className="h-4 w-4" />
            <span>{sermon.date}</span>
            {sermon.preacher && (
              <>
                <span>·</span>
                <span>{sermon.preacher}</span>
              </>
            )}
          </div>

          <h3 className="mb-2 line-clamp-2 text-xl font-bold text-gray-900">
            {sermon.title}
          </h3>

          <p className="mb-4 line-clamp-2 text-gray-600">{sermon.description}</p>

          {/* Stats */}
          <div className="flex gap-4">
            <StatIcon icon={Eye} value={sermon.viewCount} />
            {sermon.likeCount && (
              <StatIcon icon={ThumbsUp} value={sermon.likeCount} />
            )}
          </div>
        </CardContent>
      </Card>
    </Link>
  );
}
