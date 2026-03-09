import { Component, computed, HostListener, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Photo } from '../../../../core/models/photo.model';

@Component({
  selector: 'app-photo-modal',
  imports: [RouterLink],
  templateUrl: './photo-modal.component.html',
  styleUrl: './photo-modal.component.scss'
})
export class PhotoModalComponent {
  photo = input.required<Photo>();
  photos = input<Photo[]>([]);
  closed = output<void>();
  photoChanged = output<Photo>();

  private touchStartX = 0;
  private touchEndX = 0;
  private readonly SWIPE_THRESHOLD = 50;

  currentIndex = computed(() => {
    const list = this.photos();
    return list.findIndex(p => p.id === this.photo().id);
  });

  hasPrev = computed(() => this.currentIndex() > 0);
  hasNext = computed(() => {
    const list = this.photos();
    return this.currentIndex() < list.length - 1;
  });

  @HostListener('window:keydown', ['$event'])
  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') this.closed.emit();
    if (event.key === 'ArrowLeft') this.prev();
    if (event.key === 'ArrowRight') this.next();
  }

  onTouchStart(event: TouchEvent): void {
    this.touchStartX = event.changedTouches[0].screenX;
  }

  onTouchEnd(event: TouchEvent): void {
    this.touchEndX = event.changedTouches[0].screenX;
    const diff = this.touchStartX - this.touchEndX;
    if (Math.abs(diff) > this.SWIPE_THRESHOLD) {
      if (diff > 0) this.next();
      else this.prev();
    }
  }

  prev(): void {
    const list = this.photos();
    const idx = this.currentIndex();
    if (idx > 0) {
      this.photoChanged.emit(list[idx - 1]);
    }
  }

  next(): void {
    const list = this.photos();
    const idx = this.currentIndex();
    if (idx < list.length - 1) {
      this.photoChanged.emit(list[idx + 1]);
    }
  }

  saveSelection(): void {
    sessionStorage.setItem('gallery-selected-photo', String(this.photo().id));
  }
}
