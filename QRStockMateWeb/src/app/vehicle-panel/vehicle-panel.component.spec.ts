import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclePanelComponent } from './vehicle-panel.component';

describe('VehiclePanelComponent', () => {
  let component: VehiclePanelComponent;
  let fixture: ComponentFixture<VehiclePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VehiclePanelComponent]
    });
    fixture = TestBed.createComponent(VehiclePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
