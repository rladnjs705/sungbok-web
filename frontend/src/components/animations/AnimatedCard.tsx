'use client';

import { motion } from 'framer-motion';
import { ReactNode } from 'react';

interface AnimatedCardProps {
  children: ReactNode;
  className?: string;
  delay?: number;
}

export function AnimatedCard({ children, className = '', delay = 0 }: AnimatedCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-50px' }}
      transition={{ duration: 0.4, delay, ease: 'easeOut' }}
      whileHover={{
        y: -8,
        scale: 1.02,
        transition: { duration: 0.2 },
      }}
      className={className}
    >
      {children}
    </motion.div>
  );
}

export function HoverLiftCard({ children, className = '' }: { children: ReactNode; className?: string }) {
  return (
    <motion.div
      whileHover={{
        y: -4,
        transition: { duration: 0.2, ease: 'easeOut' },
      }}
      className={className}
    >
      {children}
    </motion.div>
  );
}

export function HoverScaleCard({ children, className = '' }: { children: ReactNode; className?: string }) {
  return (
    <motion.div
      whileHover={{
        scale: 1.05,
        transition: { duration: 0.2, ease: 'easeOut' },
      }}
      whileTap={{ scale: 0.98 }}
      className={className}
    >
      {children}
    </motion.div>
  );
}
