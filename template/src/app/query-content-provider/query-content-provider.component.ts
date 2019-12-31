import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, NavigationExtras, Router} from '@angular/router';
import {NgForm} from '@angular/forms';
import {Uri} from './uri';
import {MatPaginator, MatSort, MatTable} from '@angular/material';
import {
  ContentProviderContentDataSource
} from './content-provider-content-data-source';
import {RestService} from './rest.service';

@Component({
  selector: 'app-query-content-provider',
  templateUrl: './query-content-provider.component.html',
  providers: [ RestService ],
  styleUrls: ['./query-content-provider.component.css']
})
export class QueryContentProviderComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<[]>;
  dataSource: ContentProviderContentDataSource;

  uri: Uri;

  constructor(activeRoute: ActivatedRoute,
              private router: Router,
              private restService: RestService) {

    console.log('QueryContentProviderComponent created');
    let uriContent = activeRoute.snapshot.params.uri;
    if (uriContent != null) {
      uriContent = decodeURIComponent(uriContent);
    } else {
      uriContent = '';
    }
    this.uri = new Uri(uriContent);
  }

  submitUri(newUri: Uri) {
    console.log('submitUri ' + newUri.content);
    this.router.navigate(['provider', newUri.content.trim()]);
    this.uri = newUri;
    this.recreateDataSource();
    this.updateDataSource();
  }

  ngOnInit() {
    this.recreateDataSource();
  }

  ngAfterViewInit() {
    this.updateDataSource();
  }

  private recreateDataSource() {
    this.dataSource = new ContentProviderContentDataSource(this.restService, this.uri);
  }

  private updateDataSource() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

}
