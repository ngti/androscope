import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DatabaseModelService} from './model/database-model.service';

@Component({
  selector: 'app-database',
  templateUrl: './database.component.html',
  styleUrls: ['./database.component.css']
})
export class DatabaseComponent {

  constructor(
    private route: ActivatedRoute,
    public model: DatabaseModelService
  ) {
    console.log('DatabaseComponent created');

    route.url.subscribe(newUrl => {
      console.log('newUrl: ' + newUrl);

      const name = decodeURIComponent(route.snapshot.params.database);
      model.databaseName = name;
      console.log('DatabaseComponent databaseName: ' + name);
    });
  }
}
