import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommunicationPanelComponent } from './communication-panel.component';

describe('CommunicationPanelComponent', () => {
  let component: CommunicationPanelComponent;
  let fixture: ComponentFixture<CommunicationPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CommunicationPanelComponent]
    });
    fixture = TestBed.createComponent(CommunicationPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
