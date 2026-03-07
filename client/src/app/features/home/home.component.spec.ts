import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { Component, output } from '@angular/core';
import { PhotoService } from '../../core/services/photo.service';
import { ThemeService } from '../../core/services/theme.service';
import { of } from 'rxjs';
import { signal } from '@angular/core';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { HeroComponent } from './components/hero/hero.component';
import { GalleryComponent } from './components/gallery/gallery.component';
import { AboutComponent } from './components/about/about.component';
import { ContactComponent } from '../contact/contact.component';

// Stub all child components
@Component({ selector: 'app-header', template: '' })
class HeaderStubComponent {}

@Component({ selector: 'app-footer', template: '<button (click)="openContact.emit()"></button>' })
class FooterStubComponent {
  openContact = output<void>();
}

@Component({ selector: 'app-hero', template: '' })
class HeroStubComponent {}

@Component({ selector: 'app-gallery', template: '' })
class GalleryStubComponent {}

@Component({ selector: 'app-about', template: '' })
class AboutStubComponent {}

@Component({ selector: 'app-contact', template: '<div class="contact-stub"></div>' })
class ContactStubComponent {
  closed = output<void>();
}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        { provide: PhotoService, useValue: { getPhotos: () => of([]) } },
        { provide: ThemeService, useValue: { theme: signal('light'), toggleTheme: jasmine.createSpy() } }
      ]
    })
    .overrideComponent(HomeComponent, {
      remove: { imports: [HeaderComponent, FooterComponent, HeroComponent, GalleryComponent, AboutComponent, ContactComponent] },
      add: { imports: [HeaderStubComponent, FooterStubComponent, HeroStubComponent, GalleryStubComponent, AboutStubComponent, ContactStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show contact overlay by default', () => {
    expect(component.showContact()).toBeFalse();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.contact-stub')).toBeNull();
  });

  it('should show contact overlay when showContact is true', () => {
    component.showContact.set(true);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.contact-stub')).toBeTruthy();
  });
});
