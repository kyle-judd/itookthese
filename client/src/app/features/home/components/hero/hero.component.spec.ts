import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeroComponent } from './hero.component';

describe('HeroComponent', () => {
  let component: HeroComponent;
  let fixture: ComponentFixture<HeroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeroComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HeroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the main headline', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Capturing Moments');
    expect(compiled.querySelector('h2')?.textContent).toContain('Through the Lens');
  });

  it('should render the subtitle text', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const subtitle = compiled.querySelector('p');
    expect(subtitle?.textContent).toContain('Exploring the world with an iPhone camera');
  });

  it('should have a gallery CTA button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const cta = compiled.querySelector('button');
    expect(cta).toBeTruthy();
    expect(cta?.textContent).toContain('View Gallery');
  });

  it('should render the camera icon', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const svgs = compiled.querySelectorAll('svg');
    expect(svgs.length).toBeGreaterThan(0);
  });

  it('should have gradient orbs for visual effect', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const orbs = compiled.querySelectorAll('[class*="animate-orb"]');
    expect(orbs.length).toBe(2);
  });
});
