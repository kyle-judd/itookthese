import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PhotoModalComponent } from './photo-modal.component';
import { Photo } from '../../../../core/models/photo.model';
import { provideRouter } from '@angular/router';

const mockPhoto: Photo = {
  id: 42,
  title: 'Mountain Vista',
  description: 'A panoramic mountain view',
  thumbUrl: '/img/mountain-thumb.jpg',
  placeholderBase64: null,
  mediumUrl: '/img/mountain-medium.jpg',
  fullUrl: '/img/mountain-full.jpg',
  category: 'Landscape',
  categoryId: 3,
  isFeatured: true,
  sortOrder: 1,
  width: 4032,
  height: 3024,
  cameraModel: 'iPhone 15 Pro',
  lens: null,
  focalLength: '24mm',
  aperture: 'f/2.8',
  shutterSpeed: '1/500',
  iso: '64',
  createdAt: '2025-08-12'
};

describe('PhotoModalComponent', () => {
  let component: PhotoModalComponent;
  let fixture: ComponentFixture<PhotoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhotoModalComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(PhotoModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('photo', mockPhoto);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the photo image with full URL', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const img = compiled.querySelector('img') as HTMLImageElement;
    expect(img).toBeTruthy();
    expect(img.src).toContain(mockPhoto.fullUrl);
    expect(img.alt).toBe(mockPhoto.title);
  });

  it('should display the photo title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('h3');
    expect(title?.textContent).toContain('Mountain Vista');
  });

  it('should display the photo category', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Landscape');
  });

  it('should have a View details link pointing to the photo detail page', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const link = compiled.querySelector('a');
    expect(link).toBeTruthy();
    expect(link?.textContent).toContain('View details');
    expect(link?.getAttribute('href')).toBe('/photo/42');
  });

  it('should have a close button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const closeBtn = compiled.querySelector('button[aria-label="Close"]');
    expect(closeBtn).toBeTruthy();
  });

  it('should emit closed when the close button is clicked', () => {
    spyOn(component.closed, 'emit');
    const compiled = fixture.nativeElement as HTMLElement;
    const closeBtn = compiled.querySelector('button[aria-label="Close"]') as HTMLButtonElement;
    closeBtn.click();
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should emit closed when the backdrop is clicked', () => {
    spyOn(component.closed, 'emit');
    const compiled = fixture.nativeElement as HTMLElement;
    const backdrop = compiled.querySelector('.fixed') as HTMLDivElement;
    backdrop.click();
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should not emit closed when the content area is clicked', () => {
    spyOn(component.closed, 'emit');
    const compiled = fixture.nativeElement as HTMLElement;
    const content = compiled.querySelector('.relative.flex.flex-col') as HTMLDivElement;
    content.click();
    expect(component.closed.emit).not.toHaveBeenCalled();
  });
});
