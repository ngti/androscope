import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForm} from '@angular/forms';
import {Uri} from './uri';
import {MatPaginator, MatSort, MatTable} from '@angular/material';
import {
  ContentProviderContentDatasource,
  ContentProviderContentItem
} from '../content-provider-content/content-provider-content-datasource';

@Component({
  selector: 'app-query-content-provider',
  templateUrl: './query-content-provider.component.html',
  styleUrls: ['./query-content-provider.component.css']
})
export class QueryContentProviderComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ContentProviderContentItem>;
  dataSource: ContentProviderContentDatasource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name'];

  uri: Uri;

  constructor(activeRoute: ActivatedRoute,
              private router: Router) {

    let uriContent = activeRoute.snapshot.params.uri;
    if (uriContent != null) {
      uriContent = decodeURIComponent(uriContent);
    } else {
      uriContent = '';
    }
    this.uri = new Uri(uriContent);
  }

  submitUri(newUri: Uri) {
    this.router.navigate([encodeURIComponent(newUri.content.trim())]);
  }

  ngOnInit() {
    this.dataSource = new ContentProviderContentDatasource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

}
