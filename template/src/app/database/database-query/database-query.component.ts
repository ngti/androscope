import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute, Router} from '@angular/router';
import {BehaviorSubject, Subscription} from 'rxjs';
import {RestService} from '../../common/rest/rest.service';
import {Status, StatusData} from '../../common/base/status.enum';
import {delay} from 'rxjs/operators';

@Component({
  selector: 'app-database-query',
  templateUrl: './database-query.component.html',
  styleUrls: ['./database-query.component.css']
})
export class DatabaseQueryComponent implements AfterViewInit, OnDestroy {

  query: string = null;

  private uriSubscription: Subscription;

  private queryStatusSubject = new BehaviorSubject<StatusData>(new StatusData());
  readonly queryStatus$ = this.queryStatusSubject.asObservable();

  @ViewChild('sqlInput', {static: false})
  private sqlInput;

  constructor(
    readonly model: DatabaseModelService,
    private router: Router,
    private route: ActivatedRoute,
    private restService: RestService
  ) {
  }

  ngAfterViewInit(): void {
    this.uriSubscription = this.model.uri$.pipe(
      // To prevent "Expression has changed after it was checked", idea taken from
      // https://blog.angular-university.io/angular-debugging/
      delay(0)
    ).subscribe(() => {
      this.query = this.model.queryValue;
      this.sqlInput.nativeElement.focus();
    });
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  submitQuery(newQuery: string) {
    this.restService.canQuery(newQuery).subscribe(canQuery => {
      if (canQuery) {
        this.queryStatusSubject.next(new StatusData());
        this.router.navigate([encodeURIComponent(newQuery)], {
          relativeTo: this.route
        }).then(r => {
            if (r == null) {
              // Query didn't change, force update its data by updating model Uri
              this.model.forceUpdateUri();
            }
        }
        );
      } else {
        this.model.setDatabaseQuery(newQuery);
        this.queryStatusSubject.next(new StatusData(Status.IN_PROGRESS, 'Executing...'));
        this.restService.executeSql(this.model.uri, newQuery).subscribe(queryResult => {
          let queryStatus: Status;
          if (queryResult.success) {
            queryStatus = Status.SUCCESS;
          } else {
            queryStatus = Status.ERROR;
          }
          this.queryStatusSubject.next(new StatusData(queryStatus, queryResult.message));
        });
      }
    });
  }

  onTextKeyDown($event: KeyboardEvent) {
    if ($event.ctrlKey && $event.key === 'Enter') {
      this.submitQuery(this.query);
    }
  }
}
