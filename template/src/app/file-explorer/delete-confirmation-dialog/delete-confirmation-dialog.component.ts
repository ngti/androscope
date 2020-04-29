import {Component, Inject, ViewChild} from '@angular/core';
import {FileSystemEntry} from '../../common/rest/file-system-data';
import {RestService} from '../../common/rest/rest.service';
import {BehaviorSubject} from 'rxjs';
import {FileSystemParams} from '../../common/base/file-system-params';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

export class DeleteConfirmationDialogData {
  constructor(
    public readonly parent: FileSystemParams,
    public readonly entry: FileSystemEntry
  ) {
  }
}

@Component({
  selector: 'app-delete-confirmation-dialog',
  templateUrl: './delete-confirmation-dialog.component.html',
  styleUrls: ['./delete-confirmation-dialog.component.css']
})
export class DeleteConfirmationDialogComponent {

  @ViewChild('deleteButton', {static: false}) deleteButton: MatButton;

  private errorSubject = new BehaviorSubject<string>(null);
  $error = this.errorSubject.asObservable();

  constructor(
    private restService: RestService,
    private dialogRef: MatDialogRef<DeleteConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: DeleteConfirmationDialogData
  ) {
  }

  getEntryName(): string {
    return FileSystemEntry.getFullName(this.data.entry);
  }

  getEntryType(): string {
    if (this.data.entry.isFolder) {
      return 'folder';
    }
    return 'file';
  }

  onDeleteClick() {
    this.clearError();
    this.deleteButton.disabled = true;
    const entryName = this.getEntryName();
    const params = this.data.parent.withAppendedPath(entryName);
    this.restService.deleteFile(params)
      .subscribe({
        next: value => {
          if (value.success) {
            this.clearError();
            this.dialogRef.close(entryName);
          } else {
            this.reportError(value.message);
          }
        },
        error: err => {
          this.reportError(err.message);
        }
      });
  }

  private clearError() {
    this.errorSubject.next(null);
  }

  private reportError(msg: string) {
    this.errorSubject.next(msg);
    this.deleteButton.disabled = false;
  }
}
