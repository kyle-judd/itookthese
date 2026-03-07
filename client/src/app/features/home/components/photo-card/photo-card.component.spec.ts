import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PhotoCardComponent } from './photo-card.component';
import { Photo } from '../../../../core/models/photo.model';

const mockPhoto: Photo = {
  id: 1,
  title: 'Sunset Beach',
  description: 'A beautiful sunset at the beach',
  thumbUrl: '/img/sunset-thumb.jpg',
  placeholderBase64: null,
  mediumUrl: '/img/sunset-medium.jpg',
  fullUrl: '/img/sunset-full.jpg',
  category: 'Nature',
  categoryId: 1,
  isFeatured: true,
  sortOrder: 1,
  width: 4032,
  height: 3024,
  cameraModel: 'iPhone 15 Pro',
  lens: null,
  focalLength: '24mm',
  aperture: 'f/1.8',
  shutterSpeed: '1/1000',
  iso: '100',
  createdAt: '2025-06-20'
};

describe('PhotoCardComponent', () => {
  let component: PhotoCardComponent;
  let fixture: ComponentFixture<PhotoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhotoCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(PhotoCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('photo', mockPhoto);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the photo image using mediumUrl', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const imgs = compiled.querySelectorAll('img');
    const mainImg = imgs[imgs.length - 1] as HTMLImageElement;
    expect(mainImg).toBeTruthy();
    expect(mainImg.src).toContain(mockPhoto.mediumUrl);
    expect(mainImg.alt).toBe(mockPhoto.title);
  });

  it('should not show placeholder when placeholderBase64 is null', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const imgs = compiled.querySelectorAll('img');
    expect(imgs.length).toBe(1);
  });

  it('should set imageLoaded to true on image load', () => {
    expect(component.imageLoaded()).toBeFalse();
    component.onImageLoad();
    expect(component.imageLoaded()).toBeTrue();
  });

  it('should render the photo title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('h3');
    expect(title?.textContent).toContain('Sunset Beach');
  });

  it('should render the photo category', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nature');
  });

  it('should use lazy loading on the image', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const img = compiled.querySelector('img') as HTMLImageElement;
    expect(img.loading).toBe('lazy');
  });

  it('should emit photoSelected with the photo when clicked', () => {
    spyOn(component.photoSelected, 'emit');
    const compiled = fixture.nativeElement as HTMLElement;
    const card = compiled.querySelector('div') as HTMLDivElement;
    card.click();
    expect(component.photoSelected.emit).toHaveBeenCalledWith(mockPhoto);
  });
});
