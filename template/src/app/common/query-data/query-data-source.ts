import {BehaviorSubject, Observable} from 'rxjs';
import {RestService} from '../rest/rest.service';
import {Uri} from '../query-model/uri';
import {BaseDataSource} from '../base/base-data-source';
import {DataParams} from '../base/data-params';

export class QueryDataSource extends BaseDataSource<[]> {

  static DEFAULT_PAGE_SIZE = 50;

  private columnNamesSubject = new BehaviorSubject<string[]>(null);
  columnNames$ = this.columnNamesSubject.asObservable();
  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount$ = this.rowCountSubject.asObservable();
  private metadataLoaded: boolean;

  constructor(
    private restService: RestService,
    private uri: Uri,
    loadingSubject: BehaviorSubject<boolean>,
    private errorSubject: BehaviorSubject<string>
  ) {
    super(QueryDataSource.DEFAULT_PAGE_SIZE, loadingSubject);

    restService.getProviderInfo(uri).subscribe(metadata => {
      this.columnNamesSubject.next(metadata.columns);
      this.rowCountSubject.next(metadata.rowCount);
      this.errorSubject.next(metadata.errorMessage);
      this.metadataLoaded = true;
    });
  }

  get showTable(): boolean {
    return this.metadataLoaded && this.errorSubject.value == null;
  }

  disconnect() {
    super.disconnect();
    this.columnNamesSubject.complete();
    this.rowCountSubject.complete();
  }

  protected onGenerateNetworkRequest(dataParams: DataParams): Observable<[][]> {
    return this.restService.getUriData(this.uri, dataParams);
  }
}
