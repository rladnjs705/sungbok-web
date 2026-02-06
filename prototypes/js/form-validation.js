// Form Validation
class FormValidator {
  constructor(form) {
    this.form = form;
    if (!this.form) return;

    this.fields = this.form.querySelectorAll('[required], [data-validate]');
    this.init();
  }

  init() {
    // Validate on submit
    this.form.addEventListener('submit', (e) => {
      e.preventDefault();
      if (this.validateAll()) {
        this.handleSubmit();
      }
    });

    // Real-time validation
    this.fields.forEach(field => {
      field.addEventListener('blur', () => this.validateField(field));
      field.addEventListener('input', () => {
        if (field.classList.contains('error')) {
          this.validateField(field);
        }
      });
    });
  }

  validateAll() {
    let isValid = true;

    this.fields.forEach(field => {
      if (!this.validateField(field)) {
        isValid = false;
      }
    });

    return isValid;
  }

  validateField(field) {
    const value = field.value.trim();
    const type = field.type;
    const validationType = field.dataset.validate;

    // Clear previous error
    this.clearError(field);

    // Required field check
    if (field.hasAttribute('required') && !value) {
      this.showError(field, '이 필드는 필수입니다.');
      return false;
    }

    // Email validation
    if (type === 'email' || validationType === 'email') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (value && !emailRegex.test(value)) {
        this.showError(field, '올바른 이메일 주소를 입력하세요.');
        return false;
      }
    }

    // Phone validation
    if (validationType === 'phone') {
      const phoneRegex = /^01[0-9]-?[0-9]{4}-?[0-9]{4}$/;
      if (value && !phoneRegex.test(value.replace(/-/g, ''))) {
        this.showError(field, '올바른 전화번호를 입력하세요.');
        return false;
      }
    }

    // Min length
    const minLength = field.getAttribute('minlength');
    if (minLength && value.length < parseInt(minLength)) {
      this.showError(field, `최소 ${minLength}자 이상 입력하세요.`);
      return false;
    }

    // Max length
    const maxLength = field.getAttribute('maxlength');
    if (maxLength && value.length > parseInt(maxLength)) {
      this.showError(field, `최대 ${maxLength}자까지 입력 가능합니다.`);
      return false;
    }

    // Custom validation
    if (validationType === 'password-confirm') {
      const password = this.form.querySelector('[type="password"]');
      if (password && value !== password.value) {
        this.showError(field, '비밀번호가 일치하지 않습니다.');
        return false;
      }
    }

    return true;
  }

  showError(field, message) {
    field.classList.add('error');
    field.setAttribute('aria-invalid', 'true');

    // Create or update error message
    let errorElement = field.parentElement.querySelector('.form-error');
    if (!errorElement) {
      errorElement = document.createElement('span');
      errorElement.className = 'form-error';
      errorElement.setAttribute('role', 'alert');
      field.parentElement.appendChild(errorElement);
    }
    errorElement.textContent = message;
  }

  clearError(field) {
    field.classList.remove('error');
    field.setAttribute('aria-invalid', 'false');

    const errorElement = field.parentElement.querySelector('.form-error');
    if (errorElement) {
      errorElement.remove();
    }
  }

  handleSubmit() {
    // Collect form data
    const formData = new FormData(this.form);
    const data = Object.fromEntries(formData);

    console.log('Form submitted:', data);

    // Show loading state
    const submitButton = this.form.querySelector('[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.innerHTML = '<span class="spinner"></span> 저장 중...';

    // Mock API call
    setTimeout(() => {
      // Success
      submitButton.disabled = false;
      submitButton.textContent = originalText;

      if (window.utils && window.utils.showToast) {
        window.utils.showToast('저장되었습니다.', 'success');
      }

      // Reset form (optional)
      // this.form.reset();

      // Redirect (optional)
      // window.location.href = '/success';
    }, 1500);
  }
}

// Character Counter
class CharacterCounter {
  constructor(textarea) {
    this.textarea = textarea;
    if (!this.textarea) return;

    this.maxLength = this.textarea.getAttribute('maxlength');
    if (!this.maxLength) return;

    this.counter = document.createElement('div');
    this.counter.className = 'character-counter';
    this.counter.style.cssText = `
      text-align: right;
      margin-top: 4px;
      font-size: 0.875rem;
      color: var(--color-text-tertiary);
    `;

    this.textarea.parentElement.appendChild(this.counter);
    this.update();

    this.textarea.addEventListener('input', () => this.update());
  }

  update() {
    const current = this.textarea.value.length;
    const max = this.maxLength;
    this.counter.textContent = `${current} / ${max}`;

    if (current > max * 0.9) {
      this.counter.style.color = 'var(--color-warning)';
    } else {
      this.counter.style.color = 'var(--color-text-tertiary)';
    }
  }
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  // Initialize form validators
  document.querySelectorAll('form[data-validate]').forEach(form => {
    new FormValidator(form);
  });

  // Initialize character counters
  document.querySelectorAll('textarea[maxlength]').forEach(textarea => {
    new CharacterCounter(textarea);
  });

  // Checkbox agreement validation
  const checkboxes = document.querySelectorAll('input[type="checkbox"][required]');
  checkboxes.forEach(checkbox => {
    const label = checkbox.parentElement.querySelector('label') || checkbox.nextElementSibling;
    if (label) {
      label.setAttribute('aria-required', 'true');
    }
  });
});
