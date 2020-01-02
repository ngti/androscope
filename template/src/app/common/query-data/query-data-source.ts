import {DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, SortDirection} from '@angular/material/sort';
import {RestService} from '../rest/rest.service';
import {Uri} from '../query-model/uri';
import {Injectable} from '@angular/core';
import {finalize} from 'rxjs/operators';

export class QueryDataSource extends DataSource<[]> {

  private columnNames$ = new BehaviorSubject<[]>(null);
  columnNames = this.columnNames$.asObservable();
  private rowCount$ = new BehaviorSubject<number>(0);
  rowCount = this.rowCount$.asObservable();
  private data$ = new BehaviorSubject<[][]>(null);

  private loading$ = new BehaviorSubject<boolean>(false);
  loading = this.loading$.asObservable();

  constructor(private restService: RestService, private uri: Uri) {
    super();

    restService.getColumns(uri).subscribe(columnNames =>
      this.columnNames$.next(columnNames)
    );
    restService.getRowCount(uri).subscribe(rowCount =>
      this.rowCount$.next(rowCount.count)
    );
  }

  connect(): Observable<[][]> {
    console.log('Connected to data');
    return this.data$.asObservable();
  }

  disconnect() {
    this.columnNames$.complete();
    this.rowCount$.complete();
    this.data$.complete();
    this.loading$.complete();
  }

  loadData(pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string) {
    console.log('Load data');

    this.loading$.next(true);

    this.restService.getData(this.uri, pageSize, pageNumber, sortOrder, sortColumn)
      .pipe(
        finalize(() => this.loading$.next(false))
      )
      .subscribe(data =>
        this.data$.next(data)
      );
  }
}
