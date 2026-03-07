import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Photo } from '../models/photo.model';
import { ContactSubmission } from '../models/contact-submission.model';
import { SiteSettings } from '../models/site-settings.model';

interface PhotoUpdateRequest {
  title?: string;
  description?: string;
  categoryId?: number | null;
  featured?: boolean;
  sortOrder?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly photosUrl = '/api/v1/admin/photos';
  private readonly contactUrl = '/api/v1/admin/contact-submissions';
  private readonly settingsUrl = '/api/v1/admin/settings';

  // Photos
  uploadPhoto(file: File): Observable<Photo> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<Photo>(this.photosUrl, form);
  }

  updatePhoto(id: number, body: PhotoUpdateRequest): Observable<Photo> {
    return this.http.put<Photo>(`${this.photosUrl}/${id}`, body);
  }

  deletePhoto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.photosUrl}/${id}`);
  }

  reorderPhotos(ids: number[]): Observable<void> {
    return this.http.put<void>(`${this.photosUrl}/reorder`, ids);
  }

  // Contact submissions
  getSubmissions(): Observable<ContactSubmission[]> {
    return this.http.get<ContactSubmission[]>(this.contactUrl);
  }

  markAsRead(id: number): Observable<ContactSubmission> {
    return this.http.patch<ContactSubmission>(`${this.contactUrl}/${id}/read`, {});
  }

  // Site settings
  getSettings(): Observable<SiteSettings> {
    return this.http.get<SiteSettings>(this.settingsUrl);
  }

  updateSettings(settings: SiteSettings): Observable<SiteSettings> {
    return this.http.put<SiteSettings>(this.settingsUrl, settings);
  }
}
