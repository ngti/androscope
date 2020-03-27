import {DataSource} from '@angular/cdk/collections';
import {MatSort, SortDirection} from '@angular/material/sort';
import {MatPaginator} from '@angular/material';
import {BehaviorSubject, Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {DataParams} from './data-params';

export abstract class BaseDataSource<T> extends DataSource<T> {

  private dataParams = new DataParams();

  private dataSubject = new BehaviorSubject<T[]>(null);

  protected constructor(
    defaultPageSize: number,
    private loadingSubject: BehaviorSubject<boolean>
  ) {
    super();
    this.dataParams.pageSize = defaultPageSize;
  }

  connect(): Observable<T[]> {
    return this.dataSubject.asObservable();
  }

  disconnect() {
    this.dataSubject.complete();
  }

  updatePagination(paginator?: MatPaginator) {
    if (paginator == null) {
      return;
    }
    this.dataParams.updatePagination(paginator.pageSize, paginator.pageIndex);
  }

  updateSorting(sort?: MatSort) {
    if (sort == null) {
      return;
    }
    this.setSorting(sort.direction, sort.active);
  }

  setSorting(sortOrder: SortDirection = '', sortColumn?: string) {
    this.dataParams.updateSorting(sortOrder, sortColumn);
  }

  reloadDataIfNeeded() {
    this.dataParams.consume(dataParams =>
      this.reloadData(dataParams)
    );
  }

  forceReloadData() {
    this.reloadData(this.dataParams);
  }

  protected abstract onGenerateNetworkRequest(dataParams: DataParams): Observable<T[]>;

  private reloadData(dataParams: DataParams) {
    this.loadingSubject.next(true);

    this.onGenerateNetworkRequest(dataParams)
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(data =>
        this.dataSubject.next(data)
      );
  }
}
