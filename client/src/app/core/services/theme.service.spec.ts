import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove('dark');

    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeService);
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove('dark');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('initial theme', () => {
    it('should default to light when nothing in localStorage', () => {
      expect(service.theme()).toBe('light');
    });

    it('should load dark theme from localStorage', () => {
      localStorage.setItem('theme', 'dark');

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({});
      const freshService = TestBed.inject(ThemeService);

      expect(freshService.theme()).toBe('dark');
    });

    it('should default to light for unknown localStorage value', () => {
      localStorage.setItem('theme', 'invalid');

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({});
      const freshService = TestBed.inject(ThemeService);

      expect(freshService.theme()).toBe('light');
    });
  });

  describe('toggleTheme', () => {
    it('should toggle from light to dark', () => {
      expect(service.theme()).toBe('light');
      service.toggleTheme();
      expect(service.theme()).toBe('dark');
    });

    it('should toggle from dark to light', () => {
      service.toggleTheme(); // light -> dark
      service.toggleTheme(); // dark -> light
      expect(service.theme()).toBe('light');
    });
  });

  describe('effect side effects', () => {
    it('should persist theme to localStorage on change', () => {
      service.toggleTheme();
      TestBed.flushEffects();
      expect(localStorage.getItem('theme')).toBe('dark');

      service.toggleTheme();
      TestBed.flushEffects();
      expect(localStorage.getItem('theme')).toBe('light');
    });

    it('should toggle dark class on document element', () => {
      service.toggleTheme();
      TestBed.flushEffects();
      expect(document.documentElement.classList.contains('dark')).toBeTrue();

      service.toggleTheme();
      TestBed.flushEffects();
      expect(document.documentElement.classList.contains('dark')).toBeFalse();
    });
  });
});
