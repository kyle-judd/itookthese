import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Photo } from '../models/photo.model';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/photos';

  getPhotos(categoryId?: number, featured?: boolean): Observable<Photo[]> {
    let params = new HttpParams();
    if (categoryId != null) params = params.set('categoryId', categoryId);
    if (featured != null) params = params.set('isFeatured', featured);
    return this.http.get<Photo[]>(this.apiUrl, { params });
  }

  getPhoto(id: number): Observable<Photo> {
    return this.http.get<Photo>(`${this.apiUrl}/${id}`);
  }
}
