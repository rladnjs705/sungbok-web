'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Lightbox } from '@/components/ui/Lightbox';
import { ResponsiveMasonryGrid } from '@/components/ui/MasonryGrid';

interface GalleryImage {
  id: number;
  src: string;
  caption: string;
}

interface GalleryClientProps {
  images: readonly GalleryImage[];
}

export function GalleryClient({ images }: GalleryClientProps) {
  const [lightboxOpen, setLightboxOpen] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const handleImageClick = (index: number) => {
    setCurrentImageIndex(index);
    setLightboxOpen(true);
  };

  const imageSources = images.map((img) => img.src);

  return (
    <>
      {/* Image Grid with Responsive Masonry */}
      <ResponsiveMasonryGrid>
        {images.map((image, index) => (
          <div
            key={image.id}
            onClick={() => handleImageClick(index)}
            className="group relative aspect-video rounded-xl overflow-hidden shadow-lg hover:shadow-2xl transition-shadow cursor-pointer"
          >
            <Image
              src={image.src}
              alt={image.caption}
              fill
              sizes="(max-width: 640px) 100vw, (max-width: 768px) 50vw, (max-width: 1024px) 33vw, 25vw"
              loading={index < 2 ? 'eager' : 'lazy'}
              priority={index < 2}
              className="object-cover group-hover:scale-105 transition-transform duration-300"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity">
              <div className="absolute bottom-0 left-0 right-0 p-6">
                <p className="text-white font-semibold">{image.caption}</p>
              </div>
            </div>
            {/* Magnify Icon on Hover */}
            <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <div className="bg-white/20 backdrop-blur-sm rounded-full p-4">
                <svg
                  className="w-8 h-8 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v6m3-3H7"
                  />
                </svg>
              </div>
            </div>
          </div>
        ))}
      </ResponsiveMasonryGrid>

      {/* Lightbox */}
      <Lightbox
        images={imageSources}
        initialIndex={currentImageIndex}
        isOpen={lightboxOpen}
        onClose={() => setLightboxOpen(false)}
      />
    </>
  );
}
