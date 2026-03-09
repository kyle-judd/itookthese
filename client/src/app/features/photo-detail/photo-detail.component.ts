import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Location } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { switchMap } from 'rxjs';
import { PhotoService } from '../../core/services/photo.service';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-photo-detail',
  imports: [HeaderComponent, RouterLink, DatePipe],
  templateUrl: './photo-detail.component.html',
  styleUrl: './photo-detail.component.scss'
})
export class PhotoDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly location = inject(Location);
  private readonly photoService = inject(PhotoService);

  photo = toSignal(
    this.route.paramMap.pipe(
      switchMap(params => this.photoService.getPhoto(Number(params.get('id'))))
    )
  );

  goBack(): void {
    this.location.back();
  }
}
