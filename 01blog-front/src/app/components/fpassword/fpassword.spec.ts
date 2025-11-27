import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fpassword } from './fpassword';

describe('Fpassword', () => {
  let component: Fpassword;
  let fixture: ComponentFixture<Fpassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Fpassword]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Fpassword);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
