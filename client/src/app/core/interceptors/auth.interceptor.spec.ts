import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should add Authorization header to admin API requests when token exists', () => {
    localStorage.setItem('admin_token', 'my-jwt-token');

    httpClient.get('/api/v1/admin/photos').subscribe();

    const req = httpMock.expectOne('/api/v1/admin/photos');
    expect(req.request.headers.get('Authorization')).toBe('Bearer my-jwt-token');
    req.flush([]);
  });

  it('should not add Authorization header to non-admin API requests', () => {
    localStorage.setItem('admin_token', 'my-jwt-token');

    httpClient.get('/api/v1/photos').subscribe();

    const req = httpMock.expectOne('/api/v1/photos');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });

  it('should not add Authorization header when no token exists', () => {
    httpClient.get('/api/v1/admin/settings').subscribe();

    const req = httpMock.expectOne('/api/v1/admin/settings');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should pass through non-admin requests untouched when no token', () => {
    httpClient.get('/api/v1/categories').subscribe();

    const req = httpMock.expectOne('/api/v1/categories');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });

  it('should add header to admin sub-paths', () => {
    localStorage.setItem('admin_token', 'token-123');

    httpClient.patch('/api/v1/admin/contact-submissions/1/read', {}).subscribe();

    const req = httpMock.expectOne('/api/v1/admin/contact-submissions/1/read');
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-123');
    req.flush({});
  });
});
