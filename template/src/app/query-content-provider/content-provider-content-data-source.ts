import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {map, retry} from 'rxjs/operators';
import {Observable, of as observableOf, merge, Subscriber, BehaviorSubject} from 'rxjs';
import {Injectable} from '@angular/core';
import {RestService} from './rest.service';
import {Uri} from './uri';
import {RowCount} from './row-count';

/**
 * Data source for the ContentProviderContent view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
@Injectable()
export class ContentProviderContentDataSource extends DataSource<[]> {

  private columnNames$ = new BehaviorSubject<[]>(null);
  columnNames = this.columnNames$.asObservable();
  private rowCount$ = new BehaviorSubject<number>(0);
  rowCount = this.rowCount$.asObservable();
  private data$ = new BehaviorSubject<[][]>(null);
  paginator: MatPaginator;
  sort: MatSort;

  constructor(private restService: RestService, private uri: Uri) {
    super();

    restService.getColumns(uri).subscribe(columnNames =>
      this.columnNames$.next(columnNames)
    );
    restService.getRowCount(uri).subscribe(rowCount =>
      this.rowCount$.next(rowCount.count)
    );

    this.loadData();
  }

  connect(): Observable<[][]> {
    console.log('Connected to data');
    return this.data$.asObservable();
  }

  disconnect() {
    this.columnNames$.complete();
    this.rowCount$.complete();
    this.data$.complete();
  }

  private loadData() {
    this.restService.getData(this.uri, 0, 0).subscribe(data =>
      this.data$.next(data)
    );
  }
}
