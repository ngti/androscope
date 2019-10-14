import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentUriInputComponent } from './content-uri-input.component';

describe('ContentUriInputComponent', () => {
  let component: ContentUriInputComponent;
  let fixture: ComponentFixture<ContentUriInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ContentUriInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentUriInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
