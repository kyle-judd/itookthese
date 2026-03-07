import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AboutComponent } from './about.component';

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have three features', () => {
    expect(component.features.length).toBe(3);
  });

  it('should render the about heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('About This Portfolio');
  });

  it('should render a card for each feature', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const titles = compiled.querySelectorAll('h3');
    expect(titles.length).toBe(4); // 3 features + 1 philosophy heading
    expect(titles[0].textContent).toContain('iPhone Only');
    expect(titles[1].textContent).toContain('Unique Perspective');
    expect(titles[2].textContent).toContain('Passion Project');
  });

  it('should render the philosophy section', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('My Philosophy');
    expect(compiled.textContent).toContain('The best camera is the one you have with you');
  });
});
