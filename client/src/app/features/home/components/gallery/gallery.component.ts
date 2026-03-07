import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { PhotoCardComponent } from '../photo-card/photo-card.component';
import { PhotoModalComponent } from '../photo-modal/photo-modal.component';
import { PhotoService } from '../../../../core/services/photo.service';
import { Photo } from '../../../../core/models/photo.model';
import { InViewDirective } from '../../../../shared/directives/in-view.directive';

@Component({
  selector: 'app-gallery',
  imports: [PhotoCardComponent, PhotoModalComponent, InViewDirective],
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.scss'
})
export class GalleryComponent {
  private readonly photoService = inject(PhotoService);

  photos = toSignal(this.photoService.getPhotos(), { initialValue: [] });
  selectedPhoto = signal<Photo | null>(null);

  openModal(photo: Photo): void {
    this.selectedPhoto.set(photo);
  }

  closeModal(): void {
    this.selectedPhoto.set(null);
  }
}
