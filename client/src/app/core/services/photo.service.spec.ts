import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { PhotoService } from './photo.service';
import { Photo } from '../models/photo.model';

describe('PhotoService', () => {
  let service: PhotoService;
  let httpMock: HttpTestingController;

  const mockPhotos: Photo[] = [
    {
      id: 1,
      title: 'Sunset',
      description: 'A beautiful sunset',
      thumbUrl: '/img/thumb/1.jpg',
      placeholderBase64: null,
      mediumUrl: '/img/med/1.jpg',
      fullUrl: '/img/full/1.jpg',
      category: 'Landscape',
      categoryId: 2,
      isFeatured: true,
      sortOrder: 1,
      width: 4000,
      height: 3000,
      cameraModel: 'Canon R5',
      lens: 'RF 24-70mm',
      focalLength: '35mm',
      aperture: 'f/2.8',
      shutterSpeed: '1/250',
      iso: '200',
      createdAt: '2025-06-20T10:00:00'
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(PhotoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getPhotos', () => {
    it('should fetch all photos without params', () => {
      service.getPhotos().subscribe(photos => {
        expect(photos).toEqual(mockPhotos);
      });

      const req = httpMock.expectOne('/api/v1/photos');
      expect(req.request.method).toBe('GET');
      expect(req.request.params.keys().length).toBe(0);
      req.flush(mockPhotos);
    });

    it('should include categoryId param when provided', () => {
      service.getPhotos(2).subscribe(photos => {
        expect(photos).toEqual(mockPhotos);
      });

      const req = httpMock.expectOne(r => r.url === '/api/v1/photos');
      expect(req.request.params.get('categoryId')).toBe('2');
      expect(req.request.params.has('isFeatured')).toBeFalse();
      req.flush(mockPhotos);
    });

    it('should include isFeatured param when provided', () => {
      service.getPhotos(undefined, true).subscribe(photos => {
        expect(photos).toEqual(mockPhotos);
      });

      const req = httpMock.expectOne(r => r.url === '/api/v1/photos');
      expect(req.request.params.get('isFeatured')).toBe('true');
      expect(req.request.params.has('categoryId')).toBeFalse();
      req.flush(mockPhotos);
    });

    it('should include both params when both provided', () => {
      service.getPhotos(3, false).subscribe(photos => {
        expect(photos).toEqual(mockPhotos);
      });

      const req = httpMock.expectOne(r => r.url === '/api/v1/photos');
      expect(req.request.params.get('categoryId')).toBe('3');
      expect(req.request.params.get('isFeatured')).toBe('false');
      req.flush(mockPhotos);
    });
  });

  describe('getPhoto', () => {
    it('should fetch a single photo by id', () => {
      const photo = mockPhotos[0];

      service.getPhoto(1).subscribe(result => {
        expect(result).toEqual(photo);
      });

      const req = httpMock.expectOne('/api/v1/photos/1');
      expect(req.request.method).toBe('GET');
      req.flush(photo);
    });
  });
});
