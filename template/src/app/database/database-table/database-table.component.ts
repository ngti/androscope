import {Component, OnDestroy} from '@angular/core';
import {DatabaseModelService} from '../model/database-model.service';
import {ActivatedRoute} from '@angular/router';
import {DatabaseViewSqlComponent, DatabaseViewSqlDialogData} from '../database-view-sql/database-view-sql.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-database-table',
  templateUrl: './database-table.component.html',
  styleUrls: ['./database-table.component.css']
})
export class DatabaseTableComponent implements OnDestroy {

  tableName: string;

  constructor(
    readonly model: DatabaseModelService,
    private dialog: MatDialog,
    route: ActivatedRoute
  ) {
    route.url.subscribe(() => {
      this.tableName = decodeURIComponent(route.snapshot.params.table);
      model.setDatabaseTable(this.tableName);
    });
  }

  ngOnDestroy(): void {
    this.model.clearDatabaseTable();
  }

  onViewSql(tableName: string) {
    this.dialog.open(DatabaseViewSqlComponent, {
      data: new DatabaseViewSqlDialogData(this.model.uri, tableName)
    });
  }
}
