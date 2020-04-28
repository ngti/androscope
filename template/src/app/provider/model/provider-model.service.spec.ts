import { TestBed } from '@angular/core/testing';

import { ProviderModelService } from './provider-model.service';

describe('ProviderModelService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProviderModelService = TestBed.get(ProviderModelService);
    expect(service).toBeTruthy();
  });
});
