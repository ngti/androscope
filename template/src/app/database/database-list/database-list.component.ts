import {AfterViewInit, Component, OnInit} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {BehaviorSubject} from 'rxjs';
import {Database} from '../../common/rest/database-data';

@Component({
  selector: 'app-database-list',
  templateUrl: './database-list.component.html',
  styleUrls: ['./database-list.component.css']
})
export class DatabaseListComponent implements AfterViewInit, OnInit {

  private databaseListSubject = new BehaviorSubject<Database[]>([]);
  databaseList$ = this.databaseListSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(true);
  loading$ = this.loadingSubject.asObservable();

  constructor(restService: RestService) {
    restService.getDatabaseList().subscribe(list => {
      this.databaseListSubject.next(list);
      this.loadingSubject.next(false);
    });
  }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
  }

  getDbPath(database: string) {
    return encodeURIComponent(database);
  }
}
