import {BaseDataSource} from '../common/base/base-data-source';
import {FileSystemEntry} from '../common/rest/file-system-data';
import {FileSystemType, RestService} from '../common/rest/rest.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {SortDirection} from '@angular/material';

export class FileExplorerDataSource extends BaseDataSource<FileSystemEntry> {

  static DEFAULT_PAGE_SIZE = 250;

  private rowCountSubject = new BehaviorSubject<number>(0);
  rowCount$ = this.rowCountSubject.asObservable();

  constructor(
    private restService: RestService,
    private fileSystemType: FileSystemType,
    private path: string,
    loadingSubject: BehaviorSubject<boolean>
  ) {
    super(FileExplorerDataSource.DEFAULT_PAGE_SIZE, loadingSubject);

    restService.getFileCount(fileSystemType, path).subscribe(fileSystemCount => {
      this.rowCountSubject.next(fileSystemCount.totalEntries);
    });
  }

  disconnect() {
    super.disconnect();
    this.rowCountSubject.complete();
  }

  protected onGenerateNetworkRequest(
    pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string
  ): Observable<FileSystemEntry[]> {
    return this.restService.getFileList(this.fileSystemType, null, pageSize, pageNumber, sortOrder, sortColumn);
  }
}
