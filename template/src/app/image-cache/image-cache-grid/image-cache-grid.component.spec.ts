import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageCacheGridComponent } from './image-cache-grid.component';

describe('ImageCacheGridComponent', () => {
  let component: ImageCacheGridComponent;
  let fixture: ComponentFixture<ImageCacheGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImageCacheGridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageCacheGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
