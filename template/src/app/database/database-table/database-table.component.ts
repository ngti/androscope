import { Component, OnInit } from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute} from '@angular/router';
import {Uri} from "../../common/query-model/uri";
import {log} from "util";

@Component({
  selector: 'app-database-table',
  templateUrl: './database-table.component.html',
  styleUrls: ['./database-table.component.css']
})
export class DatabaseTableComponent implements OnInit {

  constructor(
    readonly model: DatabaseModelService,
    route: ActivatedRoute
  ) {
    log('DatabaseTableComponent created');
    route.url.subscribe(() => {
      const tableName = decodeURIComponent(route.snapshot.params.table);
      model.setDatabaseQuery('table', tableName);
      log('DatabaseTableComponent new table: ' + tableName);
    });
  }

  ngOnInit() {
  }

}
