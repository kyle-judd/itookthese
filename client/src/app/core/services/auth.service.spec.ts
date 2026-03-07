import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    localStorage.clear();
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('isLoggedIn', () => {
    it('should be false when no token in localStorage', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('should be true when token exists in localStorage at creation time', () => {
      localStorage.setItem('admin_token', 'existing-token');

      // Re-create service so the signal reads the token
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          { provide: Router, useValue: routerSpy }
        ]
      });
      const freshService = TestBed.inject(AuthService);

      expect(freshService.isLoggedIn()).toBeTrue();
    });
  });

  describe('login', () => {
    it('should POST credentials and store token', () => {
      const mockResponse = { token: 'test-jwt-token' };

      service.login('admin', 'password123').subscribe(res => {
        expect(res.token).toBe('test-jwt-token');
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ username: 'admin', password: 'password123' });
      req.flush(mockResponse);

      expect(localStorage.getItem('admin_token')).toBe('test-jwt-token');
      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should not set token on HTTP error', () => {
      service.login('admin', 'wrong').subscribe({
        error: () => {
          expect(localStorage.getItem('admin_token')).toBeNull();
          expect(service.isLoggedIn()).toBeFalse();
        }
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout', () => {
    it('should remove token, set isLoggedIn to false, and navigate to login', () => {
      localStorage.setItem('admin_token', 'some-token');
      service.isLoggedIn.set(true);

      service.logout();

      expect(localStorage.getItem('admin_token')).toBeNull();
      expect(service.isLoggedIn()).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/admin/login']);
    });
  });

  describe('getToken', () => {
    it('should return null when no token exists', () => {
      expect(service.getToken()).toBeNull();
    });

    it('should return the stored token', () => {
      localStorage.setItem('admin_token', 'my-token');
      expect(service.getToken()).toBe('my-token');
    });
  });
});
