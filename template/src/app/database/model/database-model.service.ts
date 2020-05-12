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

  clearDatabaseQuery() {
    this.setDatabaseQuery(null, null);
  }

  setDatabaseQuery(key: string, value: string) {
    if (this.databaseQueryKey !== key || this.databaseQueryValue !== value) {
      this.databaseQueryKey = key;
      this.databaseQueryValue = value;
      this.updateUri();
    }
  }

  forceUpdateUri() {
    this.updateUri();
  }

  private updateUri() {
    this.uri = new DatabaseUri(this.databaseNameInternal, this.databaseQueryKey, this.databaseQueryValue);
  }
}
