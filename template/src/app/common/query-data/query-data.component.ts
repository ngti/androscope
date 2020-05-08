import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {QueryModelService} from '../query-model/query-model.service';
import {RestService} from '../rest/rest.service';
import {QueryDataSource} from './query-data-source';
import {BehaviorSubject, merge, Subscription} from 'rxjs';
import {Uri} from '../query-model/uri';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';

@Component({
  selector: 'app-query-data',
  templateUrl: './query-data.component.html',
  styleUrls: ['./query-data.component.css']
})
export class QueryDataComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<[]>;
  dataSource: QueryDataSource;

  readonly defaultPageSize = QueryDataSource.DEFAULT_PAGE_SIZE;

  @Input('queryModel')
  model: QueryModelService<Uri>;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  readonly loading$ = this.loadingSubject.asObservable();

  private errorSubject = new BehaviorSubject<string>(null);
  readonly error$ = this.errorSubject.asObservable();

  private uriSubscription: Subscription;

  constructor(
    private restService: RestService
  ) {
  }

  get showTable(): boolean {
    return this.dataSource != null && this.dataSource.showTable;
  }

  ngOnInit() {
    this.uriSubscription = this.model.uri$.subscribe(newUri => {
      // Reset pagination & sorting
      if (this.paginator != null) {
        this.paginator.pageIndex = 0;
      }
      if (this.sort != null) {
        this.sort.active = null;
        this.sort.direction = '';
      }

      this.dataSource = new QueryDataSource(
        this.restService,
        newUri,
        this.loadingSubject,
        this.errorSubject
      );
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  ngAfterViewInit(): void {
    merge(this.paginator.page, this.sort.sortChange).subscribe(() => {
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.updateSorting(this.sort);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

}
