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
import {isString} from 'util';

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

  private breadcrumbsSubject = new BehaviorSubject<Breadcrumb[]>(null);
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
      const newFileSystem = snapshot.params.type;
      const newPath = snapshot.queryParams.path;
      console.log(`FileExplorerComponent new params: ${newFileSystem}, ${newPath}`);
      if (this.dataSource == null
        || this.dataSource.fileSystemType !== newFileSystem
        || this.dataSource.path !== newPath) {
        this.dataSource = new FileExplorerDataSource(
          this.restService, newFileSystem, snapshot.queryParams.path, this.loadingSubject, this.breadcrumbsSubject);
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

  onBreadcrumbClick(entry: Breadcrumb) {
    this.openFolder(this.getBreadcrumbNavigationExtras(entry));
  }

  onBreadcrumbUp(entry: Breadcrumb, event: MouseEvent) {
    this.openFolderInNewWindow(event, () => {
      return this.getBreadcrumbNavigationExtras(entry);
    });
  }

  private openFolder(extras: NavigationExtras) {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([], extras);
  }

  private openFolderInNewWindow(event: MouseEvent, extrasSupplier: () => NavigationExtras) {
    if (event.button === 1) {
      const url = this.router.createUrlTree([], extrasSupplier()).toString();
      window.open(url);
    }
  }

  onDelete(entry: FileSystemEntry) {
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      data: new DeleteConfirmationDialogData(
        this.dataSource.fileSystemType,
        entry,
        this.dataSource.path)
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
    const path = this.dataSource.getSubPath(entry);
    window.location.href = this.restService.getFileDownloadUrl(this.dataSource.fileSystemType, path);
  }

  onView(entry: FileSystemEntry) {
    const path = this.dataSource.getSubPath(entry);
    const url = this.restService.getFileViewUrl(this.dataSource.fileSystemType, path);
    window.open(url);
  }

  private getBreadcrumbNavigationExtras(breadcrumb: Breadcrumb): NavigationExtras {
    if (breadcrumb.path.length === 0) {
      return {};
    }
    return this.getNavigationExtras(breadcrumb.path);
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

  private getNavigationExtras(entry: FileSystemEntry | string): NavigationExtras {
    let subPath: string;
    if (isString(entry)) {
      subPath = entry as string;
    } else {
      subPath = this.dataSource.getSubPath(entry as FileSystemEntry);
    }
    return {
      queryParams: {path: subPath}
    };
  }
}
