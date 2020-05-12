import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DatabaseModelService} from './model/database-model.service';

@Component({
  selector: 'app-database',
  templateUrl: './database.component.html',
  styleUrls: ['./database.component.css'],
  providers: [DatabaseModelService]
})
export class DatabaseComponent {

  constructor(
    private route: ActivatedRoute,
    public model: DatabaseModelService
  ) {
    route.url.subscribe(_ => {
      model.databaseName = decodeURIComponent(route.snapshot.params.database);
    });
  }
}
