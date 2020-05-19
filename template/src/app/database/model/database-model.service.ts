import {Injectable} from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {DatabaseUri} from './database-uri';
import {BehaviorSubject} from 'rxjs';
import {RestService} from '../../common/rest/rest.service';

@Injectable({
  providedIn: 'root'
})
export class DatabaseModelService extends QueryModelService<DatabaseUri> {

  private databaseTitleSubject = new BehaviorSubject<string>('...');
  readonly databaseTitle$ = this.databaseTitleSubject.asObservable();

  private databaseNameInternal: string;
  private databaseQueryKey: string = null;
  private databaseQueryValue: string = null;

  constructor(
    private restService: RestService
  ) {
    super(null);
  }

  set databaseName(newName: string) {
    if (this.databaseName !== newName) {
      this.databaseNameInternal = newName;
      this.databaseQueryKey = null;
      this.databaseQueryValue = null;
      this.updateUri();

      this.restService.getDatabaseTitle(this.uri).subscribe(title => {
        this.databaseTitleSubject.next(title);
      });
    }
  }

  get queryValue(): string {
    return this.databaseQueryValue;
  }

  clearDatabaseTable() {
    this.setDatabaseQueryInternal(null, null);
  }

  clearDatabaseQuery(expectedQuery: string) {
    // Avoid overwriting query that was set by another component
    if (this.databaseQueryKey === 'query' && this.databaseQueryValue === expectedQuery) {
      this.setDatabaseQueryInternal(null, null);
    }
  }

  setDatabaseTable(table: string) {
    this.setDatabaseQueryInternal('table', table)
  }

  setDatabaseQuery(query: string) {
    this.setDatabaseQueryInternal('query', query)
  }

  forceUpdateUri() {
    this.updateUri();
  }

  private setDatabaseQueryInternal(key: string, value: string) {
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
