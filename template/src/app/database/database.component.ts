import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {QueryModelService} from '../common/query-model/query-model.service';
import {Uri} from '../common/query-model/uri';

@Component({
  selector: 'app-database',
  templateUrl: './database.component.html',
  styleUrls: ['./database.component.css']
})
export class DatabaseComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public model: QueryModelService
  ) {
    console.log('DatabaseComponent created');

    // route.url.subscribe(newUrl => {
    //   console.log('newUrl: ' + newUrl);
    //
    //   let uriContent = route.snapshot.params.uri;
    //   console.log('uriContent: ' + uriContent);
    //   if (uriContent != null) {
    //     uriContent = decodeURIComponent(uriContent);
    //   } else {
    //     uriContent = '';
    //   }
    //   model.uri = new Uri(uriContent);
    // });
  }

  ngOnInit() {
  }

}
