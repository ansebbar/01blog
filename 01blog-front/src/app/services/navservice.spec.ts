import { TestBed } from '@angular/core/testing';

import { Navservice } from './navservice';

describe('Navservice', () => {
  let service: Navservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Navservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
