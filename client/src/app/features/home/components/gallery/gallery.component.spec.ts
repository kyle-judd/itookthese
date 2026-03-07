import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GalleryComponent } from './gallery.component';
import { PhotoService } from '../../../../core/services/photo.service';
import { Photo } from '../../../../core/models/photo.model';
import { of } from 'rxjs';
import { Component, input, output } from '@angular/core';
import { PhotoCardComponent } from '../photo-card/photo-card.component';
import { PhotoModalComponent } from '../photo-modal/photo-modal.component';

// Stub child components to isolate gallery testing
@Component({
  selector: 'app-photo-card',
  template: '<div class="photo-card-stub" (click)="photoSelected.emit(photo())">{{ photo().title }}</div>'
})
class PhotoCardStubComponent {
  photo = input.required<Photo>();
  photoSelected = output<Photo>();
}

@Component({
  selector: 'app-photo-modal',
  template: '<div class="photo-modal-stub"><button class="close-btn" (click)="closed.emit()">Close</button></div>'
})
class PhotoModalStubComponent {
  photo = input.required<Photo>();
  photos = input<Photo[]>([]);
  closed = output<void>();
  photoChanged = output<Photo>();
}

const mockPhotos: Photo[] = [
  {
    id: 1,
    title: 'Sunset',
    description: 'A beautiful sunset',
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
  },
  {
    id: 2,
    title: 'City Lights',
    description: null,
    thumbUrl: '/img/city-thumb.jpg',
    placeholderBase64: null,
    mediumUrl: '/img/city-medium.jpg',
    fullUrl: '/img/city-full.jpg',
    category: 'Urban',
    categoryId: 2,
    isFeatured: false,
    sortOrder: 2,
    width: 4032,
    height: 3024,
    cameraModel: null,
    lens: null,
    focalLength: null,
    aperture: null,
    shutterSpeed: null,
    iso: null,
    createdAt: '2025-07-01'
  }
];

describe('GalleryComponent', () => {
  let component: GalleryComponent;
  let fixture: ComponentFixture<GalleryComponent>;
  let mockPhotoService: jasmine.SpyObj<PhotoService>;

  beforeEach(async () => {
    mockPhotoService = jasmine.createSpyObj('PhotoService', ['getPhotos']);
    mockPhotoService.getPhotos.and.returnValue(of(mockPhotos));

    await TestBed.configureTestingModule({
      imports: [GalleryComponent],
      providers: [
        { provide: PhotoService, useValue: mockPhotoService }
      ]
    })
    .overrideComponent(GalleryComponent, {
      remove: { imports: [PhotoCardComponent, PhotoModalComponent] },
      add: { imports: [PhotoCardStubComponent, PhotoModalStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(GalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load photos from the service', () => {
    expect(mockPhotoService.getPhotos).toHaveBeenCalled();
    expect(component.photos().length).toBe(2);
  });

  it('should render a photo card for each photo', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const cards = compiled.querySelectorAll('.photo-card-stub');
    expect(cards.length).toBe(2);
  });

  it('should render gallery heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Gallery');
  });

  it('should show empty message when no photos', () => {
    mockPhotoService.getPhotos.and.returnValue(of([]));
    // Recreate to use new observable
    fixture = TestBed.createComponent(GalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('No photos yet');
  });

  it('should set selectedPhoto when openModal is called', () => {
    expect(component.selectedPhoto()).toBeNull();
    component.openModal(mockPhotos[0]);
    expect(component.selectedPhoto()).toEqual(mockPhotos[0]);
  });

  it('should clear selectedPhoto when closeModal is called', () => {
    component.openModal(mockPhotos[0]);
    expect(component.selectedPhoto()).toBeTruthy();
    component.closeModal();
    expect(component.selectedPhoto()).toBeNull();
  });

  it('should show modal when a photo is selected', () => {
    component.openModal(mockPhotos[0]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const modal = compiled.querySelector('.photo-modal-stub');
    expect(modal).toBeTruthy();
  });

  it('should not show modal when no photo is selected', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const modal = compiled.querySelector('.photo-modal-stub');
    expect(modal).toBeNull();
  });
});
