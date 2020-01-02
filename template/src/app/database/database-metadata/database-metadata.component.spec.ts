import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatabaseMetadataComponent } from './database-metadata.component';

describe('DatabaseMetadataComponent', () => {
  let component: DatabaseMetadataComponent;
  let fixture: ComponentFixture<DatabaseMetadataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabaseMetadataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabaseMetadataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
