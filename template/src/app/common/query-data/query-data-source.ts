import {DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs';
import {SortDirection} from '@angular/material/sort';
import {RestService} from '../rest/rest.service';
import {Uri} from '../query-model/uri';
import {finalize} from 'rxjs/operators';

export class QueryDataSource extends DataSource<[]> {

  private columnNamesSubject = new BehaviorSubject<string[]>(null);
  columnNames = this.columnNamesSubject.asObservable();
  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount = this.rowCountSubject.asObservable();
  private dataSubject = new BehaviorSubject<[][]>(null);

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading = this.loadingSubject.asObservable();

  constructor(private restService: RestService, private uri: Uri) {
    super();

    restService.getUriMetadata(uri).subscribe(metadata => {
        this.columnNamesSubject.next(metadata.columns);
        this.rowCountSubject.next(metadata.rowCount);
    });

    this.loading.subscribe(loading => console.log(`Loading: ${loading}`));
  }

  connect(): Observable<[][]> {
    console.log('Connected to data');
    return this.dataSubject.asObservable();
  }

  disconnect() {
    this.columnNamesSubject.complete();
    this.rowCountSubject.complete();
    this.dataSubject.complete();
    this.loadingSubject.complete();
  }

  loadData(pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string) {
    console.log('Load data');

    this.loadingSubject.next(true);

    this.restService.getUriData(this.uri, pageSize, pageNumber, sortOrder, sortColumn)
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(data =>
        this.dataSubject.next(data)
      );
  }
}
