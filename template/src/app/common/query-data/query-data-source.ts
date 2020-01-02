import {DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, SortDirection} from '@angular/material/sort';
import {RestService} from '../../query-content-provider/rest.service';
import {Uri} from '../query-model/uri';
import {Injectable} from '@angular/core';

export class QueryDataSource extends DataSource<[]> {

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

  loadData(pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string) {
    console.log('Load data');
    this.restService.getData(this.uri, pageSize, pageNumber, sortOrder, sortColumn).subscribe(data =>
      this.data$.next(data)
    );
  }
}
