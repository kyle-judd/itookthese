import { Component, input, output, signal } from '@angular/core';
import { Photo } from '../../../../core/models/photo.model';

@Component({
  selector: 'app-photo-card',
  imports: [],
  templateUrl: './photo-card.component.html',
  styleUrl: './photo-card.component.scss'
})
export class PhotoCardComponent {
  photo = input.required<Photo>();
  photoSelected = output<Photo>();
  imageLoaded = signal(false);

  onImageLoad(): void {
    this.imageLoaded.set(true);
  }
}
