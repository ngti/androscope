import {Component, Input} from '@angular/core';
import {DatabaseInfo} from '../../common/rest/database-data';

@Component({
  selector: 'app-database-info',
  templateUrl: './database-info.component.html',
  styleUrls: ['./database-info.component.css']
})
export class DatabaseInfoComponent {

  @Input('data')
  databaseInfo: DatabaseInfo;

  constructor() { }

  onMenuButtonClick($event: MouseEvent) {
    $event.preventDefault();
    $event.stopImmediatePropagation();
  }
}
