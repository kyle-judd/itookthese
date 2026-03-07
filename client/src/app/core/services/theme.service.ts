import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly storageKey = 'theme';

  theme = signal<Theme>(this.loadTheme());

  constructor() {
    effect(() => {
      const current = this.theme();
      document.documentElement.classList.toggle('dark', current === 'dark');
      localStorage.setItem(this.storageKey, current);
    });
  }

  toggleTheme(): void {
    this.theme.update(t => (t === 'light' ? 'dark' : 'light'));
  }

  private loadTheme(): Theme {
    const saved = localStorage.getItem(this.storageKey);
    return saved === 'dark' ? 'dark' : 'light';
  }
}
