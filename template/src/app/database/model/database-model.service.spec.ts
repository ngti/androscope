import { TestBed } from '@angular/core/testing';

import { DatabaseModelService } from './database-model.service';

describe('DatabaseModelService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DatabaseModelService = TestBed.get(DatabaseModelService);
    expect(service).toBeTruthy();
  });
});
