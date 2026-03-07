import { Component } from '@angular/core';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { InViewDirective } from './in-view.directive';

@Component({
  template: '<div appInView class="scroll-reveal">Content</div>',
  imports: [InViewDirective]
})
class TestHostComponent {}

@Component({
  template: '<div appInView [delay]="200" class="scroll-reveal">Delayed</div>',
  imports: [InViewDirective]
})
class DelayedHostComponent {}

describe('InViewDirective', () => {
  let mockObserve: jasmine.Spy;
  let mockDisconnect: jasmine.Spy;
  let capturedCallback: IntersectionObserverCallback;
  let originalIntersectionObserver: typeof IntersectionObserver;

  beforeEach(() => {
    mockObserve = jasmine.createSpy('observe');
    mockDisconnect = jasmine.createSpy('disconnect');
    originalIntersectionObserver = window.IntersectionObserver;

    (window as any).IntersectionObserver = function(callback: IntersectionObserverCallback) {
      capturedCallback = callback;
      return {
        observe: mockObserve,
        disconnect: mockDisconnect,
        unobserve: jasmine.createSpy('unobserve')
      };
    };
  });

  afterEach(() => {
    window.IntersectionObserver = originalIntersectionObserver;
  });

  it('should create an IntersectionObserver on init', () => {
    TestBed.configureTestingModule({ imports: [TestHostComponent] });
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    expect(mockObserve).toHaveBeenCalled();
  });

  it('should add in-view class when element intersects', fakeAsync(() => {
    TestBed.configureTestingModule({ imports: [TestHostComponent] });
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    const el = fixture.nativeElement.querySelector('[appInView]') as HTMLElement;
    expect(el.classList.contains('in-view')).toBeFalse();

    capturedCallback(
      [{ isIntersecting: true } as IntersectionObserverEntry],
      {} as IntersectionObserver
    );
    tick(0);

    expect(el.classList.contains('in-view')).toBeTrue();
  }));

  it('should not add in-view class when element is not intersecting', fakeAsync(() => {
    TestBed.configureTestingModule({ imports: [TestHostComponent] });
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    const el = fixture.nativeElement.querySelector('[appInView]') as HTMLElement;

    capturedCallback(
      [{ isIntersecting: false } as IntersectionObserverEntry],
      {} as IntersectionObserver
    );
    tick(0);

    expect(el.classList.contains('in-view')).toBeFalse();
  }));

  it('should disconnect observer after intersection', fakeAsync(() => {
    TestBed.configureTestingModule({ imports: [TestHostComponent] });
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    capturedCallback(
      [{ isIntersecting: true } as IntersectionObserverEntry],
      {} as IntersectionObserver
    );
    tick(0);

    expect(mockDisconnect).toHaveBeenCalled();
  }));

  it('should respect the delay input', fakeAsync(() => {
    TestBed.configureTestingModule({ imports: [DelayedHostComponent] });
    const fixture = TestBed.createComponent(DelayedHostComponent);
    fixture.detectChanges();

    const el = fixture.nativeElement.querySelector('[appInView]') as HTMLElement;

    capturedCallback(
      [{ isIntersecting: true } as IntersectionObserverEntry],
      {} as IntersectionObserver
    );

    tick(100);
    expect(el.classList.contains('in-view')).toBeFalse();

    tick(100);
    expect(el.classList.contains('in-view')).toBeTrue();
  }));

  it('should disconnect on destroy', () => {
    TestBed.configureTestingModule({ imports: [TestHostComponent] });
    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    mockDisconnect.calls.reset();
    fixture.destroy();

    expect(mockDisconnect).toHaveBeenCalled();
  });
});
