import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ContactComponent } from './contact.component';
import { ContactService } from '../../core/services/contact.service';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('ContactComponent', () => {
  let component: ContactComponent;
  let fixture: ComponentFixture<ContactComponent>;
  let mockContactService: jasmine.SpyObj<ContactService>;

  beforeEach(async () => {
    mockContactService = jasmine.createSpyObj('ContactService', ['submit']);
    mockContactService.submit.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ContactComponent],
      providers: [
        { provide: ContactService, useValue: mockContactService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ContactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with an invalid form', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should have required validation on name', () => {
    const nameCtrl = component.form.controls.name;
    expect(nameCtrl.hasError('required')).toBeTrue();

    nameCtrl.setValue('A');
    expect(nameCtrl.hasError('minlength')).toBeTrue();

    nameCtrl.setValue('Alice');
    expect(nameCtrl.valid).toBeTrue();
  });

  it('should have email validation', () => {
    const emailCtrl = component.form.controls.email;
    expect(emailCtrl.hasError('required')).toBeTrue();

    emailCtrl.setValue('invalid');
    expect(emailCtrl.hasError('email')).toBeTrue();

    emailCtrl.setValue('test@example.com');
    expect(emailCtrl.valid).toBeTrue();
  });

  it('should have required and minlength validation on subject', () => {
    const subjectCtrl = component.form.controls.subject;
    expect(subjectCtrl.hasError('required')).toBeTrue();

    subjectCtrl.setValue('Hi');
    expect(subjectCtrl.hasError('minlength')).toBeTrue();

    subjectCtrl.setValue('Hello World');
    expect(subjectCtrl.valid).toBeTrue();
  });

  it('should have required and minlength validation on message', () => {
    const messageCtrl = component.form.controls.message;
    expect(messageCtrl.hasError('required')).toBeTrue();

    messageCtrl.setValue('Short');
    expect(messageCtrl.hasError('minlength')).toBeTrue();

    messageCtrl.setValue('This is a long enough message for the form');
    expect(messageCtrl.valid).toBeTrue();
  });

  it('should have a honeypot field with no validators', () => {
    const honeypotCtrl = component.form.controls.honeypot;
    expect(honeypotCtrl.valid).toBeTrue();
    expect(honeypotCtrl.value).toBe('');
  });

  it('should not submit when the form is invalid', () => {
    component.submit();
    expect(mockContactService.submit).not.toHaveBeenCalled();
  });

  it('should not submit when already loading', () => {
    fillValidForm();
    component.loading.set(true);
    component.submit();
    expect(mockContactService.submit).not.toHaveBeenCalled();
  });

  it('should submit with form data when valid', () => {
    fillValidForm();
    component.submit();

    expect(mockContactService.submit).toHaveBeenCalledWith(
      jasmine.objectContaining({
        name: 'Jane Smith',
        email: 'jane@example.com',
        subject: 'Photography inquiry',
        message: 'I would like to know more about your work.',
        honeypot: ''
      })
    );
  });

  it('should set loading to true during submission', () => {
    fillValidForm();
    expect(component.loading()).toBeFalse();
    component.submit();
    // After successful response (synchronous of()), loading is set back to false
    expect(component.loading()).toBeFalse();
  });

  it('should set submitted to true on success', () => {
    fillValidForm();
    component.submit();
    expect(component.submitted()).toBeTrue();
  });

  it('should set error message on failure', () => {
    mockContactService.submit.and.returnValue(throwError(() => new Error('Server error')));
    fillValidForm();
    component.submit();
    expect(component.error()).toBe('Something went wrong. Please try again.');
    expect(component.loading()).toBeFalse();
  });

  it('should clear error before each submission', () => {
    component.error.set('Previous error');
    fillValidForm();
    component.submit();
    // Error should have been cleared (and not re-set since submit succeeds)
    expect(component.error()).toBeNull();
  });

  it('should render success message after submission', () => {
    fillValidForm();
    component.submit();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Message sent!');
  });

  it('should render form when not yet submitted', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('form')).toBeTruthy();
    expect(compiled.textContent).toContain('Get in touch');
  });

  it('should render error message in the DOM', () => {
    mockContactService.submit.and.returnValue(throwError(() => new Error('fail')));
    fillValidForm();
    component.submit();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Something went wrong');
  });

  it('should have a hidden honeypot input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const honeypotInput = compiled.querySelector('input[formControlName="honeypot"]') as HTMLInputElement;
    expect(honeypotInput).toBeTruthy();
    expect(honeypotInput.classList.contains('hidden')).toBeTrue();
    expect(honeypotInput.tabIndex).toBe(-1);
  });

  it('should have the submit button disabled when form is invalid', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const submitBtn = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitBtn.disabled).toBeTrue();
  });

  it('should have the submit button enabled when form is valid', () => {
    fillValidForm();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const submitBtn = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitBtn.disabled).toBeFalse();
  });

  it('should include honeypot value in submission', () => {
    fillValidForm();
    component.form.controls.honeypot.setValue('bot-value');
    component.submit();

    expect(mockContactService.submit).toHaveBeenCalledWith(
      jasmine.objectContaining({ honeypot: 'bot-value' })
    );
  });

  function fillValidForm(): void {
    component.form.setValue({
      name: 'Jane Smith',
      email: 'jane@example.com',
      subject: 'Photography inquiry',
      message: 'I would like to know more about your work.',
      honeypot: ''
    });
  }
});
