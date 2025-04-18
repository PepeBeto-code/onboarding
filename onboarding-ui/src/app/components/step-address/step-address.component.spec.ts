import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepAddressComponent } from './step-address.component';

describe('StepAddressComponent', () => {
  let component: StepAddressComponent;
  let fixture: ComponentFixture<StepAddressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepAddressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
