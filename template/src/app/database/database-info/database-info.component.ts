import {Component, Input} from '@angular/core';
import {DatabaseInfo} from '../../common/rest/database-data';
import {MatDialog} from '@angular/material/dialog';
import {DatabaseViewSqlComponent, DatabaseViewSqlDialogData} from '../database-view-sql/database-view-sql.component';
import {DatabaseModelService} from '../model/database-model.service';

@Component({
  selector: 'app-database-info',
  templateUrl: './database-info.component.html',
  styleUrls: ['./database-info.component.css']
})
export class DatabaseInfoComponent {

  @Input('data')
  databaseInfo: DatabaseInfo;

  constructor(
    private model: DatabaseModelService,
    private dialog: MatDialog
  ) { }

  onMenuButtonClick($event: MouseEvent) {
    $event.preventDefault();
    $event.stopImmediatePropagation();
  }

  onViewSql(name: string) {
    this.dialog.open(DatabaseViewSqlComponent, {
      data: new DatabaseViewSqlDialogData(this.model.uri, name)
    });
  }
}
