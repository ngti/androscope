import {Component, OnDestroy, OnInit} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute, Router} from '@angular/router';
import {log} from 'util';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-database-query',
  templateUrl: './database-query.component.html',
  styleUrls: ['./database-query.component.css']
})
export class DatabaseQueryComponent implements OnInit, OnDestroy {

  query: string = null;

  private uriSubscription: Subscription;

  constructor(
    readonly model: DatabaseModelService,
    private router: Router,
    private route: ActivatedRoute
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
    this.router.navigate([encodeURIComponent(newQuery)], {
      relativeTo: this.route
    });
  }
}
