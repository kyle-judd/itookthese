import { Component, inject, signal, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import imageCompression from 'browser-image-compression';
import { AdminService } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { PhotoService } from '../../../core/services/photo.service';
import { CategoryService } from '../../../core/services/category.service';
import { ThemeService } from '../../../core/services/theme.service';
import { Photo } from '../../../core/models/photo.model';
import { Category } from '../../../core/models/category.model';
import { ContactSubmission } from '../../../core/models/contact-submission.model';
import { SiteSettings } from '../../../core/models/site-settings.model';

type Tab = 'photos' | 'submissions' | 'settings';

@Component({
  selector: 'app-dashboard',
  imports: [ReactiveFormsModule, DatePipe, DragDropModule, FormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly photoService = inject(PhotoService);
  private readonly authService = inject(AuthService);
  private readonly categoryService = inject(CategoryService);
  readonly themeService = inject(ThemeService);
  private readonly fb = inject(FormBuilder);

  activeTab = signal<Tab>('photos');

  // Error toast
  error = signal<string | null>(null);

  // Photos
  photos = signal<Photo[]>([]);
  uploadLoading = signal(false);
  categories = signal<Category[]>([]);
  editingPhotoId = signal<number | null>(null);
  editTitle = '';
  editDescription = '';
  editCategoryId: number | null = null;

  // Submissions
  submissions = signal<ContactSubmission[]>([]);

  // Settings
  settingsForm = this.fb.nonNullable.group({
    siteTitle:       ['', Validators.required],
    siteDescription: ['', Validators.required],
    contactEmail:    ['', [Validators.required, Validators.email]],
    socialLink:      ['']
  });
  settingsSaving = signal(false);
  settingsSaved = signal(false);

  ngOnInit(): void {
    this.loadPhotos();
    this.loadSubmissions();
    this.loadSettings();
    this.loadCategories();
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
  }

  logout(): void {
    this.authService.logout();
  }

  private showError(err: HttpErrorResponse): void {
    const messages: Record<number, string> = {
      400: 'Invalid request. Please check your input.',
      403: 'Session expired. Please log in again.',
      413: 'File is too large. Maximum size is 50MB.',
      415: 'Unsupported file type. Use JPEG, PNG, or HEIC.',
      429: 'Too many requests. Please wait and try again.',
    };
    this.error.set(messages[err.status] ?? 'Something went wrong. Please try again.');
    setTimeout(() => this.error.set(null), 5000);
  }

  // --- Photos ---

  loadPhotos(): void {
    this.photoService.getPhotos().subscribe(photos => this.photos.set(photos));
  }

  async onFileSelected(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadLoading.set(true);

    try {
      const compressed = await this.compressImage(file);

      const uploadFile = new File([compressed], file.name, { type: compressed.type });

      this.adminService.uploadPhoto(uploadFile).subscribe({
        next: photo => {
          this.photos.update(list => [...list, photo]);
          this.uploadLoading.set(false);
          input.value = '';
        },
        error: (err) => {
          this.uploadLoading.set(false);
          this.showError(err);
          input.value = '';
        }
      });
    } catch {
      this.uploadLoading.set(false);
      this.error.set('Failed to process image. Please try a different file.');
      setTimeout(() => this.error.set(null), 5000);
      input.value = '';
    }
  }

  compressImage(file: File): Promise<File> {
    return imageCompression(file, {
      maxSizeMB: 10,
      maxWidthOrHeight: 4000,
      useWebWorker: true,
      preserveExif: true
    }) as Promise<File>;
  }

  deletePhoto(id: number): void {
    if (!confirm('Delete this photo? This cannot be undone.')) return;

    this.adminService.deletePhoto(id).subscribe({
      next: () => this.photos.update(list => list.filter(p => p.id !== id)),
      error: (err) => this.showError(err)
    });
  }

  dropPhoto(event: CdkDragDrop<Photo[]>): void {
    const reordered = [...this.photos()];
    moveItemInArray(reordered, event.previousIndex, event.currentIndex);
    this.photos.set(reordered);
    this.adminService.reorderPhotos(reordered.map(p => p.id)).subscribe({
      error: (err) => this.showError(err)
    });
  }

  toggleFeatured(photo: Photo): void {
    this.adminService.updatePhoto(photo.id, { featured: !photo.isFeatured }).subscribe({
      next: (updated) => this.photos.update(list => list.map(p => p.id === updated.id ? updated : p)),
      error: (err) => this.showError(err)
    });
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe(cats => this.categories.set(cats));
  }

  startEditing(photo: Photo): void {
    this.editingPhotoId.set(photo.id);
    this.editTitle = photo.title;
    this.editDescription = photo.description ?? '';
    this.editCategoryId = photo.categoryId;
  }

  cancelEditing(): void {
    this.editingPhotoId.set(null);
  }

  savePhoto(photo: Photo): void {
    this.adminService.updatePhoto(photo.id, {
      title: this.editTitle,
      description: this.editDescription || undefined,
      categoryId: this.editCategoryId,
    }).subscribe({
      next: (updated) => {
        this.photos.update(list => list.map(p => p.id === updated.id ? updated : p));
        this.editingPhotoId.set(null);
      },
      error: (err) => this.showError(err)
    });
  }

  // --- Submissions ---

  loadSubmissions(): void {
    this.adminService.getSubmissions().subscribe(data => this.submissions.set(data));
  }

  markAsRead(id: number): void {
    this.adminService.markAsRead(id).subscribe(updated => {
      this.submissions.update(list => list.map(s => s.id === updated.id ? updated : s));
    });
  }

  get unreadCount(): number {
    return this.submissions().filter(s => !s.isRead).length;
  }

  // --- Settings ---

  loadSettings(): void {
    this.adminService.getSettings().subscribe(s => this.settingsForm.patchValue(s));
  }

  saveSettings(): void {
    if (this.settingsForm.invalid || this.settingsSaving()) return;

    this.settingsSaving.set(true);
    this.adminService.updateSettings(this.settingsForm.getRawValue() as SiteSettings).subscribe({
      next: () => {
        this.settingsSaving.set(false);
        this.settingsSaved.set(true);
        setTimeout(() => this.settingsSaved.set(false), 2500);
      },
      error: (err) => {
        this.settingsSaving.set(false);
        this.showError(err);
      }
    });
  }
}
