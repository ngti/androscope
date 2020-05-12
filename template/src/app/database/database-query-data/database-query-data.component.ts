import {Component, OnDestroy} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-database-query-data',
  templateUrl: './database-query-data.component.html',
  styleUrls: ['./database-query-data.component.css']
})
export class DatabaseQueryDataComponent implements OnDestroy {

  constructor(
    readonly model: DatabaseModelService,
    route: ActivatedRoute
  ) {
    route.url.subscribe(() => {
      const query = decodeURIComponent(route.snapshot.params.query);
      model.setDatabaseQuery('query', query);
    });
  }

  ngOnDestroy(): void {
    this.model.clearDatabaseQuery();
  }

}
