import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DatabaseInfo} from '../../common/rest/database-data';
import {log} from "util";
import {MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'app-database-info',
  templateUrl: './database-info.component.html',
  styleUrls: ['./database-info.component.css']
})
export class DatabaseInfoComponent implements OnInit {

  @Input('data')
  databaseInfo: DatabaseInfo;

  @ViewChild(MatMenuTrigger, {static: false}) trigger: MatMenuTrigger;

  constructor() { }

  ngOnInit() {
  }

  onMenuButtonClick($event: MouseEvent) {
    $event.preventDefault();
    $event.stopImmediatePropagation();
  }

  onTableMouseUp($event: MouseEvent) {
    log('onTableMouseUp ' + $event.button);
  }

  onTableClick($event: MouseEvent) {
    log('onTableClick');
  }

  onTableMouseDown($event: MouseEvent) {
    log('onTableMouseDown ' + $event.button);
    if ($event.button === 2) {
      this.trigger.openMenu();
    }
  }
}
