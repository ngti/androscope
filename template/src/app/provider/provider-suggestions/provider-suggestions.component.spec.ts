import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProviderSuggestionsComponent } from './provider-suggestions.component';

describe('ProviderSuggestionsComponent', () => {
  let component: ProviderSuggestionsComponent;
  let fixture: ComponentFixture<ProviderSuggestionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProviderSuggestionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProviderSuggestionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
