import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryContentProviderComponent } from './query-content-provider.component';

describe('QueryContentProviderComponent', () => {
  let component: QueryContentProviderComponent;
  let fixture: ComponentFixture<QueryContentProviderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryContentProviderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryContentProviderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
