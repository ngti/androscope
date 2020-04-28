import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {QueryModelService} from '../query-model/query-model.service';
import {MatPaginator, MatSort, MatTable} from '@angular/material';
import {RestService} from '../rest/rest.service';
import {QueryDataSource} from './query-data-source';
import {BehaviorSubject, merge, Subscription} from 'rxjs';
import {Uri} from '../query-model/uri';

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

  defaultPageSize = QueryDataSource.DEFAULT_PAGE_SIZE;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private errorSubject = new BehaviorSubject<string>(null);
  error$ = this.errorSubject.asObservable();

  private uriSubscription: Subscription;

  @Input('queryModel')
  model: QueryModelService<Uri>;

  constructor(
    private restService: RestService
  ) {
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

  get showTable(): boolean {
    return this.dataSource != null && this.dataSource.showTable;
  }

}
