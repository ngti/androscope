import {Component, Inject, OnInit} from '@angular/core';
import {log} from 'util';
import {BehaviorSubject} from 'rxjs';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Status, StatusData} from '../../common/base/status.enum';
import {RestService} from '../../common/rest/rest.service';
import {Uri} from '../../common/query-model/uri';

@Component({
  selector: 'app-database-upload',
  templateUrl: './database-upload.component.html',
  styleUrls: ['./database-upload.component.css']
})
export class DatabaseUploadComponent implements OnInit {

  private fileSubject = new BehaviorSubject<File>(null);
  readonly file$ = this.fileSubject.asObservable();

  private uploadStatusSubject = new BehaviorSubject<StatusData>(new StatusData());
  readonly uploadStatus$ = this.uploadStatusSubject.asObservable();

  constructor(
    private restService: RestService,
    private dialogRef: MatDialogRef<DatabaseUploadComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly dbUri: Uri
  ) {
  }

  ngOnInit() {
  }

  handleFileInput(target) {
    const files = target.files;
    if (files.length > 0) {
      this.fileSubject.next(files[0]);
      log('handleFileInput: ' + this.fileSubject.getValue().name);
    } else {
      this.fileSubject.next(null);
      log('handleFileInput: file cleared');
    }
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  onUploadClick() {
    const file = this.fileSubject.getValue();
    this.uploadStatusSubject.next(new StatusData(Status.IN_PROGRESS));
    this.restService.uploadDatabase(this.dbUri, file).subscribe({
      next: result => {
        if (result.success) {
          this.uploadStatusSubject.next(new StatusData(Status.SUCCESS));
          this.dialogRef.close(true);
        } else {
          this.uploadStatusSubject.next(new StatusData(Status.ERROR, result.message));
        }
      },
      error: err => {
        this.uploadStatusSubject.next(new StatusData(Status.ERROR, err.message));
      }
    });
  }
}
