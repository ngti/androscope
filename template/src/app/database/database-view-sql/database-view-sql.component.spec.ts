import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatabaseViewSqlComponent } from './database-view-sql.component';

describe('DatabaseViewSqlComponent', () => {
  let component: DatabaseViewSqlComponent;
  let fixture: ComponentFixture<DatabaseViewSqlComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabaseViewSqlComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabaseViewSqlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
