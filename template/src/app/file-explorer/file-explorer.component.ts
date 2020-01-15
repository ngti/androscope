import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {FileExplorerDataSource} from './file-explorer-datasource';
import {RestService} from '../common/rest/rest.service';
import {ActivatedRoute} from '@angular/router';
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
    private route: ActivatedRoute
  ) {
    route.url.subscribe(newUrl => {
      console.log(`FileExplorerComponent new url: ${newUrl}`);
      // TODO
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      console.log(`FileExplorerComponent new params: ${params.type}, ${params.path}`);
      this.dataSource = new FileExplorerDataSource(this.restService, params.type, params.path, this.loadingSubject);
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  ngAfterViewInit() {
    merge(this.paginator.page, this.sort.sortChange).subscribe(() => {
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.updateSorting(this.sort);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  getIcon(entry: FileSystemEntry): string {
    if (entry.isFolder) {
      return 'folder';
    }
    return 'file';
  }
}
