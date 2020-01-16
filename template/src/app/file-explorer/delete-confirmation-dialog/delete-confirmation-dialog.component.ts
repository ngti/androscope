import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatButton, MatDialogRef} from '@angular/material';
import {FileSystemEntry} from '../../common/rest/file-system-data';
import {FileSystemType, RestService} from '../../common/rest/rest.service';
import {BehaviorSubject} from 'rxjs';
import {FileExplorerDataSource} from '../file-explorer-datasource';

export class DeleteConfirmationDialogData {
  constructor(
    public fileSystemType: FileSystemType,
    public entry: FileSystemEntry,
    public parentPath: string
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
    public dialogRef: MatDialogRef<DeleteConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: DeleteConfirmationDialogData
  ) {
  }

  getEntryName(): string {
    const entry = this.data.entry;
    let name = entry.name;
    if (entry.extension != null) {
      name += '.' + entry.extension;
    }
    return name;
  }

  getEntryType(): string {
    if (this.data.entry.isFolder) {
      return 'folder';
    }
    return 'file';
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  onDeleteClick() {
    this.clearError();
    this.deleteButton.disabled = true;
    const entryName = this.getEntryName();
    const fullPath = FileExplorerDataSource.concatPaths(this.data.parentPath, entryName);
    this.restService.deleteFile(this.data.fileSystemType, fullPath)
      .subscribe({
        next: value => {
          this.clearError();
          this.dialogRef.close(entryName);
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
