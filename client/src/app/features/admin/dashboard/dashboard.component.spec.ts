import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { AdminService } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { PhotoService } from '../../../core/services/photo.service';
import { CategoryService } from '../../../core/services/category.service';
import { ThemeService } from '../../../core/services/theme.service';
import { Photo } from '../../../core/models/photo.model';
import { ContactSubmission } from '../../../core/models/contact-submission.model';
import { SiteSettings } from '../../../core/models/site-settings.model';
import { of, throwError } from 'rxjs';
const mockPhotos: Photo[] = [
  {
    id: 1,
    title: 'Sunset',
    description: 'Desc',
    thumbUrl: '/thumb/1.jpg',
    placeholderBase64: null,
    mediumUrl: '/med/1.jpg',
    fullUrl: '/full/1.jpg',
    category: 'Nature',
    categoryId: 1,
    isFeatured: true,
    sortOrder: 0,
    width: 4000,
    height: 3000,
    cameraModel: null,
    lens: null,
    focalLength: null,
    aperture: null,
    shutterSpeed: null,
    iso: null,
    createdAt: '2025-01-01'
  }
];

const mockSubmissions: ContactSubmission[] = [
  { id: 1, name: 'Jane', subject: 'Hi', email: 'jane@test.com', message: 'Hello!', isRead: false, createdAt: '2025-01-01' },
  { id: 2, name: 'Bob', subject: 'Q', email: 'bob@test.com', message: 'Question', isRead: true, createdAt: '2025-01-02' }
];

const mockSettings: SiteSettings = {
  siteTitle: 'My Site',
  siteDescription: 'Description',
  contactEmail: 'me@test.com',
  socialLink: 'https://instagram.com'
};

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockAdminService: jasmine.SpyObj<AdminService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockPhotoService: jasmine.SpyObj<PhotoService>;
  let mockCategoryService: jasmine.SpyObj<CategoryService>;

  beforeEach(async () => {
    mockAdminService = jasmine.createSpyObj('AdminService', [
      'uploadPhoto', 'updatePhoto', 'deletePhoto', 'reorderPhotos',
      'getSubmissions', 'markAsRead', 'getSettings', 'updateSettings'
    ]);
    mockAdminService.getSubmissions.and.returnValue(of(mockSubmissions));
    mockAdminService.getSettings.and.returnValue(of(mockSettings));
    mockAdminService.deletePhoto.and.returnValue(of(void 0));
    mockAdminService.updatePhoto.and.returnValue(of(mockPhotos[0]));
    mockAdminService.markAsRead.and.returnValue(of({ ...mockSubmissions[0], isRead: true }));
    mockAdminService.updateSettings.and.returnValue(of(mockSettings));

    mockAuthService = jasmine.createSpyObj('AuthService', ['logout']);
    mockPhotoService = jasmine.createSpyObj('PhotoService', ['getPhotos']);
    mockPhotoService.getPhotos.and.returnValue(of(mockPhotos));
    mockCategoryService = jasmine.createSpyObj('CategoryService', ['getCategories']);
    mockCategoryService.getCategories.and.returnValue(of([{ id: 1, name: 'Nature', slug: 'nature' }]));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AdminService, useValue: mockAdminService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: PhotoService, useValue: mockPhotoService },
        { provide: CategoryService, useValue: mockCategoryService },
        { provide: ThemeService, useValue: { theme: signal('light'), toggleTheme: jasmine.createSpy() } },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load photos on init', () => {
    expect(mockPhotoService.getPhotos).toHaveBeenCalled();
    expect(component.photos().length).toBe(1);
  });

  it('should load submissions on init', () => {
    expect(mockAdminService.getSubmissions).toHaveBeenCalled();
    expect(component.submissions().length).toBe(2);
  });

  it('should load settings on init', () => {
    expect(mockAdminService.getSettings).toHaveBeenCalled();
    expect(component.settingsForm.value.siteTitle).toBe('My Site');
  });

  it('should default to photos tab', () => {
    expect(component.activeTab()).toBe('photos');
  });

  it('should switch tabs', () => {
    component.setTab('submissions');
    expect(component.activeTab()).toBe('submissions');

    component.setTab('settings');
    expect(component.activeTab()).toBe('settings');
  });

  it('should call authService.logout on logout', () => {
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
  });

  it('should compute unreadCount', () => {
    expect(component.unreadCount).toBe(1);
  });

  it('should render the admin header', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Admin');
  });

  it('should render the sign out button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const buttons = compiled.querySelectorAll('header button');
    const signOutBtn = Array.from(buttons).find(b => b.textContent?.includes('Sign out'));
    expect(signOutBtn).toBeTruthy();
  });

  it('should render photo count in tab', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Photos (1)');
  });

  it('should delete a photo from the list', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    component.deletePhoto(1);
    expect(mockAdminService.deletePhoto).toHaveBeenCalledWith(1);
    expect(component.photos().length).toBe(0);
  });

  it('should not delete when confirm is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deletePhoto(1);
    expect(mockAdminService.deletePhoto).not.toHaveBeenCalled();
  });

  it('should mark submission as read', () => {
    component.markAsRead(1);
    expect(mockAdminService.markAsRead).toHaveBeenCalledWith(1);
  });

  it('should save settings', () => {
    component.settingsForm.patchValue({
      siteTitle: 'Updated',
      siteDescription: 'New desc',
      contactEmail: 'new@test.com'
    });
    component.saveSettings();
    expect(mockAdminService.updateSettings).toHaveBeenCalled();
  });

  it('should not save settings when form is invalid', () => {
    component.settingsForm.controls.siteTitle.setValue('');
    component.saveSettings();
    expect(mockAdminService.updateSettings).not.toHaveBeenCalled();
  });

  it('should set settingsSaved after successful save', fakeAsync(() => {
    component.saveSettings();
    expect(component.settingsSaved()).toBeTrue();
    tick(2500);
    expect(component.settingsSaved()).toBeFalse();
  }));

  it('should show submissions tab content when switched', () => {
    component.setTab('submissions');
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Jane');
    expect(compiled.textContent).toContain('jane@test.com');
  });

  it('should show settings form when settings tab is active', () => {
    component.setTab('settings');
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('form')).toBeTruthy();
    expect(compiled.textContent).toContain('Site Title');
  });

  it('should show error toast when upload fails', fakeAsync(async () => {
    const file = new File(['data'], 'photo.jpg', { type: 'image/jpeg' });
    spyOn(component, 'compressImage').and.returnValue(Promise.resolve(file));
    const errorResponse = { status: 415, message: 'Unsupported' } as any;
    mockAdminService.uploadPhoto.and.returnValue(throwError(() => errorResponse));
    const event = { target: { files: [file], value: '' } } as unknown as Event;
    await component.onFileSelected(event);
    tick();
    expect(component.error()).toContain('Unsupported file type');
    expect(component.uploadLoading()).toBeFalse();
    tick(5000);
    expect(component.error()).toBeNull();
  }));

  it('should show generic error for unknown status codes', fakeAsync(async () => {
    const file = new File(['data'], 'photo.jpg', { type: 'image/jpeg' });
    spyOn(component, 'compressImage').and.returnValue(Promise.resolve(file));
    const errorResponse = { status: 500, message: 'Server error' } as any;
    mockAdminService.uploadPhoto.and.returnValue(throwError(() => errorResponse));
    const event = { target: { files: [file], value: '' } } as unknown as Event;
    await component.onFileSelected(event);
    tick();
    expect(component.error()).toBe('Something went wrong. Please try again.');
    tick(5000);
  }));

  it('should load categories on init', () => {
    expect(mockCategoryService.getCategories).toHaveBeenCalled();
    expect(component.categories().length).toBe(1);
  });

  it('should start editing a photo', () => {
    component.startEditing(mockPhotos[0]);
    expect(component.editingPhotoId()).toBe(1);
    expect(component.editTitle).toBe('Sunset');
    expect(component.editDescription).toBe('Desc');
    expect(component.editCategoryId).toBe(1);
  });

  it('should cancel editing', () => {
    component.startEditing(mockPhotos[0]);
    component.cancelEditing();
    expect(component.editingPhotoId()).toBeNull();
  });

  it('should save photo edits', () => {
    component.startEditing(mockPhotos[0]);
    component.editTitle = 'New Title';
    component.savePhoto(mockPhotos[0]);
    expect(mockAdminService.updatePhoto).toHaveBeenCalledWith(1, jasmine.objectContaining({ title: 'New Title' }));
  });
});
