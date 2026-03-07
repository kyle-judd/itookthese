import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PhotoDetailComponent } from './photo-detail.component';
import { PhotoService } from '../../core/services/photo.service';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, Subject } from 'rxjs';
import { Photo } from '../../core/models/photo.model';
import { Component } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';
import { signal } from '@angular/core';
import { HeaderComponent } from '../../shared/components/header/header.component';

// Stub header to avoid pulling in ThemeService dependency chain
@Component({
  selector: 'app-header',
  template: '<header>Header Stub</header>'
})
class HeaderStubComponent {}

const mockPhoto: Photo = {
  id: 7,
  title: 'Autumn Leaves',
  description: 'Golden autumn foliage',
  thumbUrl: '/img/autumn-thumb.jpg',
  placeholderBase64: null,
  mediumUrl: '/img/autumn-medium.jpg',
  fullUrl: '/img/autumn-full.jpg',
  category: 'Nature',
  categoryId: 1,
  isFeatured: false,
  sortOrder: 3,
  width: 4032,
  height: 3024,
  cameraModel: 'iPhone 15 Pro',
  lens: 'Main Camera',
  focalLength: '24mm',
  aperture: 'f/1.8',
  shutterSpeed: '1/250',
  iso: '200',
  createdAt: '2025-10-20'
};

describe('PhotoDetailComponent', () => {
  let component: PhotoDetailComponent;
  let fixture: ComponentFixture<PhotoDetailComponent>;
  let mockPhotoService: jasmine.SpyObj<PhotoService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockPhotoService = jasmine.createSpyObj('PhotoService', ['getPhoto']);
    mockPhotoService.getPhoto.and.returnValue(of(mockPhoto));

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockRouter.navigate.and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [PhotoDetailComponent],
      providers: [
        { provide: PhotoService, useValue: mockPhotoService },
        { provide: Router, useValue: mockRouter },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ id: '7' }))
          }
        },
        {
          provide: ThemeService,
          useValue: { theme: signal('light'), toggleTheme: jasmine.createSpy() }
        }
      ]
    })
    .overrideComponent(PhotoDetailComponent, {
      remove: { imports: [HeaderComponent] },
      add: { imports: [HeaderStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(PhotoDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch the photo by route param id', () => {
    expect(mockPhotoService.getPhoto).toHaveBeenCalledWith(7);
  });

  it('should expose the loaded photo via signal', () => {
    expect(component.photo()).toEqual(mockPhoto);
  });

  it('should render the photo title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Autumn Leaves');
  });

  it('should render the full resolution image', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const img = compiled.querySelector('img') as HTMLImageElement;
    expect(img).toBeTruthy();
    expect(img.src).toContain(mockPhoto.fullUrl);
  });

  it('should render category when available', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nature');
  });

  it('should render description when available', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Golden autumn foliage');
  });

  it('should render EXIF data', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('iPhone 15 Pro');
    expect(compiled.textContent).toContain('f/1.8');
    expect(compiled.textContent).toContain('1/250');
    expect(compiled.textContent).toContain('200');
    expect(compiled.textContent).toContain('24mm');
  });

  it('should have a back button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const backBtn = compiled.querySelector('button');
    expect(backBtn?.textContent).toContain('Back to Gallery');
  });

  it('should navigate to home when goBack is called', () => {
    component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should show loading state when photo is not yet loaded', () => {
    mockPhotoService.getPhoto.and.returnValue(new Subject<Photo>().asObservable());

    // Recreate component with pending observable
    fixture = TestBed.createComponent(PhotoDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.photo()).toBeUndefined();
    const compiled = fixture.nativeElement as HTMLElement;
    const spinner = compiled.querySelector('.animate-spin');
    expect(spinner).toBeTruthy();
  });
});
