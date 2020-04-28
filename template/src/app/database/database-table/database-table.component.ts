import { Component, OnInit } from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute} from '@angular/router';
import {log} from 'util';

@Component({
  selector: 'app-database-table',
  templateUrl: './database-table.component.html',
  styleUrls: ['./database-table.component.css']
})
export class DatabaseTableComponent implements OnInit {

  tableName: string;

  constructor(
    readonly model: DatabaseModelService,
    route: ActivatedRoute
  ) {
    log('DatabaseTableComponent created');
    route.url.subscribe(() => {
      this.tableName = decodeURIComponent(route.snapshot.params.table);
      model.setDatabaseQuery('table', this.tableName);
      log('DatabaseTableComponent new table: ' + this.tableName);
    });
  }

  ngOnInit() {
  }

}
