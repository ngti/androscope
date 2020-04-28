import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, SortDirection} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {FileExplorerDataSource} from './file-explorer-datasource';
import {RestService} from '../common/rest/rest.service';
import {ActivatedRoute, NavigationExtras, Router} from '@angular/router';
import {Breadcrumb, FileSystemEntry} from '../common/rest/file-system-data';
import {BehaviorSubject, merge} from 'rxjs';
import {
  DeleteConfirmationDialogComponent,
  DeleteConfirmationDialogData
} from './delete-confirmation-dialog/delete-confirmation-dialog.component';
import {MatDialog, MatSnackBar} from '@angular/material';
import {FileSystemParams} from '../common/base/file-system-params';

@Component({
  selector: 'app-file-explorer',
  templateUrl: './file-explorer.component.html',
  styleUrls: ['./file-explorer.component.css']
})
export class FileExplorerComponent implements AfterViewInit, OnInit {

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<FileSystemEntry>;
  dataSource: FileExplorerDataSource;

  defaultPageSize = FileExplorerDataSource.DEFAULT_PAGE_SIZE;

  displayedColumns = ['type', 'name', 'extension', 'date', 'size', 'menu'];
  defaultSortOrder: SortDirection = 'asc';
  defaultSortColumn = 'name';

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private breadcrumbsSubject = new BehaviorSubject<Breadcrumb[]>([{ name: '...', path: ''}]);
  breadcrumbs$ = this.breadcrumbsSubject.asObservable();

  private viewInitialized = false;

  constructor(
    private restService: RestService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    console.log('FileExplorerComponent created');
  }

  ngOnInit() {
    merge(this.route.queryParams, this.route.params).subscribe(() => {
      const snapshot = this.route.snapshot;
      const newParams = new FileSystemParams(snapshot.params.type, snapshot.queryParams.path);
      console.log(`FileExplorerComponent new params: ${newParams}`);
      if (this.dataSource == null
        || !this.dataSource.params.equals(newParams)) {
        this.dataSource = new FileExplorerDataSource(
          this.restService, newParams, this.loadingSubject, this.breadcrumbsSubject);
        // Reset pagination
        if (this.paginator != null) {
          this.paginator.pageIndex = 0;
        }
        this.updateDataSource();
      }
    });
  }

  ngAfterViewInit() {
    this.viewInitialized = true;
    merge(this.paginator.page, this.sort.sortChange).subscribe(() => {
      this.updateDataSource();
    });
  }

  onMouseClick(entry: FileSystemEntry) {
    if (entry.isFolder) {
      this.openFolder(this.getNavigationExtras(entry));
    }
  }

  onMouseUp(entry: FileSystemEntry, event: MouseEvent) {
    this.openFolderInNewWindow(event, () => {
      return this.getNavigationExtras(entry);
    });
  }

  private openFolder(extras: NavigationExtras) {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([], extras);
  }

  private openFolderInNewWindow(event: MouseEvent, extrasSupplier: () => NavigationExtras) {
    if (event == null || event.button === 1) {
      const url = this.router.createUrlTree([], extrasSupplier()).toString();
      window.open(url);
    }
  }

  onDelete(entry: FileSystemEntry) {
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      data: new DeleteConfirmationDialogData(
        this.dataSource.params,
        entry
      )
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result != null) {
        this.snackBar.open(`«${result}» has been deleted`, null, {
          duration: 2000,
        });
        this.dataSource.forceReloadData();
      }
      console.log('The dialog was closed ' + result);
    });
  }

  onDownload(entry: FileSystemEntry) {
    const path = FileSystemEntry.getFullName(entry);
    const params = this.dataSource.params.withAppendedPath(path);
    window.location.href = this.restService.getFileDownloadUrl(params);
  }

  onView(entry: FileSystemEntry) {
    const path = FileSystemEntry.getFullName(entry);
    const params = this.dataSource.params.withAppendedPath(path);
    const url = this.restService.getFileViewUrl(params);
    window.open(url);
  }

  private updateDataSource() {
    this.dataSource.updatePagination(this.paginator);
    if (!this.viewInitialized) {
      this.dataSource.setSorting(this.defaultSortOrder, this.defaultSortColumn);
    } else {
      this.dataSource.updateSorting(this.sort);
    }
    this.dataSource.reloadDataIfNeeded();
  }

  private getNavigationExtras(entry: FileSystemEntry): NavigationExtras {
    const entryFullName = FileSystemEntry.getFullName(entry);
    const subPath = this.dataSource.params.appendPath(entryFullName);
    return {
      queryParams: {path: subPath}
    };
  }
}
