import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Breadcrumb, FileExplorerDataSource} from './file-explorer-datasource';
import {RestService} from '../common/rest/rest.service';
import {ActivatedRoute, NavigationExtras, Router} from '@angular/router';
import {FileSystemEntry} from '../common/rest/file-system-data';
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

  breadcrumbs: Breadcrumb[] = [];

  defaultPageSize = FileExplorerDataSource.DEFAULT_PAGE_SIZE;

  displayedColumns = ['type', 'name', 'extension', 'date', 'size', 'menu'];

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

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
    // this.route.queryParams.subscribe(queryParams => {
    merge(this.route.queryParams, this.route.params).subscribe(() => {
      const snapshot = this.route.snapshot;
      const newFileSystem = snapshot.params.type;
      const newPath = snapshot.queryParams.path;
      console.log(`FileExplorerComponent new params: ${newFileSystem}, ${newPath}`);
      if (this.dataSource == null
        || this.dataSource.fileSystemType !== newFileSystem
        || this.dataSource.path !== newPath) {
        this.dataSource = new FileExplorerDataSource(
          this.restService, newFileSystem, snapshot.queryParams.path, this.loadingSubject);
        this.dataSource.updatePagination(this.paginator);
        this.dataSource.reloadDataIfNeeded();
        this.updateBreadcrumbs();
      }
    });
  }

  ngAfterViewInit() {
    merge(this.paginator.page, this.sort.sortChange).subscribe(() => {
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.updateSorting(this.sort);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  onMouseClick(entry: FileSystemEntry) {
    if (entry.isFolder) {
      this.router.navigate([], this.getNavigationExtras(entry));
    }
  }

  onMouseUp(entry: FileSystemEntry, event: MouseEvent) {
    if (entry.isFolder && event.button === 1) {
      const url = this.router.createUrlTree([], this.getNavigationExtras(entry)).toString();
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
        this.dataSource.reloadData();
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

  private updateBreadcrumbs() {
    this.breadcrumbs = [];
    if (this.dataSource.path != null) {
      this.breadcrumbs.push(new Breadcrumb('Home', this.router.createUrlTree([]).toString()));
      let relativePath = '';
      this.dataSource.path.split('/').forEach(subPath => {
        relativePath += subPath;
        const url = this.router.createUrlTree([], this.getNavigationExtras(relativePath)).toString();
        const breadcrumb = new Breadcrumb(subPath, url);
        this.breadcrumbs.push(breadcrumb);
        relativePath += '/';
      });
    }
    console.log('breadcrumbs: ' + this.breadcrumbs);
  }
}
