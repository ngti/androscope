import {Component, OnDestroy, OnInit} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute, Router} from '@angular/router';
import {log} from 'util';
import {BehaviorSubject, Subscription} from 'rxjs';
import {RestService} from '../../common/rest/rest.service';

enum QueryStatus {
  IDLE,
  IN_PROGRESS,
  SUCCESS,
  ERROR
}

export class QueryData {

  constructor(
    private readonly status: QueryStatus = QueryStatus.IDLE,
    readonly message: string = null
  ) {
  }

  get display(): boolean {
    return this.status !== QueryStatus.IDLE;
  }

  get alertClass(): string {
    switch (this.status) {
      case QueryStatus.IN_PROGRESS:
        return 'alert-primary';
      case QueryStatus.SUCCESS:
        return 'alert-success';
      case QueryStatus.ERROR:
        return 'alert-danger';
      case QueryStatus.IDLE:
        return null;
    }
  }
}

@Component({
  selector: 'app-database-query',
  templateUrl: './database-query.component.html',
  styleUrls: ['./database-query.component.css']
})
export class DatabaseQueryComponent implements OnInit, OnDestroy {

  query: string = null;

  private uriSubscription: Subscription;

  private queryResultSubject = new BehaviorSubject<QueryData>(new QueryData());
  readonly queryResult$ = this.queryResultSubject.asObservable();

  constructor(
    readonly model: DatabaseModelService,
    private router: Router,
    private route: ActivatedRoute,
    private restService: RestService
  ) {
    log('DatabaseQueryComponent created');
  }

  ngOnInit() {
    this.uriSubscription = this.model.uri$.subscribe(() => {
      this.query = this.model.queryValue;
    });
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  submitQuery(newQuery: string) {
    log('Submit: ' + newQuery);

    this.restService.canQuery(newQuery).subscribe(canQuery => {
      log('DatabaseQueryComponent can query: ' + newQuery + ' -> ' + canQuery);
      if (canQuery) {
        this.queryResultSubject.next(new QueryData());
        this.router.navigate([encodeURIComponent(newQuery)], {
          relativeTo: this.route
        });
      } else {
        this.queryResultSubject.next(new QueryData(QueryStatus.IN_PROGRESS, 'Executing...'));
        this.restService.executeSql(this.model.uri, newQuery).subscribe(queryResult => {
          let queryStatus: QueryStatus;
          if (queryResult.success) {
            queryStatus = QueryStatus.SUCCESS;
          } else {
            queryStatus = QueryStatus.ERROR;
          }
          this.queryResultSubject.next(new QueryData(queryStatus, queryResult.message));
        });
      }
    });
  }
}
