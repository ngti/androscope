import {DataSource} from '@angular/cdk/collections';
import {MatSort, SortDirection} from '@angular/material/sort';
import {MatPaginator} from '@angular/material';
import {BehaviorSubject, Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

export abstract class BaseDataSource<T> extends DataSource<T> {

  private pageSize: number;
  private pageNumber = 0;
  private sortOrder: SortDirection = '';
  private sortColumn?: string = null;

  private dataSubject = new BehaviorSubject<T[]>(null);
  private changed = true;

  constructor(
    defaultPageSize: number,
    private loadingSubject: BehaviorSubject<boolean>
  ) {
    super();
    this.pageSize = defaultPageSize;
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
    if (this.pageSize !== paginator.pageSize || this.pageNumber !== paginator.pageIndex) {
      this.pageSize = paginator.pageSize;
      this.pageNumber = paginator.pageIndex;
      this.changed = true;
    }
  }

  updateSorting(sort?: MatSort) {
    if (sort == null) {
      return;
    }
    if (this.sortOrder !== sort.direction || this.sortColumn !== sort.active) {
      this.sortOrder = sort.direction;
      this.sortColumn = sort.active;
      this.changed = true;
    }
  }

  reloadDataIfNeeded() {
    if (this.changed) {
      this.reloadData();
      this.changed = false;
    }
  }

  protected abstract onGenerateNetworkRequest(
    pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string
  ): Observable<T[]>;

  private reloadData() {
    this.loadingSubject.next(true);

    this.onGenerateNetworkRequest(this.pageSize, this.pageNumber, this.sortOrder, this.sortColumn)
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(data =>
        this.dataSubject.next(data)
      );
  }
}
