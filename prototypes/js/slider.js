// Audio Player
class AudioPlayer {
  constructor(element) {
    this.player = element;
    if (!this.player) return;

    this.playBtn = this.player.querySelector('.play-btn');
    this.progressBar = this.player.querySelector('.progress-bar');
    this.currentTime = this.player.querySelector('.current-time');
    this.totalTime = this.player.querySelector('.total-time');

    // Mock audio (in real app, use <audio> element)
    this.isPlaying = false;
    this.duration = 2730; // 45:30 in seconds
    this.currentPosition = 0;
    this.interval = null;

    this.init();
  }

  init() {
    if (!this.playBtn) return;

    this.playBtn.addEventListener('click', () => this.togglePlay());
    this.progressBar.addEventListener('input', (e) => this.seek(e.target.value));

    // Initialize UI
    this.totalTime.textContent = this.formatTime(this.duration);
    this.updateUI();
  }

  togglePlay() {
    this.isPlaying = !this.isPlaying;

    if (this.isPlaying) {
      this.playBtn.textContent = '⏸';
      this.playBtn.setAttribute('aria-label', '일시정지');
      this.startProgress();
    } else {
      this.playBtn.textContent = '▶';
      this.playBtn.setAttribute('aria-label', '재생');
      this.pauseProgress();
    }
  }

  startProgress() {
    this.interval = setInterval(() => {
      this.currentPosition++;
      this.updateUI();

      if (this.currentPosition >= this.duration) {
        this.togglePlay();
        this.currentPosition = 0;
      }
    }, 1000);
  }

  pauseProgress() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
    }
  }

  seek(value) {
    this.currentPosition = (value / 100) * this.duration;
    this.updateUI();
  }

  updateUI() {
    const progress = (this.currentPosition / this.duration) * 100;
    this.progressBar.value = progress;
    this.currentTime.textContent = this.formatTime(this.currentPosition);
  }

  formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
}

// Image Carousel/Slider
class ImageSlider {
  constructor(element) {
    this.slider = element;
    if (!this.slider) return;

    this.track = this.slider.querySelector('.slider-track');
    this.slides = this.slider.querySelectorAll('.slider-slide');
    this.prevBtn = this.slider.querySelector('.slider-prev');
    this.nextBtn = this.slider.querySelector('.slider-next');
    this.dotsContainer = this.slider.querySelector('.slider-dots');

    this.currentIndex = 0;
    this.slideWidth = 0;
    this.autoPlayInterval = null;
    this.autoPlayDelay = 5000; // 5 seconds

    this.init();
  }

  init() {
    if (!this.track || this.slides.length === 0) return;

    // Create dots
    this.createDots();

    // Event listeners
    if (this.prevBtn) {
      this.prevBtn.addEventListener('click', () => this.prev());
    }

    if (this.nextBtn) {
      this.nextBtn.addEventListener('click', () => this.next());
    }

    // Keyboard navigation
    this.slider.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowLeft') this.prev();
      if (e.key === 'ArrowRight') this.next();
    });

    // Touch swipe support
    this.addSwipeSupport();

    // Resize handler
    window.addEventListener('resize', () => this.updateDimensions());

    // Initial setup
    this.updateDimensions();
    this.goToSlide(0);

    // Start autoplay
    this.startAutoPlay();

    // Pause autoplay on hover
    this.slider.addEventListener('mouseenter', () => this.stopAutoPlay());
    this.slider.addEventListener('mouseleave', () => this.startAutoPlay());
  }

  createDots() {
    if (!this.dotsContainer) return;

    this.slides.forEach((_, index) => {
      const dot = document.createElement('button');
      dot.className = 'slider-dot';
      dot.setAttribute('aria-label', `슬라이드 ${index + 1}로 이동`);
      dot.addEventListener('click', () => this.goToSlide(index));
      this.dotsContainer.appendChild(dot);
    });

    this.dots = this.dotsContainer.querySelectorAll('.slider-dot');
  }

  updateDimensions() {
    this.slideWidth = this.slides[0].offsetWidth;
  }

  goToSlide(index) {
    this.currentIndex = index;
    const offset = -this.currentIndex * this.slideWidth;
    this.track.style.transform = `translateX(${offset}px)`;

    // Update dots
    if (this.dots) {
      this.dots.forEach((dot, i) => {
        dot.classList.toggle('active', i === this.currentIndex);
      });
    }

    // Update buttons
    if (this.prevBtn) {
      this.prevBtn.disabled = this.currentIndex === 0;
    }
    if (this.nextBtn) {
      this.nextBtn.disabled = this.currentIndex === this.slides.length - 1;
    }
  }

  prev() {
    if (this.currentIndex > 0) {
      this.goToSlide(this.currentIndex - 1);
    }
  }

  next() {
    if (this.currentIndex < this.slides.length - 1) {
      this.goToSlide(this.currentIndex + 1);
    } else {
      this.goToSlide(0); // Loop back to start
    }
  }

  startAutoPlay() {
    this.autoPlayInterval = setInterval(() => {
      this.next();
    }, this.autoPlayDelay);
  }

  stopAutoPlay() {
    if (this.autoPlayInterval) {
      clearInterval(this.autoPlayInterval);
      this.autoPlayInterval = null;
    }
  }

  addSwipeSupport() {
    let touchStartX = 0;
    let touchEndX = 0;

    this.track.addEventListener('touchstart', (e) => {
      touchStartX = e.changedTouches[0].screenX;
    });

    this.track.addEventListener('touchend', (e) => {
      touchEndX = e.changedTouches[0].screenX;
      this.handleSwipe();
    });

    const handleSwipe = () => {
      const swipeThreshold = 50;
      const diff = touchStartX - touchEndX;

      if (Math.abs(diff) > swipeThreshold) {
        if (diff > 0) {
          this.next();
        } else {
          this.prev();
        }
      }
    };

    this.handleSwipe = handleSwipe;
  }
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  // Initialize audio player
  const audioPlayer = document.querySelector('.audio-player');
  if (audioPlayer) {
    new AudioPlayer(audioPlayer);
  }

  // Initialize sliders
  document.querySelectorAll('.image-slider').forEach(slider => {
    new ImageSlider(slider);
  });
});
