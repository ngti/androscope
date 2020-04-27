import { Component, OnInit } from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {Uri} from "../../common/query-model/uri";
import {ActivatedRoute} from "@angular/router";
import {BehaviorSubject} from "rxjs";
import {DatabaseInfo} from "../../common/rest/database-data";

@Component({
  selector: 'app-database-metadata',
  templateUrl: './database-metadata.component.html',
  styleUrls: ['./database-metadata.component.css']
})
export class DatabaseMetadataComponent implements OnInit {

  private databaseInfoSubject = new BehaviorSubject<DatabaseInfo>(null);
  databaseInfo$ = this.databaseInfoSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  constructor(restService: RestService, route: ActivatedRoute) {
    route.url.subscribe(newUrl => {
      const database = route.snapshot.params.database;
      this.loadingSubject.next(true);
      restService.getDatabaseInfo(database).subscribe(info => {
        this.loadingSubject.next(false);
        this.databaseInfoSubject.next(info);
      });
    });
  }

  ngOnInit() {
  }

}
