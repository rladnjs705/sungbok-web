// Modal Component
class Modal {
  constructor(modalId) {
    this.modal = document.getElementById(modalId);
    if (!this.modal) return;

    this.dialog = this.modal.querySelector('.modal-dialog');
    this.closeButtons = this.modal.querySelectorAll('[data-dismiss="modal"]');

    this.init();
  }

  init() {
    // Close buttons
    this.closeButtons.forEach(btn => {
      btn.addEventListener('click', () => this.close());
    });

    // Backdrop click
    this.modal.addEventListener('click', (e) => {
      if (e.target === this.modal) {
        this.close();
      }
    });

    // Escape key
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.modal.classList.contains('active')) {
        this.close();
      }
    });
  }

  open() {
    this.modal.classList.add('active');
    document.body.style.overflow = 'hidden';

    // Focus first focusable element
    const firstFocusable = this.dialog.querySelector('button, [href], input, select, textarea');
    if (firstFocusable) firstFocusable.focus();
  }

  close() {
    this.modal.classList.remove('active');
    document.body.style.overflow = '';
  }
}

// Lightbox for images
class Lightbox {
  constructor() {
    this.lightbox = document.getElementById('lightbox');
    if (!this.lightbox) return;

    this.image = this.lightbox.querySelector('.lightbox-image');
    this.caption = this.lightbox.querySelector('.lightbox-caption');
    this.currentCounter = this.lightbox.querySelector('.current');
    this.totalCounter = this.lightbox.querySelector('.total');
    this.closeBtn = this.lightbox.querySelector('.lightbox-close');
    this.prevBtn = this.lightbox.querySelector('.lightbox-prev');
    this.nextBtn = this.lightbox.querySelector('.lightbox-next');

    this.images = [];
    this.currentIndex = 0;

    this.init();
  }

  init() {
    // Collect all gallery images
    const galleryItems = document.querySelectorAll('.gallery-item');
    this.images = Array.from(galleryItems).map(item => {
      const img = item.querySelector('img');
      const caption = item.querySelector('figcaption');
      return {
        src: img.src.replace('/thumb/', '/full/').replace('-thumb', ''),
        alt: img.alt,
        caption: caption ? caption.textContent : ''
      };
    });

    if (this.totalCounter) {
      this.totalCounter.textContent = this.images.length;
    }

    // Event listeners
    galleryItems.forEach((item, index) => {
      item.addEventListener('click', () => this.open(index));
    });

    if (this.closeBtn) {
      this.closeBtn.addEventListener('click', () => this.close());
    }

    if (this.prevBtn) {
      this.prevBtn.addEventListener('click', () => this.prev());
    }

    if (this.nextBtn) {
      this.nextBtn.addEventListener('click', () => this.next());
    }

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
      if (!this.lightbox.classList.contains('active')) return;

      if (e.key === 'Escape') this.close();
      if (e.key === 'ArrowLeft') this.prev();
      if (e.key === 'ArrowRight') this.next();
    });

    // Touch swipe support
    this.addSwipeSupport();
  }

  open(index) {
    this.currentIndex = index;
    this.updateImage();
    this.lightbox.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  close() {
    this.lightbox.classList.remove('active');
    document.body.style.overflow = '';
  }

  prev() {
    this.currentIndex = (this.currentIndex - 1 + this.images.length) % this.images.length;
    this.updateImage();
  }

  next() {
    this.currentIndex = (this.currentIndex + 1) % this.images.length;
    this.updateImage();
  }

  updateImage() {
    const current = this.images[this.currentIndex];
    if (this.image) {
      this.image.src = current.src;
      this.image.alt = current.alt;
    }
    if (this.caption) {
      this.caption.textContent = current.caption || '';
    }
    if (this.currentCounter) {
      this.currentCounter.textContent = this.currentIndex + 1;
    }
  }

  addSwipeSupport() {
    let touchStartX = 0;
    let touchEndX = 0;

    this.lightbox.addEventListener('touchstart', (e) => {
      touchStartX = e.changedTouches[0].screenX;
    });

    this.lightbox.addEventListener('touchend', (e) => {
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
  // Initialize lightbox if it exists
  new Lightbox();

  // Initialize modals
  document.querySelectorAll('.modal').forEach(modal => {
    new Modal(modal.id);
  });
});
