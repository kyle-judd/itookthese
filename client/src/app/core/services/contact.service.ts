import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ContactRequest {
  name: string;
  email: string;
  subject: string;
  message: string;
  honeypot: string;
}

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly http = inject(HttpClient);

  submit(body: ContactRequest): Observable<void> {
    return this.http.post<void>('/api/v1/contact', body);
  }
}
