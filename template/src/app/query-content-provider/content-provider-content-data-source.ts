import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import {map, retry} from 'rxjs/operators';
import {Observable, of as observableOf, merge, Subscriber} from 'rxjs';
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
  private columnSubscribers = [];
  private cachedColumnNames: [];
  columnNames: Observable<[]>;
  rowCount: Observable<number>;
//  data = [];
  paginator: MatPaginator;
  sort: MatSort;

  constructor(private restService: RestService, private uri: Uri) {
    super();

    const columnsNetworkRequest = restService.getColumns(uri);
    columnsNetworkRequest.subscribe(columnNames => {
      console.log('Columns retrieved');
      this.cachedColumnNames = columnNames;
      this.columnSubscribers.forEach(subscriber => subscriber.next(columnNames));
    });

    this.columnNames = new Observable<[]>(subscriber => {
      if (this.cachedColumnNames) {
        console.log('Subscribed to columns - dispatching result immediately');
        subscriber.next(this.cachedColumnNames);
      } else {
        console.log('Subscribed to columns - waiting for result');
        this.columnSubscribers.push(subscriber);
      }
  });

    this.rowCount = new Observable<number>(subscriber => {
      console.log('Subscribed to row count');
      restService.getRowCount(uri).subscribe(rowCount =>
      subscriber.next(rowCount.count));
      });

    // const dataMutations = [
    //   this.paginator.page,
    //   this.paginator.pageSize
    // ];

    // merge(...dataMutations).subscribe(() => )

    // const columnCount = 20;
    //
    // for (let i = 0; i <= columnCount; i++) {
    //   this.columnNames[i] = 'Column ' + i;
    // }

    // for (let rowIndex = 0; rowIndex < 1000; rowIndex++) {
    //   const row = [];
    //
    //   for (let columnIndex = 0; columnIndex <= columnCount; columnIndex++) {
    //     row.push('Value Column ' + columnIndex + ' Row ' + rowIndex);
    //   }
    //
    //   this.data.push(row);
    // }
  }

  // getColumnNames(): Observable<[]> {
  //   console.log('Columns requested!');
  //   return this.restService.getColumns(this.uri).pipe(retry(0));
  // }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<[][]> {
    console.log('Connected to data');
    return this.restService.getData(this.uri);

    // Combine everything that affects the rendered data into one update
    // stream for the data-table to consume.
    // const dataMutations = [
    //   data,
    //   this.paginator.page,
    //   this.sort.sortChange
    // ];

    // return merge(...dataMutations)
    //   .pipe(map(() => {
    //   return data;
    //   // return this.getPagedData(Object.assign([], this.data));
    //   // return this.getPagedData(this.getSortedData([...this.data]));
    // }));
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up during connect.
   */
  disconnect() {}

  // /**
  //  * Paginate the data (client-side). If you're using server-side pagination,
  //  * this would be replaced by requesting the appropriate data from the server.
  //  */
  // private getPagedData(data: any[]) {
  //   const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
  //   return data.splice(startIndex, this.paginator.pageSize);
  // }

  // /**
  //  * Sort the data (client-side). If you're using server-side sorting,
  //  * this would be replaced by requesting the appropriate data from the server.
  //  */
  // private getSortedData(data: ContentProviderContentItem[]) {
  //   if (!this.sort.active || this.sort.direction === '') {
  //     return data;
  //   }
  //
  //   return data.sort((a, b) => {
  //     const isAsc = this.sort.direction === 'asc';
  //     switch (this.sort.active) {
  //       case 'name': return compare(a.name, b.name, isAsc);
  //       case 'id': return compare(+a.id, +b.id, isAsc);
  //       default: return 0;
  //     }
  //   });
  // }
}

/** Simple sort comparator for example ID/Name columns (for client-side sorting). */
function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
