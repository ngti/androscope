import {Component, Inject, OnInit} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {Uri} from '../../common/query-model/uri';

export class DatabaseViewSqlDialogData {
  constructor(
    readonly dbUri: Uri,
    readonly name: string
  ) {
  }
}

@Component({
  selector: 'app-database-view-sql',
  templateUrl: './database-view-sql.component.html',
  styleUrls: ['./database-view-sql.component.css']
})
export class DatabaseViewSqlComponent implements OnInit {

  private loadingSubject = new BehaviorSubject<boolean>(false);
  readonly loading$ = this.loadingSubject.asObservable();

  private sqlSubject = new BehaviorSubject<string>(null);
  readonly sql$ = this.sqlSubject.asObservable();

  constructor(
    private restService: RestService,
    @Inject(MAT_DIALOG_DATA) readonly data: DatabaseViewSqlDialogData
  ) {
  }

  ngOnInit() {
    this.loadingSubject.next(true);
    this.restService.getDatabaseSql(this.data.dbUri, this.data.name)
      .subscribe({
        next: sql => {
          this.loadingSubject.next(false);
          this.sqlSubject.next(sql);
        },
        error: err => {
          this.loadingSubject.next(false);
          this.sqlSubject.next('Error retrieving sql: ' + err.message);
        }
      });
  }
}
