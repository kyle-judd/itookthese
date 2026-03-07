import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CategoryService } from './category.service';
import { Category } from '../models/category.model';

describe('CategoryService', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;

  const mockCategories: Category[] = [
    { id: 1, name: 'Landscape', slug: 'landscape' },
    { id: 2, name: 'Portrait', slug: 'portrait' },
    { id: 3, name: 'Street', slug: 'street' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCategories', () => {
    it('should fetch all categories', () => {
      service.getCategories().subscribe(categories => {
        expect(categories.length).toBe(3);
        expect(categories).toEqual(mockCategories);
      });

      const req = httpMock.expectOne('/api/v1/categories');
      expect(req.request.method).toBe('GET');
      req.flush(mockCategories);
    });

    it('should return empty array when no categories exist', () => {
      service.getCategories().subscribe(categories => {
        expect(categories.length).toBe(0);
        expect(categories).toEqual([]);
      });

      const req = httpMock.expectOne('/api/v1/categories');
      req.flush([]);
    });
  });
});
