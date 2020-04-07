import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdddatasourceComponent } from './adddatasource.component';

describe('AdddatasourceComponent', () => {
  let component: AdddatasourceComponent;
  let fixture: ComponentFixture<AdddatasourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdddatasourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdddatasourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
