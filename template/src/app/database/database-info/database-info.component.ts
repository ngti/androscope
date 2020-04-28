import {Component, Input, OnInit} from '@angular/core';
import {DatabaseInfo} from '../../common/rest/database-data';
import {log} from "util";

@Component({
  selector: 'app-database-info',
  templateUrl: './database-info.component.html',
  styleUrls: ['./database-info.component.css']
})
export class DatabaseInfoComponent implements OnInit {

  @Input('data')
  databaseInfo: DatabaseInfo;

  constructor() { }

  ngOnInit() {
  }

  onMenuButtonClick($event: MouseEvent) {
    $event.preventDefault();
    $event.stopImmediatePropagation();
  }

  onTableMouseUp($event: MouseEvent) {
    log('onTableMouseUp');
  }

  onTableClick($event: MouseEvent) {
    log('onTableClick');
  }
}
