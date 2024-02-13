import { TestBed } from '@angular/core/testing';

import { GridItemServiceService } from './grid-item-service.service';

describe('GridItemServiceService', () => {
  let service: GridItemServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GridItemServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
