import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the portfolio name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Kyle Judd Photography');
  });

  it('should render the copyright notice with current year', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const year = new Date().getFullYear();
    expect(compiled.textContent).toContain(`${year} Kyle Judd. All rights reserved.`);
  });

  it('should have a contact button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[aria-label="Contact"]');
    expect(button).toBeTruthy();
  });

  it('should emit openContact when the contact button is clicked', () => {
    spyOn(component.openContact, 'emit');
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[aria-label="Contact"]') as HTMLButtonElement;
    button.click();
    expect(component.openContact.emit).toHaveBeenCalled();
  });

  it('should have an Instagram link with target _blank', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const link = compiled.querySelector('a[aria-label="Instagram"]') as HTMLAnchorElement;
    expect(link).toBeTruthy();
    expect(link.target).toBe('_blank');
    expect(link.rel).toContain('noopener');
  });
});
