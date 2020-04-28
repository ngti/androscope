import { Injectable } from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {DatabaseUri} from './database-uri';

@Injectable({
  providedIn: 'root'
})
export class DatabaseModelService extends QueryModelService<DatabaseUri> {

  private databaseNameInternal: string;
  private databaseQueryInternal: string = null;

  constructor() {
    super(null);
  }

  // get databaseName(): string {
  //   return this.databaseNameInternal;
  // }

  set databaseName(newName: string) {
    if (this.databaseName !== newName) {
      this.databaseNameInternal = newName;
      this.updateUri();
    }
  }

  set databaseQuery(newQuery: string) {
    if (this.databaseQueryInternal !== newQuery) {
      this.databaseQueryInternal = newQuery;
      this.updateUri();
    }
  }

  private updateUri() {
    this.uri = new DatabaseUri(this.databaseNameInternal, this.databaseQueryInternal);
  }
}
