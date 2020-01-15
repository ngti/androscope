import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {FileExplorerDataSource} from './file-explorer-datasource';
import {RestService} from '../common/rest/rest.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FileSystemEntry} from '../common/rest/file-system-data';
import {BehaviorSubject, merge} from 'rxjs';

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

  displayedColumns = ['type', 'name', 'extension', 'date', 'size'];

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  constructor(
    private restService: RestService,
    private route: ActivatedRoute,
    private router: Router
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

  onMouseOver(entry: FileSystemEntry) {
    if (entry.isFolder) {
      entry.hovered = true;
    }
  }

  onMouseOut(entry: FileSystemEntry) {
    if (entry.isFolder) {
      entry.hovered = false;
    }
  }

  onMouseClick(entry: FileSystemEntry, event: MouseEvent) {
    if (entry.isFolder) {
      const subPath = this.dataSource.getSubPath(entry.name);
      const navigationExtras = {
        queryParams: {path: subPath}
      };
      if (event.button === 1) {
        const url = this.router.createUrlTree([], navigationExtras).toString();
        window.open(url);
      } else {
        this.router.navigate([], navigationExtras);
      }
    }
  }
}
