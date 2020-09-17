import {Component, OnDestroy} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import {DatabaseInfo} from '../../common/rest/database-data';
import {DatabaseModelService} from '../model/database-model.service';
import {MatDialog} from '@angular/material/dialog';
import {DatabaseUploadComponent} from '../database-upload/database-upload.component';
import {Uri} from '../../common/query-model/uri';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-database-metadata',
  templateUrl: './database-metadata.component.html',
  styleUrls: ['./database-metadata.component.css']
})
export class DatabaseMetadataComponent implements OnDestroy {

  private databaseInfoSubject = new BehaviorSubject<DatabaseInfo>(null);
  databaseInfo$ = this.databaseInfoSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private uriSubscription: Subscription;

  constructor(
    private restService: RestService,
    readonly model: DatabaseModelService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.uriSubscription = model.uri$.subscribe(databaseUri => {
      this.reloadData(databaseUri);
    });
  }

  get databaseDownloadUrl(): string {
    return this.restService.getDatabaseDownloadUrl(this.model.uri);
  }

  get isDatabaseValid(): boolean {
    const info = this.databaseInfoSubject.getValue()
    return info != null && info.valid
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  onDatabaseUpload() {
    const dialogRef = this.dialog.open(DatabaseUploadComponent, {
      data: this.model.uri
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result != null && result) {
        this.snackBar.open('Database has been successfully uploaded', null, {
          duration: 2000,
        });
        this.reloadData(this.model.uri);
      }
    });
  }

  private reloadData(uri: Uri) {
    this.loadingSubject.next(true);
    this.restService.getDatabaseInfo(uri).subscribe(info => {
      this.loadingSubject.next(false);
      this.databaseInfoSubject.next(info);
    });
  }
}
