// Mobile Navigation
class MobileNav {
  constructor() {
    this.toggle = document.querySelector('.navbar-toggle');
    this.mobileMenu = document.querySelector('.mobile-menu');
    this.closeBtn = document.querySelector('.mobile-menu-close');
    this.menuLinks = document.querySelectorAll('.mobile-menu-nav a');

    this.init();
  }

  init() {
    if (!this.toggle || !this.mobileMenu) return;

    // Toggle button click
    this.toggle.addEventListener('click', () => this.open());

    // Close button click
    this.closeBtn.addEventListener('click', () => this.close());

    // Close on menu link click
    this.menuLinks.forEach(link => {
      link.addEventListener('click', () => this.close());
    });

    // Close on outside click
    this.mobileMenu.addEventListener('click', (e) => {
      if (e.target === this.mobileMenu) {
        this.close();
      }
    });

    // Close on escape key
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.mobileMenu.classList.contains('active')) {
        this.close();
      }
    });
  }

  open() {
    this.mobileMenu.classList.add('active');
    this.toggle.setAttribute('aria-expanded', 'true');
    document.body.style.overflow = 'hidden';

    // Focus trap
    const firstFocusable = this.mobileMenu.querySelector('button, a');
    if (firstFocusable) firstFocusable.focus();
  }

  close() {
    this.mobileMenu.classList.remove('active');
    this.toggle.setAttribute('aria-expanded', 'false');
    document.body.style.overflow = '';

    // Return focus to toggle button
    this.toggle.focus();
  }
}

// Sticky Navigation
class StickyNav {
  constructor() {
    this.navbar = document.querySelector('.navbar');
    this.init();
  }

  init() {
    if (!this.navbar) return;

    let lastScroll = 0;

    window.addEventListener('scroll', () => {
      const currentScroll = window.pageYOffset;

      // Add shadow when scrolled
      if (currentScroll > 10) {
        this.navbar.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.1)';
      } else {
        this.navbar.style.boxShadow = '';
      }

      // Hide/show on scroll (optional)
      // if (currentScroll > lastScroll && currentScroll > 100) {
      //   this.navbar.style.transform = 'translateY(-100%)';
      // } else {
      //   this.navbar.style.transform = 'translateY(0)';
      // }

      lastScroll = currentScroll;
    });
  }
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
  new MobileNav();
  new StickyNav();
});
