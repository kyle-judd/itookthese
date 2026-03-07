import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AdminService } from './admin.service';
import { Photo } from '../models/photo.model';
import { ContactSubmission } from '../models/contact-submission.model';
import { SiteSettings } from '../models/site-settings.model';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  const mockPhoto: Photo = {
    id: 1,
    title: 'Test Photo',
    description: 'Description',
    thumbUrl: '/img/thumb/1.jpg',
    placeholderBase64: null,
    mediumUrl: '/img/med/1.jpg',
    fullUrl: '/img/full/1.jpg',
    category: 'Landscape',
    categoryId: 2,
    isFeatured: false,
    sortOrder: 1,
    width: 4000,
    height: 3000,
    cameraModel: null,
    lens: null,
    focalLength: null,
    aperture: null,
    shutterSpeed: null,
    iso: null,
    createdAt: '2025-06-20T10:00:00'
  };

  const mockSubmissions: ContactSubmission[] = [
    {
      id: 1,
      name: 'Jane Doe',
      subject: 'Hello',
      email: 'jane@example.com',
      message: 'Hi there!',
      isRead: false,
      createdAt: '2025-07-01T12:00:00'
    },
    {
      id: 2,
      name: 'Bob Smith',
      subject: 'Question',
      email: 'bob@example.com',
      message: 'Quick question...',
      isRead: true,
      createdAt: '2025-07-02T14:00:00'
    }
  ];

  const mockSettings: SiteSettings = {
    siteTitle: 'My Photography',
    siteDescription: 'A portfolio site',
    contactEmail: 'me@example.com',
    socialLink: 'https://instagram.com/me'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // --- Photos ---

  describe('uploadPhoto', () => {
    it('should POST a file as FormData', () => {
      const file = new File(['image-data'], 'photo.jpg', { type: 'image/jpeg' });

      service.uploadPhoto(file).subscribe(photo => {
        expect(photo).toEqual(mockPhoto);
      });

      const req = httpMock.expectOne('/api/v1/admin/photos');
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBeTrue();
      expect(req.request.body.get('file')).toBeTruthy();
      req.flush(mockPhoto);
    });
  });

  describe('updatePhoto', () => {
    it('should PUT the update body for a given photo id', () => {
      const updateBody = { title: 'Updated Title', featured: true };

      service.updatePhoto(1, updateBody).subscribe(photo => {
        expect(photo).toEqual(mockPhoto);
      });

      const req = httpMock.expectOne('/api/v1/admin/photos/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateBody);
      req.flush(mockPhoto);
    });
  });

  describe('deletePhoto', () => {
    it('should DELETE the photo by id', () => {
      service.deletePhoto(5).subscribe();

      const req = httpMock.expectOne('/api/v1/admin/photos/5');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('reorderPhotos', () => {
    it('should PUT an array of ids to the reorder endpoint', () => {
      const ids = [3, 1, 2];

      service.reorderPhotos(ids).subscribe();

      const req = httpMock.expectOne('/api/v1/admin/photos/reorder');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual([3, 1, 2]);
      req.flush(null);
    });
  });

  // --- Contact Submissions ---

  describe('getSubmissions', () => {
    it('should GET all contact submissions', () => {
      service.getSubmissions().subscribe(submissions => {
        expect(submissions.length).toBe(2);
        expect(submissions).toEqual(mockSubmissions);
      });

      const req = httpMock.expectOne('/api/v1/admin/contact-submissions');
      expect(req.request.method).toBe('GET');
      req.flush(mockSubmissions);
    });
  });

  describe('markAsRead', () => {
    it('should PATCH the submission as read', () => {
      const readSubmission = { ...mockSubmissions[0], isRead: true };

      service.markAsRead(1).subscribe(result => {
        expect(result.isRead).toBeTrue();
      });

      const req = httpMock.expectOne('/api/v1/admin/contact-submissions/1/read');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual({});
      req.flush(readSubmission);
    });
  });

  // --- Site Settings ---

  describe('getSettings', () => {
    it('should GET site settings', () => {
      service.getSettings().subscribe(settings => {
        expect(settings).toEqual(mockSettings);
      });

      const req = httpMock.expectOne('/api/v1/admin/settings');
      expect(req.request.method).toBe('GET');
      req.flush(mockSettings);
    });
  });

  describe('updateSettings', () => {
    it('should PUT updated settings', () => {
      const updated: SiteSettings = { ...mockSettings, siteTitle: 'New Title' };

      service.updateSettings(updated).subscribe(result => {
        expect(result).toEqual(updated);
      });

      const req = httpMock.expectOne('/api/v1/admin/settings');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updated);
      req.flush(updated);
    });
  });
});
