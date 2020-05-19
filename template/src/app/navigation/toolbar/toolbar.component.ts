import {Component, OnInit} from '@angular/core';
import {RestService} from '../../common/rest/rest.service';
import {BehaviorSubject} from 'rxjs';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  private appNameSubject = new BehaviorSubject<string>('...');
  readonly appName$ = this.appNameSubject.asObservable();

  constructor(private restService: RestService) {
  }

  ngOnInit(): void {
    this.restService.getAppName().subscribe(name => {
      this.appNameSubject.next(name);
    });
  }
}
