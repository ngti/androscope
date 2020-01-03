import {DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs';
import {MatSort, SortDirection} from '@angular/material/sort';
import {RestService} from '../rest/rest.service';
import {Uri} from '../query-model/uri';
import {finalize} from 'rxjs/operators';
import {MatPaginator} from '@angular/material';

export class QueryDataSource extends DataSource<[]> {

  static DEFAULT_PAGE_SIZE = 50;

  private columnNamesSubject = new BehaviorSubject<string[]>(null);
  columnNames$ = this.columnNamesSubject.asObservable();
  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount$ = this.rowCountSubject.asObservable();
  private dataSubject = new BehaviorSubject<[][]>(null);
  private metadataLoaded: boolean;

  private pageSize: number = QueryDataSource.DEFAULT_PAGE_SIZE;
  private pageNumber = 0;
  private sortOrder: SortDirection = '';
  private sortColumn?: string = null;

  private changed = true;

  constructor(
    private restService: RestService,
    private uri: Uri,
    private loadingSubject,
    private errorSubject
  ) {
    super();

    restService.getUriMetadata(uri).subscribe(metadata => {
      this.columnNamesSubject.next(metadata.columns);
      this.rowCountSubject.next(metadata.rowCount);
      this.errorSubject.next(metadata.errorMessage);
      this.metadataLoaded = true;
    });
  }

  connect(): Observable<[][]> {
    return this.dataSubject.asObservable();
  }

  disconnect() {
    this.columnNamesSubject.complete();
    this.rowCountSubject.complete();
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

  showTable(): boolean {
    return this.metadataLoaded && this.errorSubject.value == null;
  }

  private reloadData() {
    this.loadingSubject.next(true);

    this.restService.getUriData(this.uri, this.pageSize, this.pageNumber, this.sortOrder, this.sortColumn)
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(data =>
        this.dataSubject.next(data)
      );
  }
}
