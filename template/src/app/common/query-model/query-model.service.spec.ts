import { TestBed } from '@angular/core/testing';

import { QueryModelService } from './query-model.service';

describe('QueryModelService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: QueryModelService = TestBed.get(QueryModelService);
    expect(service).toBeTruthy();
  });
});
