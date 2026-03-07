import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ContactService } from './contact.service';

describe('ContactService', () => {
  let service: ContactService;
  let httpMock: HttpTestingController;

  const mockRequest = {
    name: 'John Doe',
    email: 'john@example.com',
    subject: 'Booking inquiry',
    message: 'I would like to book a session.',
    honeypot: ''
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ContactService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('submit', () => {
    it('should POST the contact form data', () => {
      service.submit(mockRequest).subscribe();

      const req = httpMock.expectOne('/api/v1/contact');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      req.flush(null);
    });

    it('should propagate server errors', () => {
      service.submit(mockRequest).subscribe({
        error: (err) => {
          expect(err.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/v1/contact');
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });
});
