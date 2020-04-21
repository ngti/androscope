import {BaseDataSource} from '../common/base/base-data-source';
import {Breadcrumb, FileSystemEntry} from '../common/rest/file-system-data';
import {FileSystemType, RestService} from '../common/rest/rest.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {DataParams} from '../common/base/data-params';
import {FileSystemParams} from '../common/base/file-system-params';

export class FileExplorerDataSource extends BaseDataSource<FileSystemEntry> {

  static DEFAULT_PAGE_SIZE = 100;

  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount$ = this.rowCountSubject.asObservable();

  constructor(
    private restService: RestService,
    public readonly params: FileSystemParams,
    loadingSubject: BehaviorSubject<boolean>,
    breadcrumbsSubject: BehaviorSubject<Breadcrumb[]>
  ) {
    super(FileExplorerDataSource.DEFAULT_PAGE_SIZE, loadingSubject);

    console.log('FileExplorerDataSource created');

    restService.getFileCount(params).subscribe(fileSystemCount => {
      this.rowCountSubject.next(fileSystemCount.totalEntries);
    });

    restService.getBreadcrumbs(params).subscribe(breadcrumbs => {
      breadcrumbsSubject.next(breadcrumbs);
    });
  }

  forceReloadData() {
    this.params.updateTimestamp();
    super.forceReloadData();
  }

  disconnect() {
    super.disconnect();
    this.rowCountSubject.complete();
  }

  protected onGenerateNetworkRequest(dataParams: DataParams): Observable<FileSystemEntry[]> {
    return this.restService.getFileList(this.params, dataParams);
  }
}
