import {Component, OnDestroy, OnInit} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {log} from 'util';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-database-query-data',
  templateUrl: './database-query-data.component.html',
  styleUrls: ['./database-query-data.component.css']
})
export class DatabaseQueryDataComponent implements OnInit, OnDestroy {

  constructor(
    readonly model: DatabaseModelService,
    route: ActivatedRoute
  ) {
    log('DatabaseQueryDataComponent created');
    route.url.subscribe(() => {
      const query = decodeURIComponent(route.snapshot.params.query);
      model.setDatabaseQuery('query', query);
      log('DatabaseQueryDataComponent new query: ' + query);
    });
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.model.clearDatabaseQuery();
    log('DatabaseQueryDataComponent destroyed');
  }

}
