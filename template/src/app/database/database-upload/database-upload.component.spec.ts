import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatabaseUploadComponent } from './database-upload.component';

describe('DatabaseUploadComponent', () => {
  let component: DatabaseUploadComponent;
  let fixture: ComponentFixture<DatabaseUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabaseUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabaseUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
