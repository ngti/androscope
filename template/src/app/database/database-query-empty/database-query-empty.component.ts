import { Component, OnInit } from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {log} from 'util';

@Component({
  selector: 'app-database-query-empty',
  templateUrl: './database-query-empty.component.html',
  styleUrls: ['./database-query-empty.component.css']
})
export class DatabaseQueryEmptyComponent implements OnInit {

  constructor(model: DatabaseModelService) {
    log('DatabaseQueryEmptyComponent created');
    model.clearDatabaseQuery();
  }

  ngOnInit() {
  }

}
