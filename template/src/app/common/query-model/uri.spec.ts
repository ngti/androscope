import { Uri } from './uri';

describe('Uri', () => {
  it('should create an instance', () => {
    expect(new Uri('content://test')).toBeTruthy();
  });
});
