import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { HeaderComponent } from './header.component';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';
import { signal } from '@angular/core';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let mockThemeService: jasmine.SpyObj<ThemeService> & { theme: ReturnType<typeof signal> };

  beforeEach(async () => {
    const themeSignal = signal<'light' | 'dark'>('light');
    mockThemeService = {
      ...jasmine.createSpyObj('ThemeService', ['toggleTheme']),
      theme: themeSignal,
    } as any;

    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [
        { provide: ThemeService, useValue: mockThemeService },
        { provide: AuthService, useValue: { isLoggedIn: signal(false) } },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the site title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('iTookThese');
  });

  it('should render navigation links', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const links = compiled.querySelectorAll('a.nav-link');
    expect(links.length).toBe(2);
    expect(links[0].textContent).toContain('Gallery');
    expect(links[1].textContent).toContain('About');
  });

  it('should not show admin link when logged out', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const adminLink = compiled.querySelector('a[aria-label="Admin"]');
    expect(adminLink).toBeNull();
  });

  it('should show admin link when logged in', () => {
    const authService = TestBed.inject(AuthService);
    (authService.isLoggedIn as any).set(true);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const adminLink = compiled.querySelector('a[aria-label="Admin"]');
    expect(adminLink).toBeTruthy();
  });

  it('should have a theme toggle button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[aria-label="Toggle theme"]');
    expect(button).toBeTruthy();
  });

  it('should call toggleTheme when the theme button is clicked', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[aria-label="Toggle theme"]') as HTMLButtonElement;
    button.click();
    expect(mockThemeService.toggleTheme).toHaveBeenCalled();
  });

  it('should show moon icon when theme is light', () => {
    mockThemeService.theme.set('light');
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const svg = compiled.querySelector('button[aria-label="Toggle theme"] svg');
    expect(svg).toBeTruthy();
    // Moon icon has a single path with "M12 3a6..." pattern
    const path = svg?.querySelector('path');
    expect(path?.getAttribute('d')).toContain('M12 3a6');
  });

  it('should show sun icon when theme is dark', () => {
    mockThemeService.theme.set('dark');
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const svg = compiled.querySelector('button[aria-label="Toggle theme"] svg');
    expect(svg).toBeTruthy();
    // Sun icon has a circle element
    const circle = svg?.querySelector('circle');
    expect(circle).toBeTruthy();
  });
});
