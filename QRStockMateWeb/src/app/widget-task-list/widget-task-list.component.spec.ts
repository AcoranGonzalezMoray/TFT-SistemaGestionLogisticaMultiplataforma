import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetTaskListComponent } from './widget-task-list.component';

describe('WidgetTaskListComponent', () => {
  let component: WidgetTaskListComponent;
  let fixture: ComponentFixture<WidgetTaskListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WidgetTaskListComponent]
    });
    fixture = TestBed.createComponent(WidgetTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
