import * as React from "react"
import { cn } from "@/lib/utils"

export interface ProgressProps extends React.HTMLAttributes<HTMLDivElement> {
  value: number
  max?: number
  size?: "sm" | "default" | "lg"
  showLabel?: boolean
}

const Progress = React.forwardRef<HTMLDivElement, ProgressProps>(
  ({ className, value, max = 100, size = "default", showLabel = false, ...props }, ref) => {
    const percentage = Math.min(Math.max((value / max) * 100, 0), 100)
    
    return (
      <div
        ref={ref}
        className={cn(
          "relative w-full overflow-hidden rounded-full bg-neutral-200 dark:bg-neutral-800",
          {
            "h-1": size === "sm",
            "h-2": size === "default",
            "h-3": size === "lg",
          },
          className
        )}
        {...props}
      >
        <div
          className="h-full w-full flex-1 bg-primary-500 transition-all duration-300 ease-out"
          style={{ transform: `translateX(-${100 - percentage}%)` }}
        />
        {showLabel && (
          <span className="absolute inset-0 flex items-center justify-center text-xs font-medium text-neutral-700 dark:text-neutral-300">
            {Math.round(percentage)}%
          </span>
        )}
      </div>
    )
  }
)
Progress.displayName = "Progress"

export { Progress }
