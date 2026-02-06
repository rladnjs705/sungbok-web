// Tab Component
class Tabs {
  constructor(element) {
    this.tabsContainer = element;
    if (!this.tabsContainer) return;

    this.tabs = this.tabsContainer.querySelectorAll('.tab');
    this.init();
  }

  init() {
    this.tabs.forEach(tab => {
      tab.addEventListener('click', () => this.switchTab(tab));
    });

    // Keyboard navigation
    this.tabsContainer.addEventListener('keydown', (e) => {
      const currentTab = document.activeElement;
      if (!currentTab.classList.contains('tab')) return;

      let targetTab = null;

      if (e.key === 'ArrowRight') {
        targetTab = currentTab.nextElementSibling;
      } else if (e.key === 'ArrowLeft') {
        targetTab = currentTab.previousElementSibling;
      } else if (e.key === 'Home') {
        targetTab = this.tabs[0];
      } else if (e.key === 'End') {
        targetTab = this.tabs[this.tabs.length - 1];
      }

      if (targetTab && targetTab.classList.contains('tab')) {
        e.preventDefault();
        targetTab.focus();
        this.switchTab(targetTab);
      }
    });
  }

  switchTab(selectedTab) {
    // Remove active class from all tabs
    this.tabs.forEach(tab => {
      tab.classList.remove('active');
      tab.setAttribute('aria-selected', 'false');
      tab.setAttribute('tabindex', '-1');
    });

    // Add active class to selected tab
    selectedTab.classList.add('active');
    selectedTab.setAttribute('aria-selected', 'true');
    selectedTab.setAttribute('tabindex', '0');

    // Get category from data attribute
    const category = selectedTab.dataset.category;

    // Trigger custom event for filtering
    const event = new CustomEvent('tabChange', {
      detail: { category }
    });
    this.tabsContainer.dispatchEvent(event);

    // Filter content (example implementation)
    this.filterContent(category);
  }

  filterContent(category) {
    // This is a placeholder. In a real app, you would:
    // 1. Make an API call to fetch filtered data
    // 2. Update the DOM with new data
    // 3. Show/hide content based on category

    console.log(`Filtering by category: ${category}`);

    // Example: Show/hide cards based on data-category attribute
    // Only filter cards in the content area, not the tabs themselves
    const contentArea = document.querySelector('.grid-3, .notices-grid, .sermons-grid');
    if (!contentArea) return;

    const cards = contentArea.querySelectorAll('.card[data-category]');
    cards.forEach(card => {
      if (category === 'all' || card.dataset.category === category) {
        card.style.display = '';
      } else {
        card.style.display = 'none';
      }
    });
  }
}

// Category Filter
class CategoryFilter {
  constructor() {
    this.init();
  }

  init() {
    // Listen for tab changes
    document.addEventListener('tabChange', (e) => {
      const { category } = e.detail;
      this.filterByCategory(category);
    });

    // Listen for filter changes (checkboxes, dropdowns, etc.)
    const filterInputs = document.querySelectorAll('[data-filter]');
    filterInputs.forEach(input => {
      input.addEventListener('change', () => this.applyFilters());
    });
  }

  filterByCategory(category) {
    // Mock implementation
    console.log(`Applying category filter: ${category}`);

    // In real app, make API call:
    // fetch(`/api/items?category=${category}`)
    //   .then(res => res.json())
    //   .then(data => this.renderItems(data));
  }

  applyFilters() {
    const filters = {};

    // Collect all active filters
    const filterInputs = document.querySelectorAll('[data-filter]:checked, [data-filter]:not([type="checkbox"])');
    filterInputs.forEach(input => {
      const filterType = input.dataset.filter;
      if (!filters[filterType]) {
        filters[filterType] = [];
      }
      filters[filterType].push(input.value);
    });

    console.log('Applying filters:', filters);

    // In real app, make API call with filters
    // fetch(`/api/items?${new URLSearchParams(filters)}`)
    //   .then(res => res.json())
    //   .then(data => this.renderItems(data));
  }

  renderItems(items) {
    // Render filtered items to DOM
    console.log('Rendering items:', items);
  }
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  // Initialize tabs
  const tabsContainer = document.querySelector('.tabs');
  if (tabsContainer) {
    new Tabs(tabsContainer);
  }

  // Initialize category filter
  new CategoryFilter();
});
