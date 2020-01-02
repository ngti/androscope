import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {QueryModelService} from '../query-model/query-model.service';
import {MatPaginator, MatSort, MatTable} from '@angular/material';
import {RestService} from '../rest/rest.service';
import {QueryDataSource} from './query-data-source';
import {merge, Subscription} from 'rxjs';

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

  private uriSubscription: Subscription;

  constructor(
    private model: QueryModelService,
    private restService: RestService
  ) {
    console.log('QueryDataComponent created');
  }

  ngOnInit() {
    this.uriSubscription = this.model.uriObserver.subscribe(newUri => {
      console.log('New uri ' + newUri.content);
      this.dataSource = new QueryDataSource(this.restService, newUri);
      if (this.paginator != null) {
        this.paginator.pageIndex = 0;
      }
      if (this.sort != null) {
        this.sort.active = null;
        this.sort.direction = '';
      }
      this.reloadData();
    });
  }

  ngAfterViewInit(): void {
    merge(this.paginator.page, this.sort.sortChange).subscribe(() => {
      console.log(`Page: ${this.paginator.pageIndex}, size: ${this.paginator.pageSize}, sort column:
      ${this.sort.active}, sort direction: ${this.sort.direction}`);

      this.reloadData();
    });

    this.reloadData();
  }

  private reloadData() {
    if (this.paginator != null && this.sort != null) {
      this.dataSource.loadData(
        this.paginator.pageSize,
        this.paginator.pageIndex,
        this.sort.direction,
        this.sort.active
      );
    }
  }

  ngOnDestroy(): void {
    console.log('QueryDataComponent - onDestroy');
    this.uriSubscription.unsubscribe();
  }

}
