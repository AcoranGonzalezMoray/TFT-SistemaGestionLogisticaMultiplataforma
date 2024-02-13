import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransportRoutePanelComponent } from './transport-route-panel.component';

describe('TransportRoutePanelComponent', () => {
  let component: TransportRoutePanelComponent;
  let fixture: ComponentFixture<TransportRoutePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransportRoutePanelComponent]
    });
    fixture = TestBed.createComponent(TransportRoutePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
