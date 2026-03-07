import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['login']);
    mockAuthService.login.and.returnValue(of({ token: 'fake-jwt-token' }));

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockRouter.navigate.and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the login heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Admin Login');
  });

  it('should initialize with an invalid form', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should require username', () => {
    const usernameCtrl = component.form.controls.username;
    expect(usernameCtrl.hasError('required')).toBeTrue();

    usernameCtrl.setValue('admin');
    expect(usernameCtrl.valid).toBeTrue();
  });

  it('should require password', () => {
    const passwordCtrl = component.form.controls.password;
    expect(passwordCtrl.hasError('required')).toBeTrue();

    passwordCtrl.setValue('secret');
    expect(passwordCtrl.valid).toBeTrue();
  });

  it('should not submit when form is invalid', () => {
    component.submit();
    expect(mockAuthService.login).not.toHaveBeenCalled();
  });

  it('should not submit when already loading', () => {
    fillValidForm();
    component.loading.set(true);
    component.submit();
    expect(mockAuthService.login).not.toHaveBeenCalled();
  });

  it('should call authService.login with form values on valid submit', () => {
    fillValidForm();
    component.submit();
    expect(mockAuthService.login).toHaveBeenCalledWith('admin', 'password123');
  });

  it('should navigate to /admin on successful login', () => {
    fillValidForm();
    component.submit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin']);
  });

  it('should set error message on login failure', () => {
    mockAuthService.login.and.returnValue(throwError(() => new Error('Unauthorized')));
    fillValidForm();
    component.submit();
    expect(component.error()).toBe('Invalid username or password.');
    expect(component.loading()).toBeFalse();
  });

  it('should clear error before each login attempt', () => {
    component.error.set('Previous error');
    fillValidForm();
    component.submit();
    // Error cleared and login succeeded, so it should be null
    expect(component.error()).toBeNull();
  });

  it('should render error text in the DOM', () => {
    mockAuthService.login.and.returnValue(throwError(() => new Error('fail')));
    fillValidForm();
    component.submit();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Invalid username or password');
  });

  it('should have submit button disabled when form is invalid', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const btn = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(btn.disabled).toBeTrue();
  });

  it('should have submit button enabled when form is valid', () => {
    fillValidForm();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const btn = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(btn.disabled).toBeFalse();
  });

  it('should have password input of type password', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const passwordInput = compiled.querySelector('#password') as HTMLInputElement;
    expect(passwordInput.type).toBe('password');
  });

  function fillValidForm(): void {
    component.form.setValue({
      username: 'admin',
      password: 'password123'
    });
  }
});
