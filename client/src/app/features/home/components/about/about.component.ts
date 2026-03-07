import { Component } from '@angular/core';
import { InViewDirective } from '../../../../shared/directives/in-view.directive';

interface Feature {
  title: string;
  description: string;
  icon: 'smartphone' | 'eye' | 'heart';
}

@Component({
  selector: 'app-about',
  imports: [InViewDirective],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss'
})
export class AboutComponent {
  readonly features: Feature[] = [
    {
      icon: 'smartphone',
      title: 'iPhone Only',
      description: 'Every photo in this portfolio was captured using an iPhone camera, proving that great photography is about vision, not just gear.'
    },
    {
      icon: 'eye',
      title: 'Unique Perspective',
      description: 'Finding beauty in everyday moments and ordinary places through careful composition and creative angles.'
    },
    {
      icon: 'heart',
      title: 'Passion Project',
      description: 'A personal journey of capturing and sharing moments that inspire, evoke emotion, and tell stories.'
    }
  ];
}
