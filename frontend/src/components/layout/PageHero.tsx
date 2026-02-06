interface PageHeroProps {
  title: string;
  subtitle: string;
}

export function PageHero({ title, subtitle }: PageHeroProps) {
  return (
    <section className="relative bg-gradient-to-br from-primary-500 to-accent-500 dark:from-gray-700 dark:to-gray-900 py-16 text-center text-white">
      {/* Dark mode overlay for better contrast */}
      <div className="absolute inset-0 bg-black/20 dark:bg-black/40" />

      <div className="container mx-auto px-4 relative z-10">
        <h1 className="text-4xl md:text-5xl font-bold mb-4">{title}</h1>
        <p className="text-lg md:text-xl opacity-90">{subtitle}</p>
      </div>
    </section>
  );
}
