import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepPersonalComponent } from './step-personal.component';

describe('StepPersonalComponent', () => {
  let component: StepPersonalComponent;
  let fixture: ComponentFixture<StepPersonalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepPersonalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepPersonalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
