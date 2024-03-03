import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilePanelComponent } from './profile-panel.component';

describe('ProfilePanelComponent', () => {
  let component: ProfilePanelComponent;
  let fixture: ComponentFixture<ProfilePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfilePanelComponent]
    });
    fixture = TestBed.createComponent(ProfilePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
