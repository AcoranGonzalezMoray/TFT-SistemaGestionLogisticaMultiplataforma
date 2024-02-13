import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehousePanelComponent } from './warehouse-panel.component';

describe('WarehousePanelComponent', () => {
  let component: WarehousePanelComponent;
  let fixture: ComponentFixture<WarehousePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WarehousePanelComponent]
    });
    fixture = TestBed.createComponent(WarehousePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
