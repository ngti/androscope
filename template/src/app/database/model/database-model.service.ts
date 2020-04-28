import { Injectable } from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {DatabaseUri} from './database-uri';

@Injectable({
  providedIn: 'root'
})
export class DatabaseModelService extends QueryModelService<DatabaseUri> {

  private databaseNameInternal: string;
  private databaseQueryKey: string = null;
  private databaseQueryValue: string = null;

  constructor() {
    super(null);
  }

  // get databaseName(): string {
  //   return this.databaseNameInternal;
  // }

  set databaseName(newName: string) {
    if (this.databaseName !== newName) {
      this.databaseNameInternal = newName;
      this.databaseQueryKey = null;
      this.databaseQueryValue = null;
      this.updateUri();
    }
  }

  setDatabaseQuery(key: string, value: string) {
    if (this.databaseQueryKey !== key || this.databaseQueryValue !== value) {
      this.databaseQueryKey = key;
      this.databaseQueryValue = value;
      this.updateUri();
    }
  }

  private updateUri() {
    this.uri = new DatabaseUri(this.databaseNameInternal, this.databaseQueryKey, this.databaseQueryValue);
  }
}
