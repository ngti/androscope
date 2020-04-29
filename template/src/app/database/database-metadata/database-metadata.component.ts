import {Component, OnDestroy, OnInit} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, Subscription} from 'rxjs';
import {DatabaseInfo} from '../../common/rest/database-data';
import {DatabaseModelService} from '../model/database-model.service';

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
    readonly model: DatabaseModelService
  ) {
    console.log('DatabaseMetadataComponent created');

    this.uriSubscription = model.uri$.subscribe(databaseUri => {
      console.log('DatabaseMetadataComponent new database uri: ' + databaseUri.content);

      this.loadingSubject.next(true);
      restService.getDatabaseInfo(databaseUri).subscribe(info => {
        this.loadingSubject.next(false);
        this.databaseInfoSubject.next(info);
      });
    });
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  get databaseDownloadUrl(): string {
    return this.restService.getDatabaseDownloadUrl(this.model.uri);
  }
}
