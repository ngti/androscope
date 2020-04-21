import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageCacheListComponent } from './image-cache-list.component';

describe('ImageCacheListComponent', () => {
  let component: ImageCacheListComponent;
  let fixture: ComponentFixture<ImageCacheListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImageCacheListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageCacheListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
