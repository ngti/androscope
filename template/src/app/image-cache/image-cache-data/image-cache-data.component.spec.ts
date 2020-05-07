import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageCacheDataComponent } from './image-cache-data.component';

describe('ImageCacheDataComponent', () => {
  let component: ImageCacheDataComponent;
  let fixture: ComponentFixture<ImageCacheDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImageCacheDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageCacheDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
