import {BaseDataSource} from '../common/base/base-data-source';
import {Breadcrumb, FileSystemEntry} from '../common/rest/file-system-data';
import {FileSystemType, RestService} from '../common/rest/rest.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {DataParams} from '../common/base/data-params';

export class FileExplorerDataSource extends BaseDataSource<FileSystemEntry> {

  static DEFAULT_PAGE_SIZE = 250;

  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount$ = this.rowCountSubject.asObservable();

  constructor(
    private restService: RestService,
    public readonly fileSystemType: FileSystemType,
    public readonly path: string,
    loadingSubject: BehaviorSubject<boolean>,
    breadcrumbsSubject: BehaviorSubject<Breadcrumb[]>
  ) {
    super(FileExplorerDataSource.DEFAULT_PAGE_SIZE, loadingSubject);

    console.log('FileExplorerDataSource created');

    restService.getFileCount(fileSystemType, path).subscribe(fileSystemCount => {
      this.rowCountSubject.next(fileSystemCount.totalEntries);
    });

    restService.getBreadcrumbs(fileSystemType, path).subscribe(breadcrumbs => {
      breadcrumbsSubject.next(breadcrumbs);
    });
  }

  static concatPaths(parent: string, path: string): string {
    if (parent == null) {
      return path;
    }
    return parent + '/' + path;
  }

  disconnect() {
    super.disconnect();
    this.rowCountSubject.complete();
  }

  getSubPath(entry: FileSystemEntry): string {
    return FileExplorerDataSource.concatPaths(this.path, FileSystemEntry.getFullName(entry));
  }

  protected onGenerateNetworkRequest(dataParams: DataParams): Observable<FileSystemEntry[]> {
    return this.restService.getFileList(this.fileSystemType, this.path, dataParams);
  }
}
