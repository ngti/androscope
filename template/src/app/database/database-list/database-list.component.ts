import {AfterViewInit, Component, OnInit} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {BehaviorSubject} from 'rxjs';
import {Database} from '../../common/rest/database-data';

@Component({
  selector: 'app-database-list',
  templateUrl: './database-list.component.html',
  styleUrls: ['./database-list.component.css']
})
export class DatabaseListComponent implements OnInit {

  private databaseListSubject = new BehaviorSubject<Database[]>([]);
  databaseList$ = this.databaseListSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(true);
  loading$ = this.loadingSubject.asObservable();

  constructor(private restService: RestService) {
  }

  ngOnInit(): void {
    this.restService.getDatabaseList().subscribe(list => {
      this.databaseListSubject.next(list);
      this.loadingSubject.next(false);
    });
  }

  getDbPath(database: Database): string {
    if (database.error != null) {
      return null;
    }
    return './database/' + encodeURIComponent(database.name);
  }
}
