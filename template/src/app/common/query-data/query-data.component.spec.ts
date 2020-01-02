import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryDataComponent } from './query-data.component';

describe('QueryDataComponent', () => {
  let component: QueryDataComponent;
  let fixture: ComponentFixture<QueryDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
