import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatabaseQueryEmptyComponent } from './database-query-empty.component';

describe('DatabaseQueryEmptyComponent', () => {
  let component: DatabaseQueryEmptyComponent;
  let fixture: ComponentFixture<DatabaseQueryEmptyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabaseQueryEmptyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabaseQueryEmptyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
