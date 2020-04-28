import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatabaseQueryDataComponent } from './database-query-data.component';

describe('DatabaseQueryDataComponent', () => {
  let component: DatabaseQueryDataComponent;
  let fixture: ComponentFixture<DatabaseQueryDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabaseQueryDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabaseQueryDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
